package com.app;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.util.List;

import org.postgresql.core.SetupQueryRunner;
import java.sql.Time;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;

import javafx.embed.swing.SwingFXUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import javafx.scene.image.WritableImage;
import javafx.embed.swing.SwingFXUtils;

import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.postgresql.core.SetupQueryRunner;

import com.app.controllers.EmployeeDAO;
import com.app.controllers.InventoryDAO;
import com.app.controllers.ItemDAO;
import com.app.controllers.RecipeDAO;
import com.app.models.EmployeeRow;
import com.app.models.InventoryItem;
import com.app.models.Item;
import com.app.models.Order;
import com.app.models.Recipe;
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
import javafx.print.PageLayout;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.scene.SnapshotParameters;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;

/** 
* Creates a manager view window using JavaFX
* @author Ethan Faulkner
* @author Andrew Schrick
* @author Kenny Le
* @author Jeffrey Yu
* @author Christopher Jermia
* 
*
*
*/
public class ManagerCtrl {

    /** Database object representing the table of menu items */
    private final ItemDAO itemDAO = new ItemDAO();
    /** Database object representing the table of inventory items */
    private final InventoryDAO inventoryDAO = new InventoryDAO();
    /** Database object representing the table of employees */
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    /** Database object representing the table of recipes */
    private final RecipeDAO recipeDAO = new RecipeDAO();

    /** Displays the dashboard view to the user. */
    @FXML private VBox dashboardView;
    /** Displays the finanicals view to the user. */
    @FXML private HBox financialsView;
    /** Displays the orders view to the user. */
    @FXML private VBox ordersView;
    /** Displays the employees view to the user. */
    @FXML private VBox employeesView;
    /** Displays the inventory view to the user. */
    @FXML private VBox inventoryFullView;
    /** Displays the order menu view to the user. */
    @FXML private VBox menuView;
    /** Displays the product usage view to the user. */
    @FXML private VBox usageView;

    @FXML
    /**
     * Initializes manager view and queries necessary information from database
     * 
     */
    public void initialize() {
        showOnly(dashboardView);
        initializeFinancials();
        initializeEmployees();
        initializeInventory();
        initializeMenu();
        initializeOrdersTab();
        refreshEmployees();
        LoadTopEmployees();
        refreshInventory();
        refreshMenu();

        initializeUsageChart();
    }
    /**
     * Creates an an alert for a database exception describing the exception
     * @param e exception to be displayed
     */
    private void dbError(Exception e) {
        e.printStackTrace();
        new Alert(Alert.AlertType.ERROR, "Database error: " + e.getMessage()).showAndWait();
    }

    // Navigation

    /**
     * Shows only the given view and hides all other views.
     * @param view The view to show.
     */
    private void showOnly(Region view) {
        for (Region r : List.of(dashboardView, financialsView, ordersView,
                                employeesView, inventoryFullView, menuView, usageView)) {
            boolean on = (r == view);
            r.setVisible(on);
            r.setManaged(on);
        }
    }
    
    /**
     * Displays dashboard view
     * @param e the triggering action event
     */
    @FXML private void showDashboard(ActionEvent e)  { showOnly(dashboardView); }
    /**
     * Displays financials view
     * @param e the triggering action event
     */
    @FXML private void showFinancials(ActionEvent e) { showOnly(financialsView); }
    /**
     * Displays orders view
     * @param e the triggering action event
     */
    @FXML private void showOrders(ActionEvent e)     { showOnly(ordersView); }
    /**
     * Displays the employees in the store
     * @param e the triggering action event
     */
    @FXML private void showEmployees(ActionEvent e)  { showOnly(employeesView); }
    /**
     * Displays the list of inventory
     * @param e the triggering action event
     */
    @FXML private void showInventory(ActionEvent e)  { showOnly(inventoryFullView); }
    /**
     * Displays the menu view
     * @param e the triggering action event
     */
    @FXML private void showMenu(ActionEvent e)       { showOnly(menuView); }

    /**
     * Displays the usage view
     * @param event the triggering action event
     */
    @FXML private void showUsage(ActionEvent event) {showOnly(usageView);}
    
