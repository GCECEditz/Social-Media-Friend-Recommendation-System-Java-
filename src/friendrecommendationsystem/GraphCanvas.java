/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package friendrecommendationsystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * GraphCanvas.java
 * 
 * A custom JavaFX Pane that renders the social network graph visually.
 * Users are represented as circular nodes with their names displayed,
 * and friendships are represented as lines (edges) connecting the nodes.
 * 
 * @author User
 */
public class GraphCanvas extends Pane {
    
    //the graph to be displayed
    private AbstractGraph graph;
    
    //stores the visual representations of nodes
    private Map<Node, Circle> nodeCircles;
    
    //stores the text labels for nodes
    private Map<Node, Text> nodeLabels;
    
    //stores the positions of nodes as [x, y] arrays
    private Map<Node, double[]> nodePositions;
    
    //stores all edge lines for highlighting purposes
    private List<Line> edgeLines;
    
    //callback to notify the main UI when a node is clicked
    private Consumer<Node> onNodeClicked;
    
    //radius of each node circle
    private double nodeRadius = 35;
    
    //padding from the edges of the canvas
    private double padding = 60;
    
    //force-directed layout parameters
    private static final double REPULSION = 8000;
    private static final double ATTRACTION = 0.008;
    private static final double DAMPING = 0.85;
    private static final int MAX_ITERATIONS = 500;
    
    //color constants for consistent styling
    private static final Color NODE_FILL_COLOR = Color.DODGERBLUE;
    private static final Color NODE_STROKE_COLOR = Color.NAVY;
    private static final Color EDGE_COLOR = Color.LIGHTGRAY;
    private static final Color LABEL_COLOR = Color.WHITE;
    private static final Color ID_LABEL_COLOR = Color.LIGHTCYAN;
    private static final Color BACKGROUND_COLOR = Color.rgb(245, 245, 245);
    
    //tracks whether a node is being dragged to prevent click events
    private boolean isDragging = false;
    
    /**
     * Constructor for GraphCanvas
     * @param graph the AbstractGraph to be displayed
     */
    public GraphCanvas(AbstractGraph graph) {
        this.graph = graph;
        this.nodeCircles = new HashMap<>();
        this.nodeLabels = new HashMap<>();
        this.nodePositions = new HashMap<>();
        this.edgeLines = new ArrayList<>();
        
        setStyle("-fx-background-color: #" + 
                 String.format("%02x%02x%02x", 
                 (int)(BACKGROUND_COLOR.getRed() * 255),
                 (int)(BACKGROUND_COLOR.getGreen() * 255),
                 (int)(BACKGROUND_COLOR.getBlue() * 255)) + ";");
        
        //add listeners to redraw the graph when the canvas is resized
        widthProperty().addListener((obs, oldVal, newVal) -> {
            drawGraph();
        });
        heightProperty().addListener((obs, oldVal, newVal) -> {
            drawGraph();
        });
        
        drawGraph();
    }
    
    /**
     * Main method to draw the graph
     * Clears existing elements and redraws the entire graph
     */
    public void drawGraph() {
        //clear previous drawings
        getChildren().clear();
        nodeCircles.clear();
        nodeLabels.clear();
        nodePositions.clear();
        edgeLines.clear();
        
        //check if graph is empty
        if (graph == null || graph.getSize() == 0 || getWidth() == 0 || getHeight() == 0) {
            return;
        }
        
        //calculate node positions using force-directed layout
        calculatePositions();
        
        //draw edges first (so they appear behind nodes)
        drawEdges();
        
        //draw nodes on top of edges
        drawNodes();
    }
    
    /**
     * Calculates the positions of all nodes using a force-directed layout algorithm.
     */
    private void calculatePositions() {
        ArrayList<Node> nodes = graph.getNodes();
        int n = nodes.size();
        
        //use current canvas dimensions (fallback to 800x600 if not laid out yet)
        double canvasWidth = getWidth() > 0 ? getWidth() : 800;
        double canvasHeight = getHeight() > 0 ? getHeight() : 600;
        
        //initialize positions in a circle as starting point
        double centerX = canvasWidth / 2;
        double centerY = canvasHeight / 2;
        double radius = Math.min(centerX, centerY) - padding - nodeRadius;
        
        for (int i = 0; i < n; i++) {
            Node node = nodes.get(i);
            double angle = 2 * Math.PI * i / n;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            nodePositions.put(node, new double[]{x, y});
        }
        
        //forces for each node: [fx, fy]
        double[][] forces = new double[n][2];
        
        //iterate to find equilibrium
        for (int iter = 0; iter < MAX_ITERATIONS; iter++) {
            //reset forces
            for (int i = 0; i < n; i++) {
                forces[i][0] = 0;
                forces[i][1] = 0;
            }
            
            //calculate repulsive forces between all pairs of nodes
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    Node ni = nodes.get(i);
                    Node nj = nodes.get(j);
                    double[] pi = nodePositions.get(ni);
                    double[] pj = nodePositions.get(nj);
                    
                    double dx = pi[0] - pj[0];
                    double dy = pi[1] - pj[1];
                    double dist = Math.max(Math.sqrt(dx * dx + dy * dy), 1);
                    
                    double force = REPULSION / (dist * dist);
                    double fx = (dx / dist) * force;
                    double fy = (dy / dist) * force;
                    
                    forces[i][0] += fx;
                    forces[i][1] += fy;
                    forces[j][0] -= fx;
                    forces[j][1] -= fy;
                }
            }
            
