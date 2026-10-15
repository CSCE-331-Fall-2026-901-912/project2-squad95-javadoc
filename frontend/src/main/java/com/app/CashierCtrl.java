package com.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;

import com.app.controllers.*;
import com.app.models.*;

import java.util.Set;
import java.util.stream.Collectors;

import javafx.beans.value.*;

import java.net.URL;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import io.github.cdimascio.dotenv.Dotenv;

//maybe not needed
import java.util.List;

public class CashierCtrl extends Application implements Initializable {

    private static final ItemDAO itemDAO = new ItemDAO();
    private static final ToppingDAO toppingDAO = new ToppingDAO();
    private static final OrderDAO orderDAO = new OrderDAO();

    private volatile List<Topping> toppings = List.of();
    private Button selectedDrink = null;
    private Item selectedItem = null;
    private String selectedSugar = "100%";
    private String selectedIce = "Regular Ice";

    // Note: fx:id and variable name *must match* because of JavaFX reflection 
    @FXML 
    private ListView<Order> ordersList;
    // NOTE: cellfactory is an alternative but that's for phase 4/5
    final ObservableList<Order> orderEntries = FXCollections.observableArrayList();

    @FXML 
    private ListView<OrderItem> orderItemsList;

    @FXML 
    private Button newOrderButton;

    @FXML
    private Button checkoutButton;

    @FXML private Button deleteOrderButton;

    @FXML private Button managerViewButton;

    // the working id used for tracking the currently selected
    // order. this should be transformed into a different id
    // for inserting into the database when submitted
    final static int NO_ORDER_SELECTED = -1;
    private static int currProgOrderId = NO_ORDER_SELECTED;

    // the id of the most recently created order. used for creating new orders
    private static int latestProgOrderId = 0; 

    @FXML
    private TilePane menuItemsPane;
    final ObservableList<ShopMenuItem> menuItems = FXCollections.observableArrayList();
    
    private void onNewOrderClick(ActionEvent event) {
        ObservableList<OrderItem> orderList = FXCollections.observableArrayList();
        ++latestProgOrderId;
        Order newOrder = new Order(latestProgOrderId, orderList);
        orderEntries.add(newOrder);
    }

    private void initializeOrderLists() {
        newOrderButton.setOnAction(this::onNewOrderClick);
        
        // https://stackoverflow.com/questions/13264017/getting-selected-element-from-listview 
        ordersList.getSelectionModel().selectedItemProperty().addListener( (observable, oldVal, newVal) -> {
            Order currSelectedOrder = newVal;
            // break early if no list item is selected
            if (currSelectedOrder == null) { 
                orderItemsList.setItems(null);
                currProgOrderId = NO_ORDER_SELECTED;
                return; 
            }

            currProgOrderId = currSelectedOrder.id();

            orderItemsList.setItems(currSelectedOrder.orderItems());
        });

        // TODO for phase 4: delete/modify existing order item 
    }

    private boolean removeFirstOrderOfId(int targetId) {
        // Linear search, because id =/= list index
        // would be better to use an observable map later on
        for (int i = 0; i < orderEntries.size(); ++i) {
            Order entry = orderEntries.get(i);
            if (entry.id() == targetId) {
                orderEntries.remove(i);
                return true;          
            }
        }

        String errorMessage = String.format("Failed to remove order of ID %d", currProgOrderId);
        System.err.println(errorMessage);
        return false;
    }

    private int getOrderIndexOfId(int targetId) {
        for (int i = 0; i < orderEntries.size(); ++i) {
            Order entry = orderEntries.get(i);
            if (entry.id() == targetId) {
                return i;
            }
        }

        String errorMessage = String.format("Failed to get order of ID %d", currProgOrderId);
        System.err.println(errorMessage);
        return -1;
    }

    private void onDeleteOrder(ActionEvent event) {
        removeFirstOrderOfId(currProgOrderId);
    } 

    @FXML
    private Label orderPreviewLabel;
    private double selectedDrinkPrice = 0.0;
    private double toppingTotal = 0.0;

