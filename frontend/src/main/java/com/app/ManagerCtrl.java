package com.app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

import javafx.scene.control.*;
import javafx.collections.*;

import javafx.beans.property.*;

import java.io.BufferedReader;
import java.io.FileReader;

public class ManagerCtrl {

    // Views 
    @FXML private VBox dashboardView;
    @FXML private HBox financialsView;
    @FXML private VBox ordersView;
    @FXML private VBox employeesView;
    @FXML private VBox inventoryFullView;

    @FXML
    public void initialize() {
        showDashboard(null);
        initializeEmployees();
        initializeInventory();
        loadMenuItems();
        initializeMenu();
    }

    // Navigation 

    @FXML
    private void showDashboard(ActionEvent event) {
        dashboardView.setVisible(true);
        dashboardView.setManaged(true);
        
        financialsView.setVisible(false);
        financialsView.setManaged(false);
        
        ordersView.setVisible(false);
        ordersView.setManaged(false);

        employeesView.setVisible(false);
        employeesView.setManaged(false);

        inventoryFullView.setVisible(false);
        inventoryFullView.setManaged(false);

        menuView.setVisible(false);
        menuView.setManaged(false);
    }

    @FXML
    private void showFinancials(ActionEvent event) {
        dashboardView.setVisible(false);
        dashboardView.setManaged(false);
        
        financialsView.setVisible(true);
        financialsView.setManaged(true);
        
        ordersView.setVisible(false);
        ordersView.setManaged(false);

        employeesView.setVisible(false);
        employeesView.setManaged(false);

        inventoryFullView.setVisible(false);
        inventoryFullView.setManaged(false);

        menuView.setVisible(false);
        menuView.setManaged(false);
    }

    @FXML
    private void showMenu(ActionEvent event) {
        dashboardView.setVisible(false);
        dashboardView.setManaged(false);

        financialsView.setVisible(false);
        financialsView.setManaged(false);

        ordersView.setVisible(false);
        ordersView.setManaged(false);

        employeesView.setVisible(false);
        employeesView.setManaged(false);

        inventoryFullView.setVisible(false);
        inventoryFullView.setManaged(false);

        menuView.setVisible(true);
        menuView.setManaged(true);
    }
    
    @FXML
    private void showOrders(ActionEvent event) {
        dashboardView.setVisible(false);
        dashboardView.setManaged(false);
        
        financialsView.setVisible(false);
        financialsView.setManaged(false);
        
        ordersView.setVisible(true);
        ordersView.setManaged(true);

        employeesView.setVisible(false);
        employeesView.setManaged(false);

        inventoryFullView.setVisible(false);
        inventoryFullView.setManaged(false);

        menuView.setVisible(false);
        menuView.setManaged(false);
    }

    @FXML
    private void showEmployees(ActionEvent event) {
        dashboardView.setVisible(false);
        dashboardView.setManaged(false);
        
        financialsView.setVisible(false);
        financialsView.setManaged(false);
        
        ordersView.setVisible(false);
        ordersView.setManaged(false);

        employeesView.setVisible(true);
        employeesView.setManaged(true);

        inventoryFullView.setVisible(false);
        inventoryFullView.setManaged(false);

        menuView.setVisible(false);
        menuView.setManaged(false);
    }

    @FXML
    private void showInventory(ActionEvent event) {
        dashboardView.setVisible(false);
        dashboardView.setManaged(false);
        
        financialsView.setVisible(false);
        financialsView.setManaged(false);
        
        ordersView.setVisible(false);
        ordersView.setManaged(false);

        employeesView.setVisible(false);
        employeesView.setManaged(false);

        inventoryFullView.setVisible(true);
        inventoryFullView.setManaged(true);

        menuView.setVisible(false);
        menuView.setManaged(false);
    }