            //calculate attractive forces for connected nodes
            for (int i = 0; i < n; i++) {
                Node ni = nodes.get(i);
                ArrayList<Node> neighbors = graph.getNeighbors(ni);
                double[] pi = nodePositions.get(ni);
                
                for (Node nj : neighbors) {
                    double[] pj = nodePositions.get(nj);
                    
                    double dx = pj[0] - pi[0];
                    double dy = pj[1] - pi[1];
                    double dist = Math.max(Math.sqrt(dx * dx + dy * dy), 1);
                    
                    double force = ATTRACTION * dist;
                    double fx = (dx / dist) * force;
                    double fy = (dy / dist) * force;
                    
                    forces[i][0] += fx;
                    forces[i][1] += fy;
                }
            }
            
            //apply forces with damping
            for (int i = 0; i < n; i++) {
                Node node = nodes.get(i);
                double[] pos = nodePositions.get(node);
                
                pos[0] += forces[i][0] * DAMPING;
                pos[1] += forces[i][1] * DAMPING;
                
                //keep nodes within canvas bounds
                pos[0] = Math.max(padding + nodeRadius, Math.min(canvasWidth - padding - nodeRadius, pos[0]));
                pos[1] = Math.max(padding + nodeRadius, Math.min(canvasHeight - padding - nodeRadius, pos[1]));
            }
        }
    }
    
    /**
     * Draws edges (lines) between connected nodes.
     * Stores references to connected nodes inside the Line object for easy dragging updates.
     */
    private void drawEdges() {
        ArrayList<Node> nodes = graph.getNodes();
        
        for (Node node : nodes) {
            ArrayList<Node> neighbors = graph.getNeighbors(node);
            double[] pos1 = nodePositions.get(node);
            
            for (Node neighbor : neighbors) {
                //only draw each edge once (check by comparing IDs)
                if (node.getId() < neighbor.getId()) {
                    double[] pos2 = nodePositions.get(neighbor);
                    
                    Line edge = new Line(pos1[0], pos1[1], pos2[0], pos2[1]);
                    edge.setStroke(EDGE_COLOR);
                    edge.setStrokeWidth(2);
                    
                    //store nodes in line properties so we know which lines to update when dragging
                    edge.getProperties().put("n0", node);
                    edge.getProperties().put("n1", neighbor);
                    
                    edgeLines.add(edge);
                    getChildren().add(edge);
                }
            }
        }
    }
    
    /**
     * Draws nodes (circles) with their name labels and ID labels.
     * Adds mouse event handlers for hovering, clicking, and dragging.
     */
    private void drawNodes() {
        ArrayList<Node> nodes = graph.getNodes();
        
        for (Node node : nodes) {
            double[] pos = nodePositions.get(node);
            
            //create circle for node
            Circle circle = new Circle(pos[0], pos[1], nodeRadius);
            circle.setFill(NODE_FILL_COLOR);
            circle.setStroke(NODE_STROKE_COLOR);
            circle.setStrokeWidth(2);
            circle.setCursor(Cursor.HAND);
            
            //create label with node name (positioned slightly above center)
            Text label = new Text(pos[0], pos[1] - 5, node.getName());
            label.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            label.setFill(LABEL_COLOR);
            label.setTextAlignment(TextAlignment.CENTER);
            
            //create ID label below the name
            Text idLabel = new Text(pos[0], pos[1] + 12, "ID: " + node.getId());
            idLabel.setFont(Font.font("Arial", 9));
            idLabel.setFill(ID_LABEL_COLOR);
            idLabel.setTextAlignment(TextAlignment.CENTER);
            
            //center text based on its width
            label.setX(pos[0] - label.getLayoutBounds().getWidth() / 2);
            idLabel.setX(pos[0] - idLabel.getLayoutBounds().getWidth() / 2);
            
            //mouse hover highlights the node and its direct friends
            circle.setOnMouseEntered(e -> highlightNode(node));
            circle.setOnMouseExited(e -> resetHighlights());
            
            //mouse press sets dragging to false initially
            circle.setOnMousePressed(e -> {
                isDragging = false;
            });
            
            //mouse drag moves the node and updates connected edges
            circle.setOnMouseDragged(e -> {
                isDragging = true;
                handleNodeDrag(node, circle, label, idLabel, e);
                e.consume();
            });
            
            //mouse release triggers click if it wasn't a drag
            circle.setOnMouseReleased(e -> {
                if (!isDragging) {
                    if (onNodeClicked != null) {
                        onNodeClicked.accept(node);
                    }
                }
                isDragging = false;
                e.consume();
            });
            
            //store references
            nodeCircles.put(node, circle);
            nodeLabels.put(node, label);
            
            //add visual elements to canvas
            getChildren().addAll(circle, label, idLabel);
        }
    }
    
    /**
     * Handles the dragging of a node, updating its position, labels, and connected edges.
     */
    private void handleNodeDrag(Node node, Circle circle, Text label, Text idLabel, MouseEvent e) {
        double canvasWidth = getWidth();
        double canvasHeight = getHeight();
        
        //get new mouse coordinates, constrained to canvas bounds
        double newX = Math.max(nodeRadius, Math.min(canvasWidth - nodeRadius, e.getX()));
        double newY = Math.max(nodeRadius, Math.min(canvasHeight - nodeRadius, e.getY()));
        
        //update circle position
        circle.setCenterX(newX);
        circle.setCenterY(newY);
        
        //update labels position
        label.setX(newX - label.getLayoutBounds().getWidth() / 2);
        label.setY(newY - 5);
        idLabel.setX(newX - idLabel.getLayoutBounds().getWidth() / 2);
        idLabel.setY(newY + 12);
        
        //update position map
        nodePositions.get(node)[0] = newX;
        nodePositions.get(node)[1] = newY;
        
        //update connected edges
        for (Line line : edgeLines) {
            Node n0 = (Node) line.getProperties().get("n0");
            Node n1 = (Node) line.getProperties().get("n1");
            
            if (node.equals(n0) || node.equals(n1)) {
                double[] p0 = nodePositions.get(n0);
                double[] p1 = nodePositions.get(n1);
                line.setStartX(p0[0]);
                line.setStartY(p0[1]);
                line.setEndX(p1[0]);
                line.setEndY(p1[1]);
            }
        }
    }
    
    /**
     * Highlights a Node and its direct neighbours.
     * @param node the node to highlight
     */
    private void highlightNode(Node node) {
        ArrayList<Node> neighbors = graph.getNeighbors(node);
        double[] pos = nodePositions.get(node);
        
        //color nodes based on relationship to selected node
        for (Node n : nodeCircles.keySet()) {
            Circle c = nodeCircles.get(n);
            if (n.equals(node)) {
                c.setFill(Color.web("#e74c3c"));   //selected node = red
            } else if (neighbors.contains(n)) {
                c.setFill(Color.web("#2ecc71"));   //direct friend = green
            } else {
                c.setFill(Color.web("#95a5a6"));   //other users = gray
            }
        }
        
        //highlight edges connected to the selected node
        for (Line line : edgeLines) {
            boolean connected = (line.getStartX() == pos[0] && line.getStartY() == pos[1])
                             || (line.getEndX()   == pos[0] && line.getEndY()   == pos[1]);
            if (connected) {
                line.setStroke(Color.web("#e74c3c"));
                line.setStrokeWidth(3);
            } else {
                line.setStroke(Color.web("#eeeeee"));
                line.setStrokeWidth(1.5);
            }
        }
    }
    
    /**
     * Visually highlights BFS recommendations on the canvas.
     * Source user = Red, Direct friends = Green, Recommendations = Yellow.
     * 
     * @param sourceUser the starting user for BFS
     * @param recommendations the list of recommended friends returned by BFS
     */
    public void showRecommendationsOnCanvas(Node sourceUser, List<Node> recommendations) {
        ArrayList<Node> neighbors = graph.getNeighbors(sourceUser);
        
        for (Node n : nodeCircles.keySet()) {
            Circle c = nodeCircles.get(n);
            if (n.equals(sourceUser)) {
                c.setFill(Color.web("#e74c3c"));   //source user = red
            } else if (recommendations != null && recommendations.contains(n)) {
                c.setFill(Color.web("#f1c40f"));   //recommended friend = yellow
            } else if (neighbors.contains(n)) {
                c.setFill(Color.web("#2ecc71"));   //direct friend = green
            } else {
                c.setFill(Color.web("#95a5a6"));   //other users = gray
            }
        }
    }
    
    /**
     * Resets all nodes and edges to their default appearance
     */
    public void resetHighlights() {
        for (Node n : nodeCircles.keySet()) {
            nodeCircles.get(n).setFill(NODE_FILL_COLOR);
        }
        for (Line line : edgeLines) {
            line.setStroke(EDGE_COLOR);
            line.setStrokeWidth(2);
        }
    }
    
    /**
     * Sets the callback listener for node click events
     * @param consumer the function to execute when a node is clicked
     */
    public void setOnNodeClicked(Consumer<Node> consumer) {
        this.onNodeClicked = consumer;
    }
    
    /**
     * Refreshes the graph display by redrawing all elements
     */
    public void refresh() {
        drawGraph();
    }
    
    /**
     * Updates the graph being displayed
     * @param graph the new AbstractGraph to display
     */
    public void setGraph(AbstractGraph graph) {
        this.graph = graph;
        refresh();
    }
}