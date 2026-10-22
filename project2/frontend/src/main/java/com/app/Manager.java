/**
 * Manager.java
 * This is a class created for the Manager dashboard for the application.
 * It starts the application, loads the FXML file for login,
 * then sets the whole scene for the primary stage
 * @author: Christopher Jermia
 * @author: Kenny Le
 * @author: Jeffrey Yu
 * @author: Ethan Faulkner
 * @author: Andrew Schrick
 */

package com.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Application entry point that launches the login screen.
 */
public class Manager extends Application {
    /** Creates the application instance. */
    public Manager() {}

    /**
     * Start: the main entry point for JavaFX applications.
     * Loads the FXML file for the login screen and sets it 
     * as the scene for the primary stage.
     *
     * @param primaryStage The primary stage for this application - with scenes
     * @throws Exception if an error occurs during loading of the FXML file.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        // 4:3 display ratio, can be adjusted per user preference
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setScene(new Scene(root, 1024, 768));
        primaryStage.show();
    }

    /**
     * Main method: the launcher for the JavaFX application.
     * @param args as arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}