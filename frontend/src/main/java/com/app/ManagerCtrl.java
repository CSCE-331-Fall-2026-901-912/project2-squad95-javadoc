package com.app;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.util.List;

import org.postgresql.core.SetupQueryRunner;

import com.app.controllers.EmployeeDAO;
import com.app.controllers.InventoryDAO;
import com.app.controllers.ItemDAO;
import com.app.models.EmployeeRow;
import com.app.models.InventoryItem;
import com.app.models.Item;
import com.app.models.Order;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;


public class ManagerCtrl {

    private final ItemDAO itemDAO = new ItemDAO();
    private final InventoryDAO inventoryDAO = new InventoryDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();


    @FXML private VBox dashboardView;
    @FXML private HBox financialsView;
    @FXML private VBox ordersView;
    @FXML private VBox employeesView;
    @FXML private VBox inventoryFullView;
    @FXML private VBox menuView;

    @FXML
    public void initialize() {
        showOnly(dashboardView);
        initializeFinancials();
        initializeEmployees();
        initializeInventory();
        initializeMenu();
        refreshEmployees();
        refreshInventory();
        refreshMenu();
    }

    private void dbError(Exception e) {
        e.printStackTrace();
        new Alert(Alert.AlertType.ERROR, "Database error: " + e.getMessage()).showAndWait();
    }

    // ---------------------------------------------------------------
    // Navigation
    // ---------------------------------------------------------------

    private void showOnly(Region view) {
        for (Region r : List.of(dashboardView, financialsView, ordersView,
                                employeesView, inventoryFullView, menuView)) {
            boolean on = (r == view);
            r.setVisible(on);
            r.setManaged(on);
        }
    }

    @FXML private void showDashboard(ActionEvent e)  { showOnly(dashboardView); }
    @FXML private void showFinancials(ActionEvent e) { showOnly(financialsView); }
    @FXML private void showOrders(ActionEvent e)     { showOnly(ordersView); }
    @FXML private void showEmployees(ActionEvent e)  { showOnly(employeesView); }
    @FXML private void showInventory(ActionEvent e)  { showOnly(inventoryFullView); }
    @FXML private void showMenu(ActionEvent e)       { showOnly(menuView); }

