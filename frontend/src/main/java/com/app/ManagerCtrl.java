package com.app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
}