    /**
     * Switches to the cashier view when the "Switch to Cashier" button is clicked.
     * @param event the triggering action event
     * @throws IOException if the cashier FXML cannot be loaded
     */
    @FXML
    private void switchToCashier(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cashier.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    
    
    /**
     * Represents a row of data held in the financials table.
     */
    class OrdersDayRow { 
        public Date date;
        public Integer totalOrders;
        public Double totalRevenue;
        //public String metSalesGoal;
    }
    /** Label showing the total number of orders. */
    @FXML private Label ordersTotalLabel;
    @FXML private Label revenueTotalLabel;
    /** Label showing the order total in the orders view. */
    @FXML private Label ordersTabTotalLabel;

    /** Table of daily order totals in the financials view. */
    @FXML private TableView<OrdersDayRow> financialsTable;
    /** Day column in the financials table. */
    @FXML TableColumn<OrdersDayRow, Date> finDayCol;
    /** Total orders column in the financials table. */
    @FXML TableColumn<OrdersDayRow, Integer> finTotalOrdersCol;
    /** Revenue amount column in the financials table. */
    @FXML TableColumn<OrdersDayRow, Double> finAmountCol;
    //@FXML TableColumn<OrdersDayRow, String> finMetGoalCol;

    /** Label showing the total number of orders in the financials view. */
    @FXML private Label finOrdersTotalLabel;
    /** Label showing the total revenue in the financials view. */
    @FXML private Label finRevenueTotalLabel;
    /** Bar chart of the revenue trend in the financials view. */
    @FXML private BarChart<String, Number> finRevenueTrendChart;
    @FXML private BarChart<String, Number> revenueTrendChart;

    @FXML private Button xReportButton;
    @FXML private Button restockReportBtn;
    //@FXML private Button salesReportButton;

    /**
     * SalesReportRow
     * Represents a row in the sales report table.
     */
    public static class SalesReportRow {
        public String itemName;
        public Integer quantitySold;
        public Double totalRevenue;
    }

    /**
     * OrderTableRow
     * Represents a row of data for both the orders table in the orders view
     * and the dashboard orders table.
     */
    public static class OrderTableRow {
        public Integer id;
        public String items;
        public Double total;
        public String date;
        public String time;
    }

    @FXML private BarChart<String, Number> orderTrendChart;
    /** Table of orders in the orders view. */
    @FXML private TableView<OrderTableRow> ordersTable;
    /** Order id column in the orders table. */
    @FXML private TableColumn<OrderTableRow, Number> orderIdCol;
    /** Items column in the orders table. */
    @FXML private TableColumn<OrderTableRow, String> orderItemsCol;
    /** Order total column in the orders table. */
    @FXML private TableColumn<OrderTableRow, String> orderTotalCol;
    /** Order date column in the orders table. */
    @FXML private TableColumn<OrderTableRow, String> orderDateCol;
    /** Order time column in the orders table. */
    @FXML private TableColumn<OrderTableRow, String> orderTimeCol;

    /** Table of recent orders shown on the dashboard. */
    @FXML private TableView<OrderTableRow> dashOrdersTable;
    /** Order id column in the dashboard orders table. */
    @FXML private TableColumn<OrderTableRow, Number> dashOrderIdCol;
    /** Items column in the dashboard orders table. */
    @FXML private TableColumn<OrderTableRow, String> dashOrderItemsCol;
    /** Order total column in the dashboard orders table. */
    @FXML private TableColumn<OrderTableRow, String> dashOrderTotalCol;
    /** Order time column in the dashboard orders table. */
    @FXML private TableColumn<OrderTableRow, String> dashOrderTimeCol;


    // Database Queries

    /** Loads the total number of orders for today and updates the label in the financials view.
     * 
     */
private void loadOrdersToday() {
        // today's orders for Dashboard and Orders Tab
        String sql = "SELECT COUNT(id) FROM orders WHERE date = CURRENT_DATE;";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String count = String.valueOf(rs.getInt(1));
                
                // Update Dashboard tab
                if (ordersTotalLabel != null) ordersTotalLabel.setText(count);
                
                // Update Orders tab
                if (ordersTabTotalLabel != null) ordersTabTotalLabel.setText(count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /** Loads the total revenue from all orders and updates the label in the financials view.
     *
     */
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

    /** Loads the revenue trend data from the database and populates the finRevenueTrendChart with the results.
     * 
     */
    private void loadRevenueTrend() {
        String sql = "SELECT TO_CHAR(date, 'Mon') AS month, SUM(subtotal) AS revenue " +
                     "FROM orders " +
                     "WHERE EXTRACT(YEAR FROM date) = 2025 " +
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
                
                XYChart.Data<String, Number> finData = new XYChart.Data<>(month, revenue);
                XYChart.Data<String, Number> dashData = new XYChart.Data<>(month, revenue);
                
                // colors for bars
                finData.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        newNode.setStyle(revenue >= 85000 ? "-fx-bar-fill: #22d428;" : "-fx-bar-fill: #e01f1f;"); // if >= $85,000 in monthly sales
                    }
                });
                
                dashData.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        newNode.setStyle(revenue >= 85000 ? "-fx-bar-fill: #22d428;" : "-fx-bar-fill: #e01f1f;");
                    }
                });

                finSeries.getData().add(finData);
                dashSeries.getData().add(dashData);
            }
            
            // Financials
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

    /** Loads the total number of orders and total revenue for each date in the past year, 
     * and populates the financialsTable with the results.
     * 
     */
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

            ColumnCurrencyFormatSetter<OrdersDayRow, Double> currencyFormatSetter =
                new ColumnCurrencyFormatSetter<>("USD");
            currencyFormatSetter.applyTo(finAmountCol);

            // TODO: determine if "met sales goal column" should be done
            //finMetGoalCol.setCellValueFactory(data -> 
                //new SimpleObjectProperty<>("No"));
            
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

    

    /**
     * Represents a row of data held in the HourlySalesSummaryTable class. Used in the x-report.
     */
    class HourlySalesSummary {
        public Integer hour;
        public Integer transactionCount;
        public Integer itemsSold;
        public Double totalRevenue;
        public Double averageRevenue;
    }

    /**
     * 
     * ColumnCurrencyFormatSetter: Helper class for setting columns' format to currency.
     * This should be used only after calling .setCellValueFactory() on columns to be formatted. 
     * @param <RowData> The structure representing each row of the Table.
     * @param <T> The type of data in the column to be formatted as currency.
     */
    class ColumnCurrencyFormatSetter<RowData, T> {
        NumberFormat currencyFormat;
        Callback< TableColumn<RowData, T>, TableCell<RowData, T> > moneyCellFactory;

        /**
         * Initializes instance, given an ISO_4217 currency code.
         * @param currencyCode The type of currency to represent column data as. 
         */
        ColumnCurrencyFormatSetter(String currencyCode) {
            this.currencyFormat = NumberFormat.getCurrencyInstance();
            this.currencyFormat.setCurrency(Currency.getInstance(currencyCode));

            this.moneyCellFactory = (tc) -> new TableCell<RowData, T>() {
                // https://stackoverflow.com/questions/48733121/javafx-format-double-in-tablecolumn
                @Override
                protected void updateItem(T value, boolean empty) {
                    super.updateItem(value, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(currencyFormat.format(value));
                    }
                }
            };
        }

        ColumnCurrencyFormatSetter() {
            this("USD");
        }

        public void applyTo(TableColumn<RowData, T> col) {
            col.setCellFactory(moneyCellFactory);
        }
    }

    /**
     * Contains both the data reprentation of the x-report and its view. 
     */
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
            
            ColumnCurrencyFormatSetter<HourlySalesSummary, Double> moneyFormatSetter = 
                new ColumnCurrencyFormatSetter<>("USD");
            moneyFormatSetter.applyTo(totalRevenueCol);
            moneyFormatSetter.applyTo(avgRevenueCol);

            view.getColumns().add(hourCol);
            view.getColumns().add(transactionsCol);
            view.getColumns().add(itemsSoldCol);
            view.getColumns().add(totalRevenueCol);
            view.getColumns().add(avgRevenueCol);
        }

        /**
         * Clears, then populates the data held internally in the table object with
         * report data of the current date. This updates the view as well.
         */
        public void loadData() {
            // debugging aid; must wrap in quotes due to SQL syntax
            // final String dateToLoad = "\'2025-10-10\'";
            final String dateToLoad = "CURRENT_DATE";

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
                        "WHERE date = " + dateToLoad + " \r\n" + 
                        "GROUP BY date, hour\r\n" + 
                        "ORDER BY date DESC, hour ASC;";

            try (Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

                // view can be reloaded, so cleanup outdated entries
                // an error from next() can happen after clearing, but oh well
                entries.clear(); 

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
        
        /**
         * @return A TableView node with the current x-report data.
         */
        public TableView<HourlySalesSummary> getView() {
            return view;
        }
    }

    /** Initializes the orders tab by setting up the table columns, loading today's orders into the table, 
     * and loading the hourly trend data into the chart.
     */
    private void initializeOrdersTab() {
        if (orderIdCol != null) {
            orderIdCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().id));
            orderItemsCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().items));
            orderTotalCol.setCellValueFactory(data -> new SimpleStringProperty(String.format("$%,.2f", data.getValue().total)));
            orderDateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().date));
            orderTimeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().time));
        }

        if (dashOrderIdCol != null) {
            dashOrderIdCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().id));
            dashOrderItemsCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().items));
            dashOrderTotalCol.setCellValueFactory(data -> new SimpleStringProperty(String.format("$%,.2f", data.getValue().total)));
            dashOrderTimeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().time));
        }

        ObservableList<OrderTableRow> tableData = FXCollections.observableArrayList();
        String tableSql = "SELECT o.id, o.subtotal, o.date, o.time, string_agg(i.name, ', ') as item_names " +
                          "FROM orders o " +
                          "LEFT JOIN order_items oi ON o.id = oi.order_id " +
                          "LEFT JOIN items i ON oi.item_id = i.id " +
                          "WHERE o.date = CURRENT_DATE " +
                          "GROUP BY o.id, o.subtotal, o.date, o.time " +
                          "ORDER BY o.time DESC;";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(tableSql)) {
             
            while (rs.next()) {
                OrderTableRow row = new OrderTableRow();
                row.id = rs.getInt("id");
                row.total = rs.getDouble("subtotal");
                row.date = rs.getString("date");
                row.time = rs.getString("time");
                row.items = rs.getString("item_names");
                if (row.items == null) row.items = "No items";
                tableData.add(row);
            }
            
            if (ordersTable != null) ordersTable.setItems(tableData);
            if (dashOrdersTable != null) dashOrdersTable.setItems(tableData);
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        String chartSql = "SELECT EXTRACT(HOUR FROM CAST(time as TIME)) as hour, COUNT(id) as total_orders " +
                          "FROM orders WHERE date = CURRENT_DATE " +
                          "GROUP BY EXTRACT(HOUR FROM CAST(time as TIME)) " +
                          "ORDER BY hour ASC;";
                          
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(chartSql)) {
             
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Orders Today");
            
            while (rs.next()) {
                String hourLabel = rs.getInt("hour") + ":00";
                int count = rs.getInt("total_orders");
                series.getData().add(new XYChart.Data<>(hourLabel, count));
            }
            
            if (orderTrendChart != null) {
                orderTrendChart.getData().clear();
                orderTrendChart.getData().add(series);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 
     * Holds both the view object and the internal data for the x-report's table. 
     */
    HourlySalesSummaryTable reportTable = new HourlySalesSummaryTable();
    
    /**
     * Opens a prompt to the user to export a snapshot of the provided node
     * as a PDF file with a maximum length of 1 page. 
     * 
     * @param node The scene graph node to snapshot for the exported PDF.
     * 
     */
    private void promptExportToPDF(Node node) {
        // https://stackoverflow.com/questions/22789449/export-stage-into-pdf
        SnapshotParameters params = new SnapshotParameters();
        // what if no scaling the snapshot :D

        WritableImage fxImg = node.snapshot(params, null);
        BufferedImage bufImg = SwingFXUtils.fromFXImage(fxImg, null);

        try(PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            // 72 points per inch, returned lengths are in points
            final float MARGIN = 36;
            float pageWidth = page.getMediaBox().getWidth() - (MARGIN * 2);
            float pageHeight = page.getMediaBox().getWidth() - (MARGIN * 2);

            PDImageXObject pdImage = LosslessFactory.createFromImage(doc, bufImg);

            float imageWidth = bufImg.getWidth();
            float imageHeight = bufImg.getHeight();

            float imageScale = Math.min(pageWidth / imageWidth, 
                                        pageHeight / imageHeight);
            
            imageWidth *= imageScale;
            imageHeight *= imageScale;

            float xPos = MARGIN;
            // The y-axis starts at the "bottom" and writes "down"
            float yPos = page.getMediaBox().getHeight() - MARGIN - imageHeight;

            try(PDPageContentStream contentStream = new PDPageContentStream(doc, page)) {
                contentStream.drawImage(pdImage, xPos, yPos, imageWidth, imageHeight);
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Document");
            var currentDate = Calendar.getInstance().getTime();
            String dateStr = currentDate.toString();
            dateStr = dateStr.replace(':', '-');
            fileChooser.setInitialFileName("summary-report " + dateStr + ".pdf");

            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf")
            );

            File selectedFile = fileChooser.showSaveDialog(null);
            if (selectedFile == null) return;
            doc.save(selectedFile);

        } catch(IOException e) {
            System.err.println("Something went wrong while printing!");
            e.printStackTrace();
        } 
    }

    /** Gets the current time and date in text string format.
     * 
     * @return A string containing the current date and time formatted for display in the x-report popup window.
     */
    private String getCurrentTimeDateText() {
        Date currDate = new Date(System.currentTimeMillis());
        Time currTime = new Time(System.currentTimeMillis());
        String labelFormat = "Generated report for %s since %s";
        return labelFormat.formatted(currDate, currTime);
    }    

    /** Creates a popup window displaying the x-report, which includes a table
     *  of hourly sales summary data and buttons for exporting to PDF, refreshing the data, and closing the window.
     * 
     * @param event The button ActionEvent that triggered the creation of the popup window.
     */
    private void createXReportPopupWindow(ActionEvent event) {
        Stage popup = new Stage();
        popup.setTitle("X-report");

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> {
            popup.close();
        });

        Label reportDateLabel = new Label( getCurrentTimeDateText() );

        VBox printableContain = new VBox(20, reportDateLabel, reportTable.getView()); 

        Button printButton = new Button("Export to PDF");
        printButton.setOnAction(e -> {
            final double REC_PRINT_WIDTH = 750;
            reportTable.getView().setMinWidth(REC_PRINT_WIDTH);
            promptExportToPDF(printableContain);
            // use computed size
            reportTable.getView().setMinWidth(-1);
        });

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> {
            reportTable.loadData();
            reportDateLabel.setText(getCurrentTimeDateText());
        });

        HBox btnContain = new HBox(20, printButton, refreshButton, closeButton);
        btnContain.setAlignment(Pos.BASELINE_RIGHT);
        VBox root = new VBox(20, printableContain, btnContain);

        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        popup.setScene(new Scene(root, 500, 450));

        popup.initModality(Modality.NONE);
        popup.setTitle("X-report");
        popup.showAndWait();
    }

    // Sales Reports
    @FXML private Button salesReportButton;

    
    /** Represents a row of data held in the sales report table.
     */
    // public static class SalesReportRow {
    //     public String itemName;
    //     public Integer quantitySold;
    //     public Double totalRevenue;
    // }

    /** 
    * Initializes the base structure for Restock Entries and the Table
    */
    private static final double MIN_STOCK = 10000.0;
    
    /** Restock Reports
     * Represents a row of the restock table.
     */
    public static class RestockEntries {
        public Integer id;
        public String name;
        public String unit;
        public Double stock;
        public Double minStock;
    }

    /**
     * Helper class that generates the restock table.
     */
    public static class RestockTable {
        private ObservableList<RestockEntries> entries = FXCollections.observableArrayList();
        private TableView<RestockEntries> view;

        /**
         * generates the restock table with headers
         */
        public RestockTable() {
            view = new TableView<>();

            var idColumn = new TableColumn<RestockEntries, Integer>("ID");
            idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().id));

            var nameColumn = new TableColumn<RestockEntries, String>("Name");
            nameColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().name));

            var unitColumn = new TableColumn<RestockEntries, String>("Unit");
            unitColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().unit));

            var stockColumn = new TableColumn<RestockEntries, Double>("Stock");
            stockColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().stock));

            var minStockColumn = new TableColumn<RestockEntries, Double>("Min Stock");
            minStockColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().minStock));

            view.getColumns().add(idColumn);
            view.getColumns().add(nameColumn);
            view.getColumns().add(unitColumn);
            view.getColumns().add(stockColumn);
            view.getColumns().add(minStockColumn);
        }
        
        /**
         * loads the stock data into the table
         */
        public void loadData() {
            entries.clear();
            String sql = "SELECT id, name, unit, stock, " +
             MIN_STOCK + " AS min_stock " +
             "FROM inventory " +
             "WHERE stock < " + MIN_STOCK + " " +
             "ORDER BY id";

            System.out.println("Running Restock Report SQL: " + sql);

            try (Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    RestockEntries entry = new RestockEntries();
                    entry.id = rs.getInt("id");
                    entry.name = rs.getString("name");
                    entry.unit = rs.getString("unit");
                    entry.stock = rs.getDouble("stock");
                    entry.minStock = rs.getDouble("min_stock");

                    System.out.println("Loaded: " + entry.name +
                               " | Stock: " + entry.stock);

                    entries.add(entry);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            System.out.println("Total restock items: " + entries.size());

            view.getItems().setAll(entries);
        }
        
        /**
         * Getter for the restock table's view object
         * @return returns the view of the table
         */
        public TableView<RestockEntries> getView() {
            return view;
        }
    }

    RestockTable restockReportTableView = new RestockTable();

    /**
     * Initializes the financials view, populating tables and charts within the view
     * and assigning actions to buttons in the view.
     */
    private void initializeFinancials() {
        loadTotalRevenue();
        loadOrdersToday();
        loadRevenueTrend();
        LoadTotalOrdersByDate();

        // load data only once to reduce latency when opening x-report each time
        reportTable.loadData();

        xReportButton.setOnAction( this::createXReportPopupWindow );

        restockReportTableView.loadData();
        restockReportBtn.setOnAction( event -> { 
            restockReportTableView.loadData();
            
            Stage popup = new Stage();
            popup.setTitle("Restock Report");

            Button closeButton = new Button("Confirm");
            closeButton.setOnAction(e -> {
                popup.close();
            });

            Button printButton = new Button("Export to PDF");
            printButton.setOnAction(e -> {
                final double REC_PRINT_WIDTH = 750;
                restockReportTableView.getView().setMinWidth(REC_PRINT_WIDTH);
                promptExportToPDF(restockReportTableView.getView());
                // use computed size
                restockReportTableView.getView().setMinWidth(-1);
            });

            VBox root = new VBox(20, restockReportTableView.getView(), closeButton, printButton);
            root.setPadding(new Insets(20));
            root.setAlignment(Pos.CENTER);
            popup.setScene(new Scene(root, 500, 450));

            popup.initModality(Modality.NONE);
            popup.showAndWait();
        });

            
        /**
        * Creates a popup window displaying the sales report, which includes 
        * a table of sales data for items sold between the selected start and end dates. 
        * The user can select the date range and load the report data, 
        * as well as print the report.
        */
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
    
    /** refreshes the financials view by reloading the total revenue, orders today, 
     * and revenue trend data from the database.
     * @deprecated no longer used; superseded by the newer financials refresh logic
     */
    @Deprecated 
    private void refreshFinanicals() {
        try { 
            loadTotalRevenue();
            loadOrdersToday();
            loadRevenueTrend();
        } catch (Exception e) {
            dbError(e);
        }
    }


    /** Full employees table in the employees view. */
    @FXML private TableView<EmployeeRow> employeesFullTable;
    /** Employee id column in the employees table. */
    @FXML private TableColumn<EmployeeRow, Number> empTabIdCol;
    /** Employee name column in the employees table. */
    @FXML private TableColumn<EmployeeRow, String> cashierColumn;
    /** Hours worked column in the employees table. */
    @FXML private TableColumn<EmployeeRow, Number> hoursColumn;
    @FXML private TableColumn<EmployeeRow, Number> salesColumn;
    /** Manager flag column in the employees table. */
    @FXML private TableColumn<EmployeeRow, Boolean> managerColumn;

    /** Employee table shown on the dashboard. */
    @FXML private TableView<EmployeeRow> employeeTable;
    /** Employee id column in the dashboard employee table. */
    @FXML private TableColumn<EmployeeRow, Number> dashEmpIdCol;
    /** Employee name column in the dashboard employee table. */
    @FXML private TableColumn<EmployeeRow, String> dashCashierCol;
    /** Hours worked column in the dashboard employee table. */
    @FXML private TableColumn<EmployeeRow, Number> dashHoursCol;
    /** Sales column in the dashboard employee table. */
    @FXML private TableColumn<EmployeeRow, Number> dashSalesCol;
    /** Manager flag column in the dashboard employee table. */
    @FXML private TableColumn<EmployeeRow, Boolean> dashManagerCol;
    
    /** Observable list of employee rows backing the employee tables. */
    private final ObservableList<EmployeeRow> employees = FXCollections.observableArrayList();

    /** Initializes the employees table view by setting up the cell value factories for each column and 
     * populating the table with employee data from the database.
     */
    private void initializeEmployees() {
        if (empTabIdCol != null) empTabIdCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().id()));
        cashierColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().name()));
        salesColumn.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().sales()));
        managerColumn.setCellValueFactory(d -> new SimpleBooleanProperty(d.getValue().isManager()));
        
        ColumnCurrencyFormatSetter<EmployeeRow, Number> currencyFormatSetter =
                new ColumnCurrencyFormatSetter<>("USD");
        currencyFormatSetter.applyTo(salesColumn);

        employeesFullTable.setItems(employees);

        if (dashCashierCol != null) {
            if (dashEmpIdCol != null) dashEmpIdCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().id()));
            dashCashierCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().name()));
            dashSalesCol.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().sales()));
            dashManagerCol.setCellValueFactory(d -> new SimpleBooleanProperty(d.getValue().isManager()));
            
            currencyFormatSetter.applyTo(dashSalesCol);

            employeeTable.setItems(employees);
        }
    }

    /** Refreshes the employees table view by retrieving the latest employee data from the
     *  database and updating the table.
     * 
     */
    private void refreshEmployees() {
        try {
            employees.setAll(employeeDAO.getAllWithSales());
        } catch (SQLException e) {
            dbError(e);
        }
    }

    @FXML
    /** Adds a new employee to the database by prompting the user for the employee's name and 
     * inserting it into the database.
     * 
     */
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
    /** Updates the name of the selected employee in the employees table view by prompting the user for
     *  a new name and updating it in the database.
     * 
     */
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

    /**
     * Adds an employee to the table
     */
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

    /**
     * Promotes the employee to manager 
     */
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

    @FXML BarChart<Number, String> topCashiersChart;
    /** Bar chart of top cashiers by sales. */
    @FXML BarChart<Number, String> empTopCashiersChart;

    /** 
     * Fetches and populates the two top cashiers charts using data from the database.
     * If an error occurs, silently fails and prints an error message to the console.
     */
    private void LoadTopEmployees() {
        String sql = "SELECT e.name, SUM(o.subtotal) AS total_sales " +
                     "FROM orders o JOIN employees e ON o.employee_id = e.id " +
                     "GROUP BY e.name " +
                     "ORDER BY total_sales ASC;";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            XYChart.Series<Number, String> dashSeries = new XYChart.Series<>();
            dashSeries.setName("Total Sales");
            XYChart.Series<Number, String> empSeries = new XYChart.Series<>();
            empSeries.setName("Total Sales");

            while (rs.next()) {
                double sales = rs.getDouble("total_sales");
                String name = rs.getString("name");
                
                dashSeries.getData().add(new XYChart.Data<>(sales, name));
                empSeries.getData().add(new XYChart.Data<>(sales, name));
            }
            if (topCashiersChart != null) {
                topCashiersChart.getData().clear();
                topCashiersChart.getData().add(dashSeries);
            }
            if (empTopCashiersChart != null) {
                empTopCashiersChart.getData().clear();
                empTopCashiersChart.getData().add(empSeries);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /** Full inventory table in the inventory view. */
    @FXML private TableView<InventoryItem> inventoryFullTable;
    /** Item id column in the inventory table. */
    @FXML private TableColumn<InventoryItem, Number> inventoryIdColumn;
    /** Item name column in the inventory table. */
    @FXML private TableColumn<InventoryItem, String> inventoryNameColumn;
    /** Stock quantity column in the inventory table. */
    @FXML private TableColumn<InventoryItem, Number> inventoryQuantityColumn;
    /** Unit of measure column in the inventory table. */
    @FXML private TableColumn<InventoryItem, String> inventoryUnitColumn;
    /** Label showing the number of low-stock inventory items. */
    @FXML private Label lowStockCountLabel;
    /** Bar chart of current inventory stock levels. */
    @FXML private BarChart<String, Number> inventoryStockChart;
    /** Observable list of inventory items backing the inventory table. */
    private final ObservableList<InventoryItem> inventoryItems = FXCollections.observableArrayList();

    /**
     * initializes the inventory, setting the appropriate CellValueFactory for each
     * of the columns in the inventory table.
     */
    private void initializeInventory() {
        inventoryIdColumn.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().id()));
        inventoryNameColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().name()));
        inventoryQuantityColumn.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().stock()));
        inventoryUnitColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().unit()));
        inventoryFullTable.setItems(inventoryItems);
    }
        
    /**
     * pulls the latest inventories from the database and refreshes the inventoryStockChart
     */
    private void refreshInventory() {
        try {
            inventoryItems.setAll(inventoryDAO.getAll());
            
            String sql = "SELECT COUNT(id) FROM inventory WHERE stock < " + MIN_STOCK + ";";
            try (Connection conn = Database.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                 
                if (rs.next()) {
                    int lowStockCount = rs.getInt(1);
                    if (lowStockCountLabel != null) {
                        lowStockCountLabel.setText(String.valueOf(lowStockCount));
                    }
                }
            }
            
            if (inventoryStockChart != null) {
                inventoryStockChart.getData().clear();
                
                XYChart.Series<String, Number> lowStockSeries = new XYChart.Series<>();
                lowStockSeries.setName("Stock Level");
                
                for (InventoryItem item : inventoryItems) {
                    if (item.stock() < MIN_STOCK) {
                        String unit = item.unit() != null ? item.unit() : "units";
                        String axisLabel = item.name() + "\n(" + unit + ")";
                        
                        lowStockSeries.getData().add(new XYChart.Data<>(axisLabel, item.stock()));
                    }
                }
                inventoryStockChart.getData().add(lowStockSeries);
            }
        } catch (SQLException e) {
            dbError(e);
        }
    }

    /**
     * adds new inventory items to the database, refreshing the inventory on
     * success and printing a stack trace on failure. attached to a button in .fxml file
     */
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

    /**
     * updates the quantities of inventory items, refreshing the inventory on
     * success and printing a stack trace on failure. attached to a button in .fxml file
     */
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

    





    @FXML private DatePicker usageStartDate;
    @FXML private DatePicker usageEndDate;
    @FXML private BarChart<String, Number> usageChart;


    /**
     * Creates a popup window displaying inventory usage
     * for a selected date range.
     */
    private void initializeUsageChart() {
        usageStartDate.setValue(java.time.LocalDate.of(2025, 1, 1));
        usageEndDate.setValue(java.time.LocalDate.of(2025, 12, 31));

        usageStartDate.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                loadUsageChart();
            }
        });

        usageEndDate.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                loadUsageChart();
            }
        });

        loadUsageChart();
    }

    /**
     * Loads an inventory usage chart based on user-inputted start and end dates.
     * Alerts the user if database fetching fails.
     */
    @FXML
    private void loadUsageChart() {

        if (usageStartDate.getValue() == null ||
            usageEndDate.getValue() == null ||
            usageStartDate.getValue().isAfter(usageEndDate.getValue())) {

            new Alert(
                Alert.AlertType.WARNING,
                "Please select a valid date range."
            ).showAndWait();
            return;
        }

        String sql =
            "SELECT inv.name, inv.unit, " +
            "SUM(r.quantity) AS total_used " +
            "FROM order_items oi " +
            "JOIN orders o ON oi.order_id = o.id " +
            "JOIN recipes r ON oi.item_id = r.item_id " +
            "JOIN inventory inv ON r.inventory_id = inv.id " +
            "WHERE o.date BETWEEN ? AND ? " +
            "GROUP BY inv.id, inv.name, inv.unit " +
            "ORDER BY total_used DESC";

        XYChart.Series<String, Number> series =
            new XYChart.Series<>();

        series.setName("Inventory Used");

        try (Connection conn = Database.getConnection();
            java.sql.PreparedStatement stmt =
                conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(usageStartDate.getValue()));
            stmt.setDate(2, java.sql.Date.valueOf(usageEndDate.getValue()));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    String unit = rs.getString("unit");
                    double used = rs.getDouble("total_used");

                    series.getData().add(
                        new XYChart.Data<>(
                            name + " (" + unit + ")",
                            used
                        )
                    );
                }
            }

            usageChart.getData().setAll(series);

        } catch (SQLException e) {
            dbError(e);
        }
    }


    

    /** Table of menu items in the menu view. */
    @FXML private TableView<Item> menuTable;
    /** Item id column in the menu table. */
    @FXML private TableColumn<Item, Number> menuIdColumn;
    /** Item name column in the menu table. */
    @FXML private TableColumn<Item, String> menuNameColumn;
    /** Item price column in the menu table. */
    @FXML private TableColumn<Item, Number> menuPriceColumn;
    /** Observable list of menu items backing the menu table. */
    private final ObservableList<Item> menuItems = FXCollections.observableArrayList();

    /**
     * Initializes the order menu view, setting the column formats and populating the 
     * table with available menu items.
     */
    private void initializeMenu() {
        menuIdColumn.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().id()));
        menuNameColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().name()));
        menuPriceColumn.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().basePrice()));

        ColumnCurrencyFormatSetter<Item, Number> currencyFormatSetter =
            new ColumnCurrencyFormatSetter<>("USD");
        currencyFormatSetter.applyTo(menuPriceColumn);

        menuTable.setItems(menuItems);
    }

    /**
     * Refreshes the menu table with items from the database, alerting the user on error.   
     */
    private void refreshMenu() {
        try {
            menuItems.setAll(itemDAO.getAll());
        } catch (SQLException e) {
            dbError(e);
        }
    }

    /**
     * Prompts user to add a new menu item and associated recipe
     */
    @FXML
    private void addMenuItem() {
        List<InventoryItem> inventory;
        try {
            inventory = inventoryDAO.getAll();
        } catch (SQLException e) {
            dbError(e);
            return;
        }

        Dialog<Recipe> dialog = new Dialog<>();
        dialog.setTitle("Add Menu Item");

        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        TextField nameField = new TextField();
        nameField.setPromptText("Drink name");
        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        Map<InventoryItem, TextField> qtyFields = new LinkedHashMap<>();
        GridPane recipeGrid = new GridPane();
        recipeGrid.setHgap(10);
        recipeGrid.setVgap(6);
        int row = 0;
        for (InventoryItem inv : inventory) {
            TextField qty = new TextField();
            qty.setPromptText("0");
            qty.setPrefWidth(70);
            qtyFields.put(inv, qty);
            recipeGrid.addRow(row++, new Label(inv.name()), qty, new Label(inv.unit()));
        }
        ScrollPane scroll = new ScrollPane(recipeGrid);
        scroll.setFitToWidth(true);
        scroll.setPrefViewportHeight(250);

        VBox form = new VBox(10,
            new Label("Drink Name"), nameField,
            new Label("Price"), priceField,
            new Label("Recipe (amount used per drink)"), scroll
        );
        dialog.getDialogPane().setContent(form);

        Node addNode = dialog.getDialogPane().lookupButton(addButton);
        addNode.addEventFilter(ActionEvent.ACTION, ev -> {
            if (buildRecipe(nameField, priceField, qtyFields) == null) {
                ev.consume(); 
                new Alert(Alert.AlertType.WARNING,
                    "Enter a name, a non-negative price, and whole-number ingredient amounts (blank = 0). "
                    + "At least one ingredient is required.").showAndWait();
            }
        });

        dialog.setResultConverter(button ->
            button == addButton ? buildRecipe(nameField, priceField, qtyFields) : null);

        dialog.showAndWait().ifPresent(recipe -> {
            try {
                Item newItem = itemDAO.insert(recipe.item().name(), recipe.item().basePrice());
                recipeDAO.insert(newItem, recipe.recipe());
                refreshMenu();
            } catch (SQLException e) {
                dbError(e);
            }
        });
    }

    /**
     * Builds a recipe from the values entered in the add-menu-item dialog.
     *
     * @param nameField the text field containing the item name
     * @param priceField the text field containing the item base price
     * @param qtyFields the quantity fields keyed by inventory item
     * @return the constructed recipe, or {@code null} if the input is invalid
     */
    private Recipe buildRecipe(TextField nameField, TextField priceField,
                            Map<InventoryItem, TextField> qtyFields) {
        try {
            String name = nameField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            if (name.isEmpty() || !Double.isFinite(price) || price < 0) return null;

            Map<InventoryItem, Integer> quantities = new LinkedHashMap<>();
            for (var entry : qtyFields.entrySet()) {
                String text = entry.getValue().getText().trim();
                if (text.isEmpty()) continue;
                int qty = Integer.parseInt(text);
                if (qty < 0) return null;
                if (qty > 0) quantities.put(entry.getKey(), qty);
            }
            if (quantities.isEmpty()) return null;

            return new Recipe(new Item(0, name, price), quantities);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Prompts the user with a dialog to modify the currently selected menu item.
     * On confirmation, updates the data associated with the selected menu item in the 
     * database on success, and alerts the user with an error on database failure.
     */
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