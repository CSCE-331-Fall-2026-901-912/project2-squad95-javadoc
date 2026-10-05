package com.app;
// import java.io.IOException;
// import java.util.List;
// import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.application.Application;



public class CashierCtrl extends Application {
    
    @Override
    public void start(Stage stage) {
        // Unrelated: to refer to an fxml file from here, do "/file.fxml", because the 
        // "root" is automatically src/main/resources/

        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        Scene scene = new Scene(new StackPane(l), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}