    public void initialize1() {
        System.out.println(new java.io.File("items.csv").getAbsolutePath());

        try (BufferedReader reader = new BufferedReader(new FileReader("items.csv"))) {

            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                String name = data[1];
                double price = Double.parseDouble(data[2]);

                Button button = new Button(name);

                button.setPrefSize(100, 60);
                button.setWrapText(true);

                button.setStyle("-fx-background-color: lightgray;");

                button.setOnAction(event -> {

                    if (selectedDrink != null) {
                        selectedDrink.setStyle("-fx-background-color: lightgray;");
                    }

                    button.setStyle("-fx-background-color: lightgreen;");

                    selectedDrink = button;
                    selectedDrinkPrice = price;

                    System.out.println("Selected: " + name);
                    System.out.println("Price: $" + price);
                    
                    showToppingsPopup();
                });

                menuItemsPane.getChildren().add(button);
            }

        } catch (IOException e) {
            System.out.println("FAILED TO READ items.csv");
            e.printStackTrace();
        }
    }

    private void addItemToCurrOrder(OrderItem item) {
        int currOrderIndex = getOrderIndexOfId(currProgOrderId);
        // handle case where no order is selected 
        if (currOrderIndex < 0) return;
        orderEntries.get(currOrderIndex).orderItems().add(item);
    }

    /**
     * Toggles the visibility of the manager view button based on the logged-in user's role.
     * @param isManager true if the user is a manager, false if they are a cashier.
     */
    public void setManagerAccess(boolean isManager) {
        if (managerViewButton != null) {
            managerViewButton.setVisible(isManager);
            managerViewButton.setManaged(isManager);
        }
    }

    // TODO: speed up manager switching because it's a PITA to load for debugging everytime
    @FXML
    private void switchToManager(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/manager.fxml")
        );

        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource())
            .getScene().getWindow();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private int loggedInEmployeeId = -1;

    /**
     * Sets the ID of the employee currently using the register.
     */
    public void setEmployeeId(int employeeId) {
        this.loggedInEmployeeId = employeeId;
    }

    private void onCheckout(ActionEvent event) {
        int idx = getOrderIndexOfId(currProgOrderId);
        if (idx < 0) {
            new Alert(Alert.AlertType.WARNING, "Select an order first.").showAndWait();
            return;
        }
        final Order orderToProcess = orderEntries.get(idx);
        
        if (orderToProcess.orderItems().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "This order is empty.").showAndWait();
            return;
        }

        checkoutButton.setDisable(true);
        Task<OrderReceipt> task = new Task<>() {
            @Override protected OrderReceipt call() throws Exception {
                return orderDAO.checkout(orderToProcess, loggedInEmployeeId, Session.taxRate);
            }
        };
        task.setOnSucceeded(e -> {
            checkoutButton.setDisable(false);
            OrderReceipt r = task.getValue();
            removeFirstOrderOfId(orderToProcess.id());
            new Alert(Alert.AlertType.INFORMATION, String.format(
                "Order #%d placed.%nSubtotal: $%.2f%nTax: $%.2f%nTotal: $%.2f",
                r.orderId(), r.subtotal(), r.tax(), r.grandTotal())).showAndWait();
        });
        task.setOnFailed(e -> {
            checkoutButton.setDisable(false);
            showError(task.getException());
        });
        new Thread(task).start();
    }

    private void showToppingsPopup() {
        Stage popup = new Stage();
        popup.setTitle("Select Toppings");

        FlowPane toppingsPane = new FlowPane(10, 10);
        toppingsPane.setPadding(new Insets(20));
        toppingsPane.setPrefWrapLength(400);

        Set<Topping> selectedToppings = new LinkedHashSet<>();

        for (Topping t : toppings) {
            Button button = new Button(t.name() + "\n$" + String.format("%.2f", t.basePrice()));
            button.setPrefSize(120, 60);
            button.setWrapText(true);
            button.setStyle("-fx-background-color: lightgray;");
            button.setOnAction(event -> {
                if (selectedToppings.remove(t)) {
                    button.setStyle("-fx-background-color: lightgray;");
                } else {
                    selectedToppings.add(t);
                    button.setStyle("-fx-background-color: lightgreen;");
                }
            });
            toppingsPane.getChildren().add(button);
        }

        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(event -> {
            popup.close();
            showSugarPopup(selectedToppings);
        });

        VBox root = new VBox(20, toppingsPane, confirmButton);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        popup.setScene(new Scene(root, 500, 450));
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.showAndWait();
    }

    private void showSugarPopup(Set<Topping> chosenToppings) {
        Stage popup = new Stage();
        popup.setTitle("Select Sugar Level");
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.getChildren().add(new Label("Choose Sugar Level"));

        ToggleGroup group = new ToggleGroup();
        for (String level : new String[]{"0%", "25%", "50%", "75%", "100%"}) {
            RadioButton rb = new RadioButton(level);
            rb.setToggleGroup(group);
            if (level.equals("100%")) rb.setSelected(true);
            root.getChildren().add(rb);
        }

        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(event -> {
            selectedSugar = ((RadioButton) group.getSelectedToggle()).getText();
            popup.close();
            showIcePopup(chosenToppings);
        });
        root.getChildren().add(confirmButton);

        popup.setScene(new Scene(root, 350, 350));
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.show();
    }

    private void showIcePopup(Set<Topping> chosenToppings) {
        Stage popup = new Stage();
        popup.setTitle("Select Ice Level");
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.getChildren().add(new Label("Choose Ice Level"));

        ToggleGroup group = new ToggleGroup();
        for (String level : new String[]{"No Ice", "Less Ice", "Regular Ice", "Extra Ice"}) {
            RadioButton rb = new RadioButton(level);
            rb.setToggleGroup(group);
            if (level.equals("Regular Ice")) rb.setSelected(true);
            root.getChildren().add(rb);
        }

        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(event -> {
            selectedIce = ((RadioButton) group.getSelectedToggle()).getText();

            OrderItem completed = new OrderItem(selectedItem, selectedIce, selectedSugar,
                                                List.copyOf(chosenToppings));
            
            orderPreviewLabel.setText(String.format(
            "Drink: %s%nDrink Price: $%.2f%nToppings: %s%nSugar Level: %s%nIce Level: %s%nTotal Price: $%.2f",
            selectedItem.name(), completed.price(),
            chosenToppings.isEmpty() ? "None"
                : chosenToppings.stream().map(Topping::name).collect(Collectors.joining(", ")),
            selectedSugar, selectedIce, completed.totalPrice()));

            addItemToCurrOrder(completed);
            popup.close();
        });
        root.getChildren().add(confirmButton);

        popup.setScene(new Scene(root, 350, 350));
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.show();
    }
    
    
    private void loadMenu() {
        Task<List<Item>> task = new Task<>() {
            @Override protected List<Item> call() throws Exception {
                toppings = toppingDAO.getAll();
                return itemDAO.getAll();
            }
        };
        task.setOnSucceeded(e -> populateMenuPane(task.getValue()));
        task.setOnFailed(e -> showError(task.getException()));
        new Thread(task).start();
    }
    
    private void populateMenuPane(List<Item> items) {
        menuItemsPane.getChildren().clear();
        for (Item item : items) {
            Button button = new Button(item.name() + "\n$" + String.format("%.2f", item.basePrice()));
            button.setPrefSize(100, 60);
            button.setWrapText(true);
            button.setStyle("-fx-background-color: lightgray;");
            button.setOnAction(event -> {
                boolean validOrderSelected = currProgOrderId != NO_ORDER_SELECTED;
                if (!validOrderSelected) {
                    new Alert(Alert.AlertType.WARNING,
                        "Please select an order first.").showAndWait();
                    return;
                }

                if (selectedDrink != null) selectedDrink.setStyle("-fx-background-color: lightgray;");
                button.setStyle("-fx-background-color: lightgreen;");
                selectedDrink = button;
                selectedItem = item;
                showToppingsPopup();
            });
            menuItemsPane.getChildren().add(button);
        }
    }

    private static void showError(Throwable ex) {
        ex.printStackTrace();
        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Database error: " + ex.getMessage()).showAndWait());
    }

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) { 
        // https://blueskyworkshop.com/topics/Java-Pages/fx-ListView-Basics/
        ordersList.setItems(orderEntries);

        loadMenu();

        // ordersList isn't available when start() is called, so
        // populate it here instead
        initializeOrderLists();

        checkoutButton.setOnAction(this::onCheckout);
        deleteOrderButton.setOnAction(this::onDeleteOrder);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Set the controller manually to avoid needing to respecify fx:controller 
        // attribute again for every .fxml import. fx:controller must not be set though
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml")); // Changed to login.fxml
        // loader.setController(new CashierCtrl());
        Parent root = loader.load();
    
        Scene scene = new Scene(root, 1280, 720);
    
        primaryStage.setTitle("POS System");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch();
    }

}