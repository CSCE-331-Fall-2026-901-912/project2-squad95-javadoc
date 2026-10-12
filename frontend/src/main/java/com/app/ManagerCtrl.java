package com.app;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.app.controllers.EmployeeDAO;
import com.app.controllers.InventoryDAO;
import com.app.controllers.ItemDAO;
import com.app.models.EmployeeRow;
import com.app.models.InventoryItem;
import com.app.models.Item;

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
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


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

    class OrdersDayRow { 
        public Date date;
        public Integer totalOrders;
        public Double totalRevenue;
        public String metSalesGoal;
    }
    @FXML private Label ordersTotalLabel;
    @FXML private Label revenueTotalLabel;

    @FXML private TableView<OrdersDayRow> financialsTable;
    @FXML TableColumn<OrdersDayRow, Date> finDayCol;
    @FXML TableColumn<OrdersDayRow, Integer> finTotalOrdersCol;
    @FXML TableColumn<OrdersDayRow, Double> finAmountCol;
    @FXML TableColumn<OrdersDayRow, String> finMetGoalCol;

    @FXML private Label finOrdersTotalLabel;
    @FXML private Label finRevenueTotalLabel;
    @FXML private BarChart<String, Number> finRevenueTrendChart;
    @FXML private BarChart<String, Number> revenueTrendChart;

    @FXML private Button xReportButton;
    @FXML private Button salesReportButton;

    public static class SalesReportRow {
        public String itemName;
        public Integer quantitySold;
        public Double totalRevenue;
    }

    // Database Queries
    private void loadOrdersToday() {
        // today's orders for Dashboard
        String sql = "SELECT COUNT(id) FROM orders WHERE date = CURRENT_DATE;";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String count = String.valueOf(rs.getInt(1));
                
                // Update Dashboard tab only
                if (ordersTotalLabel != null) ordersTotalLabel.setText(count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTotalRevenue() {
        // total revenue for Financials
        String sqlTotal = "SELECT SUM(subtotal) FROM orders;";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlTotal)) {
             
            if (rs.next()) {
                double totalRevenue = rs.getDouble(1);
                if (finRevenueTotalLabel != null) {
                    finRevenueTotalLabel.setText(String.format("$%,.2f", totalRevenue));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // today's revenue for Dashboard
        String sqlToday = "SELECT SUM(subtotal) FROM orders WHERE date = CURRENT_DATE;";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlToday)) {
             
            if (rs.next()) {
                double todayRevenue = rs.getDouble(1);
                if (revenueTotalLabel != null) {
                    revenueTotalLabel.setText(String.format("$%,.2f", todayRevenue));
                }
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

            XYChart.Series<String, Number> finSeries = new XYChart.Series<>();
            finSeries.setName("2025 Revenue Trend");
            XYChart.Series<String, Number> dashSeries = new XYChart.Series<>();
            dashSeries.setName("2025 Revenue Trend");

            while (rs.next()) {
                String month = rs.getString("month");
                double revenue = rs.getDouble("revenue");
                finSeries.getData().add(new XYChart.Data<>(month, revenue));
                dashSeries.getData().add(new XYChart.Data<>(month, revenue));
            }
            //  Financials
            finRevenueTrendChart.getData().clear();
            finRevenueTrendChart.getData().add(finSeries);
            // Dashboard
            if (revenueTrendChart != null) {
                revenueTrendChart.getData().clear();
                revenueTrendChart.getData().add(dashSeries);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void LoadTotalOrdersByDate() {
        // Note: make sure to keep the fields in the 
        // class and SQL statement identical
        String sql = "SELECT CAST(DATE AS date) AS date,\r\n" + 
                     "       COUNT(id) AS totalOrders,\r\n" + 
                     "       SUM(subtotal) AS totalRevenue\r\n" + 
                     "FROM orders\r\n" + 
                     "GROUP BY CAST(DATE AS date)\r\n" + 
                     "ORDER BY date DESC\r\n" + 
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
            // TODO: is this less garbage
            String sql = "WITH item_counts AS (\r\n" + 
                        "       SELECT order_id,\r\n" + 
                        "              COUNT(id) as items_in_order\r\n" + 
                        "       FROM order_items\r\n" + 
                        "       GROUP BY order_id\r\n" + 
                        ")\r\n" + 
                        "SELECT date,\r\n" + 
                        "       EXTRACT(HOUR FROM CAST(time as TIME)) as hour,\r\n" + 
                        "       COUNT(orders.id) as total_orders,\r\n" + 
                        "       SUM(orders.subtotal) as total_revenue,\r\n" + 
                        "       SUM(ic.items_in_order) as item_count\r\n" + 
                        "FROM orders\r\n" + 
                        "LEFT JOIN item_counts as ic\r\n" + 
                        "       ON ic.order_id = orders.id\r\n" + 
                        "WHERE date = CURRENT_DATE\r\n" + 
                        "GROUP BY date, hour\r\n" + 
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

            // print button

            VBox root = new VBox(20, reportTableView.getView(), closeButton);
            root.setPadding(new Insets(20));
            root.setAlignment(Pos.CENTER);
            popup.setScene(new Scene(root, 500, 450));

            popup.initModality(Modality.NONE);
            popup.showAndWait();
        });

        salesReportButton.setOnAction(event -> {
            Stage popup = new Stage();
            popup.setTitle("Sales Report");

            DatePicker startDatePicker = new DatePicker();
            DatePicker endDatePicker = new DatePicker();

            startDatePicker.setValue(java.time.LocalDate.of(2025, 1, 1));
            endDatePicker.setValue(java.time.LocalDate.of(2025, 12, 31));
            
            Button loadButton = new Button("Select");

            HBox controls = new HBox(10, new Label("Start:"), startDatePicker, new Label("End:"), endDatePicker, loadButton);
            controls.setAlignment(Pos.CENTER);

            TableView<SalesReportRow> table = new TableView<>();
            TableColumn<SalesReportRow, String> nameCol = new TableColumn<>("Item Name");
            nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().itemName));
            nameCol.setPrefWidth(150);
            
            TableColumn<SalesReportRow, Integer> qtyCol = new TableColumn<>("Total Sold");
            qtyCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().quantitySold));
            qtyCol.setPrefWidth(100);
            
            TableColumn<SalesReportRow, String> revCol = new TableColumn<>("Total Revenue");
            revCol.setCellValueFactory(data -> new SimpleStringProperty(String.format("$%,.2f", data.getValue().totalRevenue)));
            revCol.setPrefWidth(125);

            table.getColumns().add(nameCol);
            table.getColumns().add(qtyCol);
            table.getColumns().add(revCol);

            loadButton.setOnAction(e -> {
                ObservableList<SalesReportRow> data = FXCollections.observableArrayList();
                
                String sql = "SELECT i.name, COUNT(oi.id) as quantity, SUM(oi.price_at_order) as revenue " +
                             "FROM order_items oi " +
                             "JOIN orders o ON oi.order_id = o.id " +
                             "JOIN items i ON oi.item_id = i.id " +
                             "WHERE o.date >= '" + java.sql.Date.valueOf(startDatePicker.getValue()) + "' " +
                             "AND o.date <= '" + java.sql.Date.valueOf(endDatePicker.getValue()) + "' " +
                             "GROUP BY i.name ORDER BY revenue DESC;";
                
                try (Connection conn = Database.getConnection();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                     
                    while (rs.next()) {
                        SalesReportRow row = new SalesReportRow();
                        row.itemName = rs.getString("name");
                        row.quantitySold = rs.getInt("quantity");
                        row.totalRevenue = rs.getDouble("revenue");
                        data.add(row);
                    }
                    table.setItems(data);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            Button printButton = new Button("Print");
            printButton.setOnAction(e -> {
                PrinterJob job = PrinterJob.createPrinterJob();
                if (job != null) {
                    Stage popupStage = (Stage) printButton.getScene().getWindow();
                    boolean proceed = job.showPrintDialog(popupStage);
                    if (proceed) {
                        Label titleLabel = new Label("Sales Report");
                        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
                        Label datesLabel = new Label("From " + startDatePicker.getValue() + " to " + endDatePicker.getValue());
                        datesLabel.setStyle("-fx-font-size: 16px; -fx-padding: 0 0 10 0;");

                        // had to remove table to preserve format for printing 
                        VBox parent = (VBox) table.getParent();
                        int tableIndex = parent.getChildren().indexOf(table);
                        parent.getChildren().remove(table);

                        VBox printContainer = new VBox(10, titleLabel, datesLabel, table);
                        PageLayout pageLayout = job.getJobSettings().getPageLayout();
                        double pageWidth = pageLayout.getPrintableWidth();
                        double pageHeight = pageLayout.getPrintableHeight();
                        
                        printContainer.setPrefSize(pageWidth, pageHeight);
                        VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS);
                        
                        printContainer.applyCss();
                        printContainer.layout();

                        boolean success = job.printPage(printContainer);
                        System.out.println("printing...");
                        printContainer.getChildren().remove(table);
                        parent.getChildren().add(tableIndex, table);
                        if (success) {
                            job.endJob();
                        }
                    }
                }
            });

            HBox bottomControls = new HBox(10, printButton);
            bottomControls.setAlignment(Pos.CENTER);

            VBox root = new VBox(20, controls, table, bottomControls);
            VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS);
            root.setPadding(new Insets(20));
            root.setAlignment(Pos.CENTER);
            popup.setScene(new Scene(root, 600, 690));

            popup.initModality(Modality.NONE);
            popup.showAndWait();
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