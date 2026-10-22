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


/**
 * CashierCtrl
 * Controller class for the cashier interface.
*/
public class CashierCtrl extends Application implements Initializable {

    /** Represents the database table for order items. */
    private static final ItemDAO itemDAO = new ItemDAO();
    /** Represents the database table for toppings. */
    private static final ToppingDAO toppingDAO = new ToppingDAO();
    /** Represents the database table for the order history. */
    private static final OrderDAO orderDAO = new OrderDAO();

    /** The list of toppings loaded from the database. Used by the toppings selection popup. */
    private volatile List<Topping> toppings = List.of();
    /** Tracks information from the selected drink button to be used in creating an order item. */
    private Button selectedDrink = null;
    /** Tracks the selected item used in creating an order item. */
    private Item selectedItem = null;
    /** Tracks the user-selected sugar level used in creating an order item.*/
    private String selectedSugar = "100%";
    /** Tracks the user-selected ice level used in creating an order item. */
    private String selectedIce = "Regular Ice";

    // Note: fx:id and variable name *must match* because of JavaFX reflection 
    /** Displays the current list of orders to be modified and submitted to the database. */
    @FXML private ListView<Order> ordersList;
    // NOTE: cellfactory is an alternative but that's for phase 4/5
    /** Hold internal data for the current list of orders to be modified and submitted to the database. */
    final ObservableList<Order> orderEntries = FXCollections.observableArrayList();

    /** Displays the current list of items for the currently selected order. */
    @FXML private ListView<OrderItem> orderItemsList;

    /** Button object for creating a new order. Mapped to its corresponding element in the cashier.fxml file. */
    @FXML private Button newOrderButton;

    /** Button object for submitting an order. Mapped to its corresponding element in the cashier.fxml file. */
    @FXML private Button checkoutButton;

    /** Button object for deleting the selected order. Mapped to its corresponding element in the cashier.fxml file. */
    @FXML private Button deleteOrderButton;
    
    /** 
     * Button object for switching to the manager view for users with appropriate permissions. 
     * Mapped to its corresponding element in the cashier.fxml file. 
    */
    @FXML private Button managerViewButton;

    /** Button object for editing the selected order's notes. Mapped to its corresponding element in the cashier.fxml file. */
    @FXML private Button notesButton;

    /** A constant signifying that no order is currently selected. */
    final static int NO_ORDER_SELECTED = -1;


    /**
     * The working ID used for tracking the currently selected
     * order. This should be transformed into a different ID
     * for inserting into the database when submitted.
     */
    private static int currProgOrderId = NO_ORDER_SELECTED;

    /** The ID of the most recently created order. Internally used for creating new orders. */
    private static int latestProgOrderId = 0; 

    /** A TilePane displaying available menu items from the database. */
    @FXML private TilePane menuItemsPane;
    /** Holds the data of menuItems displayed by menuItemsPane. */
    final ObservableList<ShopMenuItem> menuItems = FXCollections.observableArrayList();
    
    /**
     * Appends a new order to the internal list of order entries.
     * @author Kenny Le
     * @param event Event object provided by the click event. Unused.
     */
    private void onNewOrderClick(ActionEvent event) {
        ObservableList<OrderItem> orderList = FXCollections.observableArrayList();
        ++latestProgOrderId;
        Order newOrder = new Order(latestProgOrderId, orderList);
        orderEntries.add(newOrder);
    }

    /**
     * Connects the order list UI to their respective actions and listeners.
     * Should only be called once during program startup.
     * @author Kenny Le
     */
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

    /**
     * Searches the internal order entries list to remove the first element matching the given ID.
     * @author Kenny Le
     * @param targetId The ID of the order to find and remove. This is not the index of the order.
     * @return Whether or not an order of matching targetId was found and removed.
     */
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

    /**
     * Searches the internal order entries list to return the index of first element matching the given ID.
     * @author Kenny Le
     * @param targetId The ID of the order to get. This is not the index of the order.
     * @return The index of the matched order in the order entries list, or -1 if not found.
     */
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

    /**
     * Method used by a button to delete orders. Deletes the currently selected order.
     * @author Kenny Le
     * @param event Unused button event object.
     */
    private void onDeleteOrder(ActionEvent event) {
        removeFirstOrderOfId(currProgOrderId);
    } 

    /** Displays information about the currently selected order item in an order. */
    @FXML private Label orderPreviewLabel;
    // private double selectedDrinkPrice = 0.0;
    // private double toppingTotal = 0.0;

    /**
     * Populates the menu pane with a list of items given in the resources/items.csv file.
     * Silently prints to the console upon any IO errors.
     * @author Jeffrey Yu
     */
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
                    // selectedDrinkPrice = price;

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