    @FXML
    private void switchToCashier(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/cashier.fxml")
        );

        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource())
            .getScene().getWindow();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML private TableView<Employee> employeesFullTable;

    @FXML private TableColumn<Employee, String> cashierColumn;
    @FXML private TableColumn<Employee, Number> hoursColumn;
    @FXML private TableColumn<Employee, Number> salesColumn;
    @FXML private TableColumn<Employee, Boolean> managerColumn;

    private final ObservableList<Employee> employees =
        FXCollections.observableArrayList();

    public class Employee {
        private final StringProperty name;
        private final DoubleProperty hours;
        private final DoubleProperty sales;
        private final BooleanProperty manager;

        public Employee(String name, double hours,
                        double sales, boolean manager) {
            this.name = new SimpleStringProperty(name);
            this.hours = new SimpleDoubleProperty(hours);
            this.sales = new SimpleDoubleProperty(sales);
            this.manager = new SimpleBooleanProperty(manager);
        }

        public String getName() { return name.get(); }
        public double getHours() { return hours.get(); }
        public double getSales() { return sales.get(); }
        public boolean isManager() { return manager.get(); }

        public void setName(String value) { name.set(value); }
        public void setHours(double value) { hours.set(value); }
        public void setSales(double value) { sales.set(value); }
        public void setManager(boolean value) { manager.set(value); }
    }

    private void initializeEmployees() {
        cashierColumn.setCellValueFactory(
            data -> new SimpleStringProperty(data.getValue().getName())
        );

        hoursColumn.setCellValueFactory(
            data -> new SimpleDoubleProperty(data.getValue().getHours())
        );

        salesColumn.setCellValueFactory(
            data -> new SimpleDoubleProperty(data.getValue().getSales())
        );

        managerColumn.setCellValueFactory(
            data -> new SimpleBooleanProperty(data.getValue().isManager())
        );

        employeesFullTable.setItems(employees);
    }

    @FXML
    private void addEmployee() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Employee");
        dialog.setHeaderText("Enter employee name");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.isBlank()) {
                employees.add(new Employee(name.trim(), 0, 0, false));
            }
        });
    }

    @FXML
    private void updateEmployee() {
        Employee selected =
            employeesFullTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            return;
        }

        TextInputDialog dialog =
            new TextInputDialog(selected.getName());

        dialog.setTitle("Update Employee");
        dialog.setHeaderText("Update employee name");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.isBlank()) {
                selected.setName(name.trim());
                employeesFullTable.refresh();
            }
        });
    }

    @FXML
    private void removeEmployee() {
        Employee selected =
            employeesFullTable.getSelectionModel().getSelectedItem();

        if (selected != null) {
            employees.remove(selected);
        }
    }

    @FXML
    private void promoteEmployee() {
        Employee selected =
            employeesFullTable.getSelectionModel().getSelectedItem();

        if (selected != null) {
            selected.setManager(true);
            employeesFullTable.refresh();
        }
    }





    public class InventoryItem {
        private final IntegerProperty id;
        private final StringProperty name;
        private final DoubleProperty quantity;
        private final StringProperty unit;

        public InventoryItem(int id, String name,
                            double quantity, String unit) {
            this.id = new SimpleIntegerProperty(id);
            this.name = new SimpleStringProperty(name);
            this.quantity = new SimpleDoubleProperty(quantity);
            this.unit = new SimpleStringProperty(unit);
        }

        public int getId() { return id.get(); }
        public String getName() { return name.get(); }
        public double getQuantity() { return quantity.get(); }
        public String getUnit() { return unit.get(); }

        public void setQuantity(double value) {
            quantity.set(value);
        }
    }

    @FXML private TableView<InventoryItem> inventoryFullTable;

    @FXML private TableColumn<InventoryItem, Number> inventoryIdColumn;
    @FXML private TableColumn<InventoryItem, String> inventoryNameColumn;
    @FXML private TableColumn<InventoryItem, Number> inventoryQuantityColumn;
    @FXML private TableColumn<InventoryItem, String> inventoryUnitColumn;

    private final ObservableList<InventoryItem> inventoryItems = FXCollections.observableArrayList();

    private void initializeInventory() {
        inventoryIdColumn.setCellValueFactory(
            data -> new SimpleIntegerProperty(data.getValue().getId())
        );

        inventoryNameColumn.setCellValueFactory(
            data -> new SimpleStringProperty(data.getValue().getName())
        );

        inventoryQuantityColumn.setCellValueFactory(
            data -> new SimpleDoubleProperty(data.getValue().getQuantity())
        );

        inventoryUnitColumn.setCellValueFactory(
            data -> new SimpleStringProperty(data.getValue().getUnit())
        );

        inventoryFullTable.setItems(inventoryItems);
    }

    @FXML
    private void addInventoryItem() {
        Dialog<InventoryItem> dialog = new Dialog<>();
        dialog.setTitle("Add Inventory Item");

        ButtonType addButton =
            new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(
            addButton, ButtonType.CANCEL
        );

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
            if (button == addButton) {
                try {
                    String name = nameField.getText().trim();
                    String unit = unitField.getText().trim();
                    double quantity =
                        Double.parseDouble(quantityField.getText().trim());

                    if (name.isEmpty() || unit.isEmpty()
                            || !Double.isFinite(quantity) || quantity < 0) {
                        return null;
                    }

                    int nextId = inventoryItems.stream()
                        .mapToInt(InventoryItem::getId)
                        .max().orElse(-1) + 1;

                    return new InventoryItem(nextId, name, quantity, unit);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(inventoryItems::add);
    }


    @FXML
    private void updateInventoryQuantity() {
        InventoryItem selected =
            inventoryFullTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            new Alert(Alert.AlertType.WARNING,
                "Please select an inventory item first.")
                .showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog(
            String.valueOf(selected.getQuantity())
        );

        dialog.setTitle("Update Inventory");
        dialog.setHeaderText("Update " + selected.getName());
        dialog.setContentText("New quantity:");

        dialog.showAndWait().ifPresent(input -> {
            try {
                double quantity = Double.parseDouble(input.trim());

                if (!Double.isFinite(quantity) || quantity < 0) {
                    throw new NumberFormatException();
                }

                selected.setQuantity(quantity);
                inventoryFullTable.refresh();

            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR,
                    "Please enter a valid non-negative quantity.")
                    .showAndWait();
            }
        });
    }




    public class MenuItem {
        private final IntegerProperty id;
        private final StringProperty name;
        private final DoubleProperty price;

        public MenuItem(int id, String name, double price) {
            this.id = new SimpleIntegerProperty(id);
            this.name = new SimpleStringProperty(name);
            this.price = new SimpleDoubleProperty(price);
        }

        public int getId() { return id.get(); }
        public String getName() { return name.get(); }
        public double getPrice() { return price.get(); }

        public void setName(String value) { name.set(value); }
        public void setPrice(double value) { price.set(value); }
    }

    @FXML private VBox menuView;

    @FXML private TableView<MenuItem> menuTable;

    @FXML private TableColumn<MenuItem, Number> menuIdColumn;
    @FXML private TableColumn<MenuItem, String> menuNameColumn;
    @FXML private TableColumn<MenuItem, Number> menuPriceColumn;

    private final ObservableList<MenuItem> menuItems = FXCollections.observableArrayList();

    private void initializeMenu() {
        menuIdColumn.setCellValueFactory(
            data -> new SimpleIntegerProperty(data.getValue().getId())
        );

        menuNameColumn.setCellValueFactory(
            data -> new SimpleStringProperty(data.getValue().getName())
        );

        menuPriceColumn.setCellValueFactory(
            data -> new SimpleDoubleProperty(data.getValue().getPrice())
        );

        menuTable.setItems(menuItems);
    }

    private void loadMenuItems() {
        menuItems.clear();

        try (BufferedReader reader =
                new BufferedReader(new FileReader("items.csv"))) {

            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);

                int id = Integer.parseInt(data[0].trim());
                String name = data[1].trim();
                double price = Double.parseDouble(data[2].trim());

                menuItems.add(new MenuItem(id, name, price));
            }

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addMenuItem() {
        Dialog<MenuItem> dialog = new Dialog<>();
        dialog.setTitle("Add Menu Item");

        ButtonType addButton =
            new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(
            addButton, ButtonType.CANCEL
        );

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
            if (button == addButton) {
                try {
                    String name = nameField.getText().trim();
                    double price = Double.parseDouble(
                        priceField.getText().trim()
                    );

                    if (name.isEmpty() || !Double.isFinite(price) || price < 0) {
                        return null;
                    }

                    int nextId = menuItems.stream()
                        .mapToInt(MenuItem::getId)
                        .max().orElse(-1) + 1;

                    return new MenuItem(nextId, name, price);

                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(menuItems::add);
    }

    @FXML
    private void updateMenuItem() {
        MenuItem selected =
            menuTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            new Alert(Alert.AlertType.WARNING,
                "Please select a menu item first.")
                .showAndWait();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Update Menu Item");

        dialog.getDialogPane().getButtonTypes().addAll(
            ButtonType.OK, ButtonType.CANCEL
        );

        TextField nameField =
            new TextField(selected.getName());

        TextField priceField =
            new TextField(String.valueOf(selected.getPrice()));

        VBox form = new VBox(10,
            new Label("Drink Name"), nameField,
            new Label("Price"), priceField
        );

        dialog.getDialogPane().setContent(form);

        dialog.showAndWait().ifPresent(button -> {
            if (button == ButtonType.OK) {
                try {
                    String name = nameField.getText().trim();
                    double price = Double.parseDouble(
                        priceField.getText().trim()
                    );

                    if (name.isEmpty() || !Double.isFinite(price) || price < 0) {
                        throw new IllegalArgumentException();
                    }

                    selected.setName(name);
                    selected.setPrice(price);

                    menuTable.refresh();

                } catch (IllegalArgumentException e) {
                    new Alert(Alert.AlertType.ERROR,
                        "Enter a valid name and non-negative price.")
                        .showAndWait();
                }
            }
        });
    }
}