    @FXML
    private void switchToCashier(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cashier.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML private Button printReportButton;

    class OrdersDayRow { 
        public Date date;
        public Integer totalOrders;
        public Double totalRevenue;
        public String metSalesGoal;
    }

    @FXML private TableView<OrdersDayRow> financialsTable;
    @FXML TableColumn<OrdersDayRow, Date> finDayCol;
    @FXML TableColumn<OrdersDayRow, Integer> finTotalOrdersCol;
    @FXML TableColumn<OrdersDayRow, Double> finAmountCol;
    @FXML TableColumn<OrdersDayRow, String> finMetGoalCol;

    @FXML private Label finOrdersTotalLabel;
    @FXML private Label finRevenueTotalLabel;
    @FXML private BarChart<String, Number> finRevenueTrendChart;

    @FXML private Button xReportButton;

    // Database Queries
    private void loadOrdersToday() {
        String sql = "SELECT COUNT(id) FROM orders WHERE date = CURRENT_DATE;";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                finOrdersTotalLabel.setText(String.valueOf(rs.getInt(1)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTotalRevenue() {
        String sql = "SELECT SUM(subtotal) FROM orders;";
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
             
            if (rs.next()) {
                double totalRevenue = rs.getDouble(1);
                finRevenueTotalLabel.setText(String.format("$%,.2f", totalRevenue));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRevenueTrend() {
        String sql = "SELECT TO_CHAR(date, 'Mon') AS month, SUM(subtotal) AS revenue " +
                     "FROM orders " +
                     "GROUP BY TO_CHAR(date, 'Mon'), EXTRACT(MONTH FROM date) " +
                     "ORDER BY EXTRACT(MONTH FROM date);";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("2025 Revenue Trend");

            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(rs.getString("month"), rs.getDouble("revenue")));
            }

            finRevenueTrendChart.getData().clear();
            finRevenueTrendChart.getData().add(series);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void LoadTotalOrdersByDate() {
        // Note: make sure to keep the fields in the 
        // class and SQL statement identical
        String sql = "SELECT CAST(DATE AS date) AS date,\r\n" + //
                     "       COUNT(id) AS totalOrders,\r\n" + //
                     "       SUM(subtotal) AS totalRevenue\r\n" + //
                     "FROM orders\r\n" + //
                     "GROUP BY CAST(DATE AS date)\r\n" + //
                     "ORDER BY date DESC\r\n" + //
                     "LIMIT 365;";
                     
        try (Connection conn = Database.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            
            // TODO: move to dedicate init() function
            finDayCol.setCellValueFactory(data -> 
                new SimpleObjectProperty<>(data.getValue().date));
            finTotalOrdersCol.setCellValueFactory(data -> 
                new SimpleObjectProperty<>(data.getValue().totalOrders));
            finAmountCol.setCellValueFactory(data -> 
                new SimpleObjectProperty<>(data.getValue().totalRevenue));
            // TODO: determine if "met sales goal column" should be done
            finMetGoalCol.setCellValueFactory(data -> 
                new SimpleObjectProperty<>("No"));
            
            // TODO: either move this out of the function or use a different collection
            ObservableList<OrdersDayRow> entries = FXCollections.observableArrayList();
            
            while (rs.next()) {
                OrdersDayRow newEntry = new OrdersDayRow();
                newEntry.date = rs.getDate("date");
                newEntry.totalOrders = rs.getInt("totalOrders");
                newEntry.totalRevenue = rs.getDouble("totalRevenue");
                entries.add(newEntry);
            }

            financialsTable.getItems().setAll(entries);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class HourlySalesSummary {
        public Integer hour;
        public Integer transactionCount;
        public Integer itemsSold;
        public Double totalRevenue;
        public Double averageRevenue;
    }

    class HourlySalesSummaryTable {
        private ObservableList<HourlySalesSummary> entries = FXCollections.observableArrayList();
        private TableView<HourlySalesSummary> view;

        HourlySalesSummaryTable() {
            view = new TableView<HourlySalesSummary>();

            var hourCol = new TableColumn<HourlySalesSummary, Integer>("Hour");
            var transactionsCol = new TableColumn<HourlySalesSummary, Integer>("Transactions");
            var itemsSoldCol = new TableColumn<HourlySalesSummary, Integer>("Items sold");
            var totalRevenueCol = new TableColumn<HourlySalesSummary, Double>("Total Revenue");
            var avgRevenueCol = new TableColumn<HourlySalesSummary, Double>("Avg. Revenue Per Transaction");
            
            hourCol.setCellValueFactory(data -> 
                new SimpleObjectProperty<>(data.getValue().hour));
            transactionsCol.setCellValueFactory(data -> 
                new SimpleObjectProperty<>(data.getValue().transactionCount));
            itemsSoldCol.setCellValueFactory(data -> 
                new SimpleObjectProperty<>(data.getValue().itemsSold));
            totalRevenueCol.setCellValueFactory(data -> 
                new SimpleObjectProperty<>(data.getValue().totalRevenue));
            avgRevenueCol.setCellValueFactory(data -> 
                new SimpleObjectProperty<>(data.getValue().averageRevenue));        
            
            view.getColumns().add(hourCol);
            view.getColumns().add(transactionsCol);
            view.getColumns().add(itemsSoldCol);
            view.getColumns().add(totalRevenueCol);
            view.getColumns().add(avgRevenueCol);
        }

        public void loadData() {
            // TODO: make this less garbage
            String sql = "WITH item_counts AS (\r\n" + //
                        "       SELECT order_id,\r\n" + //
                        "              COUNT(id) as items_in_order\r\n" + //
                        "       FROM order_items\r\n" + //
                        "       GROUP BY order_id\r\n" + //
                        ")\r\n" + //
                        "SELECT date,\r\n" + //
                        "       EXTRACT(HOUR FROM CAST(time as TIME)) as hour,\r\n" + //
                        "       COUNT(orders.id) as total_orders,\r\n" + //
                        "       SUM(orders.subtotal) as total_revenue,\r\n" + //
                        "       SUM(ic.items_in_order) as item_count\r\n" + //
                        "FROM orders\r\n" + //
                        "LEFT JOIN item_counts as ic\r\n" + //
                        "       ON ic.order_id = orders.id\r\n" + //
                        "WHERE date = CURRENT_DATE\r\n" + //
                        "GROUP BY date, hour\r\n" + //
                        "ORDER BY date DESC, hour DESC;";

            try (Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    HourlySalesSummary newEntry = new HourlySalesSummary();
                    newEntry.hour = rs.getInt("hour");
                    newEntry.transactionCount = rs.getInt("total_orders");
                    newEntry.itemsSold = rs.getInt("item_count");
                    newEntry.totalRevenue = rs.getDouble("total_revenue");
                    newEntry.averageRevenue = 
                        newEntry.totalRevenue / newEntry.transactionCount;
                    entries.add(newEntry);
                }        
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            view.getItems().setAll(entries);
        }

        public TableView<HourlySalesSummary> getView() {
            return view;
        }
    }

    HourlySalesSummaryTable reportTableView = new HourlySalesSummaryTable();

    private void initializeFinancials() {
        loadTotalRevenue();
        loadOrdersToday();
        loadRevenueTrend();
        LoadTotalOrdersByDate();

        // load data only once to reduce latency when opening x-report each time
        // may need to explicitly refresh somewhere else, on defined intervals
        reportTableView.loadData();

        xReportButton.setOnAction( event -> { 
            Stage popup = new Stage();
            popup.setTitle("X-report");

            Button closeButton = new Button("Confirm");
            closeButton.setOnAction(e -> {
                popup.close();
            });

            // TODO: add print button

            VBox root = new VBox(20, reportTableView.getView(), closeButton);
            root.setPadding(new Insets(20));
            root.setAlignment(Pos.CENTER);
            popup.setScene(new Scene(root, 500, 450));

            popup.initModality(Modality.NONE);
            popup.showAndWait();
        });

        printReportButton.setOnAction( (event) -> {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null) {
                // Show system print dialog; user selects 'Save as PDF' / 'Print to PDF'
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                boolean proceed = job.showPrintDialog(stage);
                if (proceed) {
                    boolean success = job.printPage(finRevenueTrendChart);
                    System.out.println("printing...");
                    if (success) {
                        job.endJob();
                    }
                }
            }
            System.out.println("the print failed");
        });
    }
    
    private void refreshFinanicals() {
        try { 
            loadTotalRevenue();
            loadOrdersToday();
            loadRevenueTrend();
        } catch (Exception e) {
            dbError(e);
        }
    }


    @FXML private TableView<EmployeeRow> employeesFullTable;
    @FXML private TableColumn<EmployeeRow, String> cashierColumn;
    @FXML private TableColumn<EmployeeRow, Number> hoursColumn;
    @FXML private TableColumn<EmployeeRow, Number> salesColumn;
    @FXML private TableColumn<EmployeeRow, Boolean> managerColumn;
    private final ObservableList<EmployeeRow> employees = FXCollections.observableArrayList();

    private void initializeEmployees() {
        cashierColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().name()));
        salesColumn.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().sales()));
        managerColumn.setCellValueFactory(d -> new SimpleBooleanProperty(d.getValue().isManager()));
        hoursColumn.setVisible(false); // no hours data in the schema; remove from the FXML if unused
        employeesFullTable.setItems(employees);
    }

    private void refreshEmployees() {
        try {
            employees.setAll(employeeDAO.getAllWithSales());
        } catch (SQLException e) {
            dbError(e);
        }
    }

    @FXML
    private void addEmployee() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Employee");
        dialog.setHeaderText("Enter employee name");

        dialog.showAndWait().ifPresent(name -> {
            if (name.isBlank()) return;
            try {
                employeeDAO.insert(name.trim(), "cashier");
                refreshEmployees();
            } catch (SQLException e) {
                dbError(e);
            }
        });
    }

    @FXML
    private void updateEmployee() {
        EmployeeRow sel = employeesFullTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        TextInputDialog dialog = new TextInputDialog(sel.name());
        dialog.setTitle("Update Employee");
        dialog.setHeaderText("Update employee name");

        dialog.showAndWait().ifPresent(name -> {
            if (name.isBlank()) return;
            try {
                employeeDAO.updateName(sel.id(), name.trim());
                refreshEmployees();
            } catch (SQLException e) {
                dbError(e);
            }
        });
    }

    @FXML
    private void removeEmployee() {
        EmployeeRow sel = employeesFullTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        try {
            employeeDAO.delete(sel.id());
            refreshEmployees();
        } catch (SQLException e) {
            dbError(e); 
        }
    }

    @FXML
    private void promoteEmployee() {
        EmployeeRow sel = employeesFullTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        try {
            employeeDAO.updateRole(sel.id(), "manager");
            refreshEmployees();
        } catch (SQLException e) {
            dbError(e);
        }
    }



    @FXML private TableView<InventoryItem> inventoryFullTable;
    @FXML private TableColumn<InventoryItem, Number> inventoryIdColumn;
    @FXML private TableColumn<InventoryItem, String> inventoryNameColumn;
    @FXML private TableColumn<InventoryItem, Number> inventoryQuantityColumn;
    @FXML private TableColumn<InventoryItem, String> inventoryUnitColumn;
    private final ObservableList<InventoryItem> inventoryItems = FXCollections.observableArrayList();

    private void initializeInventory() {
        inventoryIdColumn.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().id()));
        inventoryNameColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().name()));
        inventoryQuantityColumn.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().stock()));
        inventoryUnitColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().unit()));
        inventoryFullTable.setItems(inventoryItems);
    }

    private void refreshInventory() {
        try {
            inventoryItems.setAll(inventoryDAO.getAll());
        } catch (SQLException e) {
            dbError(e);
        }
    }

    @FXML
    private void addInventoryItem() {
        Dialog<InventoryItem> dialog = new Dialog<>();
        dialog.setTitle("Add Inventory Item");

        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        TextField nameField = new TextField();
        nameField.setPromptText("Item name");

        TextField unitField = new TextField();
        unitField.setPromptText("Unit (g, ml, pieces)");

        TextField quantityField = new TextField();
        quantityField.setPromptText("Starting quantity");

        VBox form = new VBox(10,
            new Label("Item Name"), nameField,
            new Label("Unit"), unitField,
            new Label("Quantity"), quantityField
        );
        dialog.getDialogPane().setContent(form);

        dialog.setResultConverter(button -> {
            if (button != addButton) return null;
            try {
                String name = nameField.getText().trim();
                String unit = unitField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());

                if (name.isEmpty() || unit.isEmpty() || quantity < 0) {
                    return null;
                }
                return new InventoryItem(0, name, unit, quantity);
            } catch (NumberFormatException e) {
                return null;
            }
        });

        dialog.showAndWait().ifPresent(item -> {
            try {
                inventoryDAO.insert(item.name(), item.unit(), item.stock());
                refreshInventory();
            } catch (SQLException e) {
                dbError(e);
            }
        });
    }

    @FXML
    private void updateInventoryQuantity() {
        InventoryItem selected = inventoryFullTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select an inventory item first.").showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog(String.valueOf(selected.stock()));
        dialog.setTitle("Update Inventory");
        dialog.setHeaderText("Update " + selected.name());
        dialog.setContentText("New quantity:");

        dialog.showAndWait().ifPresent(input -> {
            int quantity;
            try {
                quantity = Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                quantity = -1;
            }

            if (quantity < 0) {
                new Alert(Alert.AlertType.ERROR,
                    "Please enter a valid non-negative whole number.").showAndWait();
                return;
            }

            try {
                inventoryDAO.updateStock(selected.id(), quantity);
                refreshInventory();
            } catch (SQLException e) {
                dbError(e);
            }
        });
    }


    @FXML private TableView<Item> menuTable;
    @FXML private TableColumn<Item, Number> menuIdColumn;
    @FXML private TableColumn<Item, String> menuNameColumn;
    @FXML private TableColumn<Item, Number> menuPriceColumn;
    private final ObservableList<Item> menuItems = FXCollections.observableArrayList();

    private void initializeMenu() {
        menuIdColumn.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().id()));
        menuNameColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().name()));
        menuPriceColumn.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().basePrice()));
        menuTable.setItems(menuItems);
    }

    private void refreshMenu() {
        try {
            menuItems.setAll(itemDAO.getAll());
        } catch (SQLException e) {
            dbError(e);
        }
    }

    @FXML
    private void addMenuItem() {
        Dialog<Item> dialog = new Dialog<>();
        dialog.setTitle("Add Menu Item");

        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        TextField nameField = new TextField();
        nameField.setPromptText("Drink name");

        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        VBox form = new VBox(10,
            new Label("Drink Name"), nameField,
            new Label("Price"), priceField
        );
        dialog.getDialogPane().setContent(form);

        dialog.setResultConverter(button -> {
            if (button != addButton) return null;
            try {
                String name = nameField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());

                if (name.isEmpty() || !Double.isFinite(price) || price < 0) {
                    return null;
                }
                return new Item(0, name, price);
            } catch (NumberFormatException e) {
                return null;
            }
        });

        dialog.showAndWait().ifPresent(item -> {
            try {
                itemDAO.insert(item.name(), item.basePrice());
                refreshMenu();
            } catch (SQLException e) {
                dbError(e);
            }
        });
    }

    @FXML
    private void updateMenuItem() {
        Item selected = menuTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a menu item first.").showAndWait();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Update Menu Item");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField nameField = new TextField(selected.name());
        TextField priceField = new TextField(String.valueOf(selected.basePrice()));

        VBox form = new VBox(10,
            new Label("Drink Name"), nameField,
            new Label("Price"), priceField
        );
        dialog.getDialogPane().setContent(form);

        dialog.showAndWait().ifPresent(button -> {
            if (button != ButtonType.OK) return;

            String name = nameField.getText().trim();
            double price;
            try {
                price = Double.parseDouble(priceField.getText().trim());
            } catch (NumberFormatException e) {
                price = -1;
            }

            if (name.isEmpty() || !Double.isFinite(price) || price < 0) {
                new Alert(Alert.AlertType.ERROR,
                    "Enter a valid name and non-negative price.").showAndWait();
                return;
            }

            try {
                itemDAO.update(new Item(selected.id(), name, price));
                refreshMenu();
            } catch (SQLException e) {
                dbError(e);
            }
        });
    }
}