    /**
     * Appends the order item to the currently selected order.
     * Does nothing if no order is selected.
     * @param item The OrderItem to append to the currently selected order.
     */
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

    /**
     * Changes the current view of the application from Cashier to Manager by
     * reading manager.fxml. This is done on every call.
     * @param event Unused button event.
     * @throws IOException manager.fxml is malformed or missing from resources/. 
     */
    @FXML
    private void switchToManager(ActionEvent event) throws IOException {
        // TODO: speed up manager switching because it's a PITA to load for debugging everytime
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

    // private int loggedInEmployeeId = -1;

    // /**
    //  * Sets the ID of the employee currently using the register.
    //  * @param employeeId The ID of the employee logging in.
    //  */
    // public void setEmployeeId(int employeeId) {
    //     // this.loggedInEmployeeId = employeeId;
    // }

    /**
     * Sends the currently selected order to the database, then removes it from the internal order entries list. 
     * Creates an alert if the selected order cannot be found or if the order has no items. 
     * Creates a confirmation popup on success.
     * Used as an action for the checkout button.
     * @author Kenny Le
     * @author Ethan Faulkner
     * @param event Unused button event.
     */
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
                // Now pulls the globally saved ID from the Session
                return orderDAO.checkout(orderToProcess, Session.employeeId, Session.taxRate);
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


    /**
     * Constructs a popup for the employee to select toppings for the current drink to be added.
     * This popup is populated with a cached set of toppings from the database.
     * showSugarPopup() is called when the confirm button is clicked. 
     * @author Jeffrey Yu
     */
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

    /**
     * Constructs a popup for the employee to select the sugar level for the current drink to be added.
     * showIcePopup() is called when the confirm button is clicked. 
     * @author Jeffrey Yu
     */
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

    /**
     * Constructs a popup for the employee to select the ice level for the current drink to be added.
     * Creates and appends a new order item to the order's entries when Confirm is clicked. 
     * @author Jeffrey Yu
     */
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
    
    /**
     * Fetches menu items from the database, then attempts to populate the menu pane with fetched menu items.
     * Shows an error message if fetching from the database fails.
     */
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
    
    /**
     * Populates the menu pane with buttons from the list of Item objects passed to this method, and 
     * assigns styling and action events 
     * @param items
     */
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

    /**
     * For errors related to the database. Prints the stack trace leading to the error
     * and displays an error message to the user.
     * @param ex The database exception object holding relevant error information.
     */
    private static void showError(Throwable ex) {
        ex.printStackTrace();
        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Database error: " + ex.getMessage()).showAndWait());
    }

    /**
     * Button action for creating a popup menu allowing the user to edit the notes
     * for the currently selected order. Saves user-inputted text for the currently
     * selected order when the confirmButton is clicked.
     * @author Kenny Le
     * @param event Unused button event.
     */
    private void onOpenOrderNotes(ActionEvent event) {
        int idx = getOrderIndexOfId(currProgOrderId);
        if (idx < 0) {
            new Alert(Alert.AlertType.WARNING, "Select an order first.").showAndWait();
            return;
        }
        final Order orderToNote = orderEntries.get(idx);

        Stage popup = new Stage();
        popup.setTitle("Notes for Order " + Integer.toString(orderToNote.id()));
        VBox root = new VBox(10);
        root.alignmentProperty().set(Pos.TOP_CENTER);
        root.setPadding(new Insets(5, 5, 10, 5));

        TextArea textArea = new TextArea();
        textArea.setText(orderToNote.notes());
        textArea.setPrefHeight(600);

        Button confirmButton = new Button("Save and Close");
        confirmButton.setOnAction(e -> {
            orderToNote.notes = textArea.getText();
            popup.close();
        });

        root.getChildren().addAll(textArea, confirmButton);

        popup.setScene(new Scene(root, 350, 350));
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.show();
    }

    /** 
     * Initializes the cashier interface when called after login, 
     * attaching the appropriate data and actions to views and buttons in the UI. 
     * @param location Unused URL parameter.
     * @param resourceBundle Unused ResourceBundle parameter.
     */
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

        notesButton.setOnAction(this::onOpenOrderNotes);
    }

    /**    (non-Javadoc)
     * First loads the login scene for the interface (see java.com.app.LoginCtrl),
     * which should load the cashier scene after a successful login.
     * Sets the initial size and name of the application.
     * @param primaryStage The stage object used by the main application.
     * @throws Exception Exception may be thrown if the login.fxml file is malformed.
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
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

    /**
     * Launches the application. The start() method is indirectly invoked.
     * @param args Command line arguments, which are unused.
     */
    public static void main(String[] args) {
        launch();
    }

}