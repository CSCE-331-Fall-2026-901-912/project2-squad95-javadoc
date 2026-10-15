package com.app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginCtrl {
    
    @FXML private TextField employeeIdField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin(ActionEvent event) {
        String idText = employeeIdField.getText().trim();
        if (idText.isEmpty()) {
            errorLabel.setText("Please enter your Employee ID.");
            return;
        }

        String sql = "SELECT name, role FROM employees WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, Integer.parseInt(idText));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                boolean isManager = "manager".equalsIgnoreCase(role);
                int employeeId = Integer.parseInt(idText);
                
                Session.employeeId = employeeId; 

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/cashier.fxml"));
                Parent root = loader.load();
                
                CashierCtrl cashierCtrl = loader.getController();
                cashierCtrl.setManagerAccess(isManager);
                
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root, 1280, 720));
                stage.show();
            } else {
                errorLabel.setText("Invalid Employee ID");
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("ID must be a number");
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Database error occurred");
        }
    }
}