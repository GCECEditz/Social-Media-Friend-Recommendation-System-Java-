/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package friendrecommendationsystem;

import java.util.List;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

/**
 * GraphDisplay.java
 * 
 * Main JavaFX application for displaying the social network graph.
 * This class creates the main window, initializes the graph with
 * sample data, and handles the sidebar information panel.
 * 
 * @author User
 */
public class GraphDisplay extends Application {
    
    //the graph data structure
    private AbstractGraph graph;
    
    //the canvas for drawing the graph
    private GraphCanvas canvas;
    
    //UI containers for dynamic updates
    private VBox userInfoBox;
    private Label nodeCountLabel;
    private Label edgeCountLabel;
    private ScrollPane userScrollPane;
    
    //dimensions for the graph canvas (initial size)
    private static final double CANVAS_WIDTH = 800;
    private static final double CANVAS_HEIGHT = 600;
    
    //dimensions for the sidebar
    private static final double SIDEBAR_WIDTH = 280;
    
    @Override
    public void start(Stage primaryStage) {
        //initialize the graph with sample data
        initializeSampleGraph();
        
        //create main layout
        BorderPane root = new BorderPane();
        
        //create canvas for graph display
        canvas = new GraphCanvas(graph);
        canvas.setPrefSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        
        //set the callback for when a node is clicked
        canvas.setOnNodeClicked(this::handleNodeClick);
        
        //create sidebar with controls and information
        VBox sidebar = createSidebar();
        
        //assemble layout
        root.setCenter(canvas);
        root.setRight(sidebar);
        
        //create scene and stage
        Scene scene = new Scene(root, CANVAS_WIDTH + SIDEBAR_WIDTH, CANVAS_HEIGHT);
        primaryStage.setTitle("Friend Recommendation System - Graph Display");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true); // Allow window resizing
        primaryStage.show();
    }
    
    /**
     * Creates the sidebar panel with title, statistics, user info, legend, and controls
     * @return VBox containing all sidebar elements
     */
    private VBox createSidebar() {
        //create title label
        Label titleLabel = new Label("Social Media Friend\nRecommendation System");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-alignment: center;");
        titleLabel.setMaxWidth(SIDEBAR_WIDTH - 40);
        titleLabel.setWrapText(true);
        
        Separator sep1 = new Separator();
        
        //create graph info section
        Label infoTitle = new Label("Graph Information");
        infoTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        
        nodeCountLabel = new Label("Vertices: " + graph.getSize());
        nodeCountLabel.setStyle("-fx-font-size: 11px;");
        
        edgeCountLabel = new Label("Edges: " + countEdges());
        edgeCountLabel.setStyle("-fx-font-size: 11px;");
        
        VBox statsBox = new VBox(5, infoTitle, nodeCountLabel, edgeCountLabel);
        statsBox.setPadding(new Insets(0, 0, 10, 0));
        
        Separator sep2 = new Separator();
        
        //create user info section (dynamically updates on click)
        userInfoBox = new VBox(5);
        userInfoBox.setPadding(new Insets(0, 0, 10, 0));
        showDefaultUserInfo();
        
        //wrap user info box in a scroll pane to prevent UI overflow
        userScrollPane = new ScrollPane(userInfoBox);
        userScrollPane.setFitToWidth(true);
        userScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        userScrollPane.setStyle("-fx-background-color: transparent; -fx-background: white;");
        //allow the scroll pane to grow and take up available vertical space
        VBox.setVgrow(userScrollPane, Priority.ALWAYS);
        
        Separator sep3 = new Separator();
        
        //create legend section
        Label legendTitle = new Label("Legend");
        legendTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        
        HBox legendDefault = createLegendItem("#1E90FF", "#000080", "User (Default)");
        HBox legendSelected = createLegendItem("#e74c3c", "#c0392b", "Selected User");
        HBox legendFriend = createLegendItem("#2ecc71", "#27ae60", "Direct Friend");
        HBox legendRecommend = createLegendItem("#f1c40f", "#f39c12", "BFS Recommendation");
        HBox legendOther = createLegendItem("#95a5a6", "#7f8c8d", "Other Users");
        HBox legendEdge = createLegendItem("#D3D3D3", "#808080", "Friendship (Edge)");
        
        VBox legendBox = new VBox(8, legendTitle, legendDefault, legendSelected, legendFriend, legendRecommend, legendOther, legendEdge);
        legendBox.setPadding(new Insets(0, 0, 10, 0));
        
        Separator sep4 = new Separator();
        
        //create controls section
        Label controlsTitle = new Label("Controls");
        controlsTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        
        Button refreshBtn = new Button("Refresh Graph");
        refreshBtn.setStyle("-fx-padding: 8 15; -fx-font-size: 11px; -fx-cursor: hand;");
        refreshBtn.setMaxWidth(Double.MAX_VALUE);
        refreshBtn.setOnAction(e -> {
            canvas.refresh();
            updateStatistics();
            showDefaultUserInfo(); 
        });
        
        Button addUserBtn = new Button("Add Friend (User)");
        addUserBtn.setStyle("-fx-padding: 8 15; -fx-font-size: 11px; -fx-cursor: hand;");
        addUserBtn.setMaxWidth(Double.MAX_VALUE);
        addUserBtn.setOnAction(e -> handleAddUser());

        Button removeUserBtn = new Button("Remove Friend (User)");
        removeUserBtn.setStyle("-fx-padding: 8 15; -fx-font-size: 11px; -fx-cursor: hand;");
        removeUserBtn.setMaxWidth(Double.MAX_VALUE);
        removeUserBtn.setOnAction(e -> handleRemoveUser());

        Button addEdgeBtn = new Button("Add Friendship (Edge)");
        addEdgeBtn.setStyle("-fx-padding: 8 15; -fx-font-size: 11px; -fx-cursor: hand;");
        addEdgeBtn.setMaxWidth(Double.MAX_VALUE);
        addEdgeBtn.setOnAction(e -> handleAddEdge());

        Button removeEdgeBtn = new Button("Remove Friendship (Edge)");
        removeEdgeBtn.setStyle("-fx-padding: 8 15; -fx-font-size: 11px; -fx-cursor: hand;");
        removeEdgeBtn.setMaxWidth(Double.MAX_VALUE);
        removeEdgeBtn.setOnAction(e -> handleRemoveEdge());
        
        VBox controlsBox = new VBox(8, controlsTitle, addUserBtn, removeUserBtn, addEdgeBtn, removeEdgeBtn, refreshBtn);
        
        //assemble sidebar
        VBox sidebar = new VBox(15, titleLabel, sep1, statsBox, sep2, userScrollPane, sep3, legendBox, sep4, controlsBox);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(SIDEBAR_WIDTH);
        sidebar.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 0 0 0 1;");
        
        return sidebar;
    }
    
    /**
     * Creates a legend item with a colored circle and label
     */
    private HBox createLegendItem(String fillColor, String strokeColor, String text) {
        javafx.scene.shape.Circle legendCircle = new javafx.scene.shape.Circle(8);
        legendCircle.setStyle("-fx-fill: " + fillColor + "; -fx-stroke: " + strokeColor + "; -fx-stroke-width: 2;");
        
        Label legendLabel = new Label(text);
        legendLabel.setStyle("-fx-font-size: 10px;");
        
        HBox item = new HBox(10, legendCircle, legendLabel);
        item.setAlignment(Pos.CENTER_LEFT);
        
        return item;
    }
    
    /**
     * Handles the event when a node is clicked on the canvas.
     * Updates the sidebar to display the selected user's information.
     * @param node the Node that was clicked
     */
    private void handleNodeClick(Node node) {
        userInfoBox.getChildren().clear();
        
        Label title = new Label("User Information");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        Label id = new Label("User ID   : " + node.getId());
        id.setStyle("-fx-font-size: 11px;");
        
        Label name = new Label("Name      : " + node.getName());
        name.setStyle("-fx-font-size: 11px;");
        
        Label desc = new Label("Bio       : " + node.getDesc());
        desc.setStyle("-fx-font-size: 11px;");
        desc.setWrapText(true);
        desc.setMaxWidth(SIDEBAR_WIDTH - 40);
        
        Label degree = new Label("Degree    : " + graph.getDegree(node));
        degree.setStyle("-fx-font-size: 11px;");
        
        Label friendsTitle = new Label("Friends (" + graph.getNeighborsCount(node) + "):");
        friendsTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");
        
        userInfoBox.getChildren().addAll(title, id, name, desc, degree, friendsTitle);
        
        //list each friend of the selected user
        for (Node friend : graph.getNeighbors(node)) {
            Label friendLabel = new Label("  • " + friend.getName() + "  [ID: " + friend.getId() + "]");
            friendLabel.setStyle("-fx-font-size: 11px;");
            userInfoBox.getChildren().add(friendLabel);
        }
        
        //recommend friends button
        Button recommendButton = new Button("Recommend Friends");
        recommendButton.setMaxWidth(Double.MAX_VALUE);

        recommendButton.setOnAction(e -> {
            List<Node> recommendations =
                    BFSRecommendation.recommendFriends(graph, node);

            showRecommendations(recommendations, node);
        });

        userInfoBox.getChildren().add(recommendButton);
    }
    
    /**
     * Displays default instructions in the user info box
     */
    private void showDefaultUserInfo() {
        userInfoBox.getChildren().clear();
        
        Label title = new Label("User Information");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        Label hint1 = new Label("• Hover over a node to highlight");
        Label hint2 = new Label("  its direct friends.");
        Label hint3 = new Label("• Click on a node to view its");
        Label hint4 = new Label("  full information here.");
        Label hint5 = new Label("• Drag a node to reposition it.");
        
        hint1.setStyle("-fx-font-size: 11px;");
        hint2.setStyle("-fx-font-size: 11px;");
        hint3.setStyle("-fx-font-size: 11px;");
        hint4.setStyle("-fx-font-size: 11px;");
        hint5.setStyle("-fx-font-size: 11px;");
        
        userInfoBox.getChildren().addAll(title, hint1, hint2, hint3, hint4, hint5);
    }
    
    /**
     * Displays BFS friend recommendations in the side panel and highlights the canvas.
     * This method is designed to be called by teammates implementing BFS.
     * 
     * @param recommendations list of recommended users (from BFS)
     * @param sourceUser the user for whom recommendations are generated
     */
    public void showRecommendations(List<Node> recommendations, Node sourceUser) {
        //visually highlight the graph on canvas
        canvas.showRecommendationsOnCanvas(sourceUser, recommendations);
        
        //update sidebar
        userInfoBox.getChildren().clear();
        
        Label title = new Label("Friend Recommendations");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        Label sourceLabel = new Label("For: " + sourceUser.getName() + " [ID: " + sourceUser.getId() + "]");
        sourceLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");
        
        userInfoBox.getChildren().addAll(title, sourceLabel);
        
        if (recommendations == null || recommendations.isEmpty()) {
            userInfoBox.getChildren().add(new Label("No recommendations found."));
        } else {
            Label recTitle = new Label("\nRecommended Friends:");
            recTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");
            userInfoBox.getChildren().add(recTitle);
            
            int rank = 1;
            for (Node rec : recommendations) {
                Label recLabel = new Label("  " + rank + ". " + rec.getName() + "  [ID: " + rec.getId() + "]");
                recLabel.setStyle("-fx-font-size: 11px;");
                userInfoBox.getChildren().add(recLabel);
                rank++;
            }
        }
        
        //add back button
        Button backBtn = new Button("Back to Main View");
        backBtn.setStyle("-fx-padding: 8 15; -fx-font-size: 11px; -fx-cursor: hand;");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setOnAction(e -> {
            canvas.resetHighlights();
            showDefaultUserInfo();
        });
        userInfoBox.getChildren().add(backBtn);
        
        //scroll to the top so the recommendation names are visible
        Platform.runLater(() -> userScrollPane.setVvalue(0));
    }
    
    /**
     * Counts the total number of edges in the graph.
     * Since the graph is undirected, we count each edge only once.
     * @return the number of edges
     */
    private int countEdges() {
        int count = 0;
        for (Node node : graph.getNodes()) {
            count += graph.getNeighbors(node).size();
        }
        //divide by 2 because each edge is counted twice
        return count / 2;
    }
    
    /**
     * Updates the statistics labels with current graph data
     */
    private void updateStatistics() {
        nodeCountLabel.setText("Vertices: " + graph.getSize());
        edgeCountLabel.setText("Edges: " + countEdges());
    }
    
    private ComboBox<Node> createNodeComboBox() {
        ComboBox<Node> box = new ComboBox<>();
        box.getItems().addAll(graph.getNodes());
        box.setConverter(new StringConverter<Node>() {
            @Override
            public String toString(Node n) {
                return n == null ? "" : n.getName() + "  [ID: " + n.getId() + "]";
            }

            @Override
            public Node fromString(String s) {
                return null; //box is not editable, so parsing back is never needed
            }
        });
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    /**
     * Shows a simple informational/error alert dialog.
     */
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Opens a dialog to add a new user (Node) to the graph.
     * Prompts for a name and a short bio, then calls
     * AbstractGraph.addNode(Node) and refreshes the display.
     */
    private void handleAddUser() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Friend");
        dialog.setHeaderText("Enter the new user's details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextArea descField = new TextArea();
        descField.setPromptText("Short bio");
        descField.setPrefRowCount(3);
        descField.setWrapText(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Bio:"), 0, 1);
        grid.add(descField, 1, 1);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == addButtonType) {
            String name = nameField.getText() == null ? "" : nameField.getText().trim();
            String desc = descField.getText() == null ? "" : descField.getText().trim();

            if (name.isEmpty()) {
                showAlert(AlertType.ERROR, "Invalid Input", "Name cannot be empty.");
                return;
            }

            Node newUser = new Node(name, desc.isEmpty() ? "No bio provided." : desc);
            graph.addNode(newUser);

            canvas.refresh();
            updateStatistics();
            showDefaultUserInfo();
        }
    }

    /**
     * Opens a dialog to remove an existing user (Node) from the graph.
     * Removing a user also removes all of their friendships (edges),
     * which AbstractGraph.removeNode(Node) already handles.
     */
    private void handleRemoveUser() {
        if (graph.getSize() == 0) {
            showAlert(AlertType.INFORMATION, "No Users", "There are no users to remove.");
            return;
        }

        ComboBox<Node> userBox = createNodeComboBox();
        userBox.getSelectionModel().selectFirst();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Remove Friend");
        dialog.setHeaderText("Select the user to remove");
        ButtonType removeButtonType = new ButtonType("Remove", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(removeButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10, new Label("User:"), userBox);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == removeButtonType) {
            Node selected = userBox.getValue();
            if (selected == null) {
                showAlert(AlertType.ERROR, "Invalid Selection", "Please select a user.");
                return;
            }

            graph.removeNode(selected);

            canvas.refresh();
            updateStatistics();
            showDefaultUserInfo();
        }
    }

    /**
     * Opens a dialog to add a friendship (edge) between two existing users.
     */
    private void handleAddEdge() {
        if (graph.getSize() < 2) {
            showAlert(AlertType.INFORMATION, "Not Enough Users", "You need at least two users to create a friendship.");
            return;
        }

        ComboBox<Node> userABox = createNodeComboBox();
        ComboBox<Node> userBBox = createNodeComboBox();
        userABox.getSelectionModel().selectFirst();
        userBBox.getSelectionModel().select(Math.min(1, graph.getSize() - 1));

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Friendship");
        dialog.setHeaderText("Select the two users to connect");
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        grid.add(new Label("User A:"), 0, 0);
        grid.add(userABox, 1, 0);
        grid.add(new Label("User B:"), 0, 1);
        grid.add(userBBox, 1, 1);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == addButtonType) {
            Node userA = userABox.getValue();
            Node userB = userBBox.getValue();

            if (userA == null || userB == null) {
                showAlert(AlertType.ERROR, "Invalid Selection", "Please select both users.");
                return;
            }
            if (userA.equals(userB)) {
                showAlert(AlertType.ERROR, "Invalid Selection", "A user cannot be friends with themselves.");
                return;
            }
            if (graph.getNeighbors(userA).contains(userB)) {
                showAlert(AlertType.INFORMATION, "Already Friends",
                        userA.getName() + " and " + userB.getName() + " are already friends.");
                return;
            }

            graph.addNeighbor(userA, userB);

            canvas.refresh();
            updateStatistics();
            showDefaultUserInfo();
        }
    }

    /**
     * Opens a dialog to remove a friendship (edge) between two existing users.
     */
    private void handleRemoveEdge() {
        if (countEdges() == 0) {
            showAlert(AlertType.INFORMATION, "No Friendships", "There are no friendships to remove.");
            return;
        }

        ComboBox<Node> userABox = createNodeComboBox();
        ComboBox<Node> userBBox = createNodeComboBox();
        userABox.getSelectionModel().selectFirst();
        userBBox.getSelectionModel().select(Math.min(1, graph.getSize() - 1));

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Remove Friendship");
        dialog.setHeaderText("Select the two users to disconnect");
        ButtonType removeButtonType = new ButtonType("Remove", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(removeButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        grid.add(new Label("User A:"), 0, 0);
        grid.add(userABox, 1, 0);
        grid.add(new Label("User B:"), 0, 1);
        grid.add(userBBox, 1, 1);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == removeButtonType) {
            Node userA = userABox.getValue();
            Node userB = userBBox.getValue();

            if (userA == null || userB == null) {
                showAlert(AlertType.ERROR, "Invalid Selection", "Please select both users.");
                return;
            }
            if (!graph.getNeighbors(userA).contains(userB)) {
                showAlert(AlertType.INFORMATION, "Not Friends",
                        userA.getName() + " and " + userB.getName() + " are not currently friends.");
                return;
            }

            graph.removeNeighbor(userA, userB);

            canvas.refresh();
            updateStatistics();
            showDefaultUserInfo();
        }
    }
    
    /**
     * Initializes the graph with sample data containing 12 users.
     * This creates a complex social network with various connections
     * to demonstrate the friend recommendation system.
     */
    private void initializeSampleGraph() {
        graph = new AbstractGraph();
        
        //create 12 user nodes (more than 10 as required by assignment)
        Node james   = new Node("James",   "Loves photography and hiking."),
             mary    = new Node("Mary",    "Food enthusiast and home baker."),
             chad    = new Node("Chad",    "Gym rat and fitness coach."),
             delaney = new Node("Delaney", "Bookworm and literature lover."),
             edgar   = new Node("Edgar",   "Music producer and DJ."),
             farsch  = new Node("Farsch",  "Travel blogger and vlogger."),
             greg    = new Node("Greg",    "Software engineer and gamer."),
             holly   = new Node("Holly",   "Artist and freelance painter."),
             ivan    = new Node("Ivan",    "Professional eSports player."),
             julia   = new Node("Julia",   "Yoga instructor and wellness coach."),
             kevin   = new Node("Kevin",   "Chef specialising in Asian cuisine."),
             luna    = new Node("Luna",   "Astronomy enthusiast and stargazer.");
        
        //add all nodes to graph
        graph.addNode(james);
        graph.addNode(mary);
        graph.addNode(chad);
        graph.addNode(delaney);
        graph.addNode(edgar);
        graph.addNode(farsch);
        graph.addNode(greg);
        graph.addNode(holly);
        graph.addNode(ivan);
        graph.addNode(julia);
        graph.addNode(kevin);
        graph.addNode(luna);
        
        //define undirected friendships (edges) between users
        graph.addNeighbor(james, mary);
        graph.addNeighbor(james, greg);
        graph.addNeighbor(james, luna);
        graph.addNeighbor(mary, chad);
        graph.addNeighbor(mary, delaney);
        graph.addNeighbor(chad, greg);
        graph.addNeighbor(chad, delaney);
        graph.addNeighbor(delaney, edgar);
        graph.addNeighbor(edgar, holly);
        graph.addNeighbor(edgar, farsch);
        graph.addNeighbor(farsch, ivan);
        graph.addNeighbor(greg, holly);
        graph.addNeighbor(holly, ivan);
        graph.addNeighbor(ivan, julia);
        graph.addNeighbor(julia, kevin);
        graph.addNeighbor(kevin, luna);
        graph.addNeighbor(luna, julia);
    }
    
    /**
     * Returns the graph instance
     * @return the AbstractGraph being displayed
     */
    public AbstractGraph getGraph() {
        return graph;
    }
    
    /**
     * Returns the canvas instance for external access
     * @return the GraphCanvas used for drawing
     */
    public GraphCanvas getCanvas() {
        return canvas;
    }
    
    /**
     * Main method to launch the application
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}