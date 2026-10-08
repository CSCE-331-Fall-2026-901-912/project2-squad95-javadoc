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
}