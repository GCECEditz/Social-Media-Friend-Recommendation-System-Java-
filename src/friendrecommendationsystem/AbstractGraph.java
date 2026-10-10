/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package friendrecommendationsystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author User
 */
public class AbstractGraph {
    
    private LinkedHashMap<Node, ArrayList<Node>> map;
    private LinkedHashMap<Integer, Node> nodes;
    private boolean doLog = false;
    
    public AbstractGraph(){
        map = new LinkedHashMap<>();
        nodes = new LinkedHashMap<>();
    }
    
    public boolean getDoLog(){
        return doLog;
    }
    
    public void setDoLog(boolean d){
        doLog = d;
    }
    
    //adds a Node object into the graph
    public void addNode(Node n){
        if (!nodes.containsKey(n.getId())){
            nodes.put(n.getId(), n);
            map.put(n, new ArrayList<>());
            return;
        }
        if (doLog) System.out.format("Node %d already exists.\n", n.getId());
    }
    
    //add neighbour using integer id as key
    public void addNeighbor(int k, int n){
        if (nodes.containsKey(k) && nodes.containsKey(n)){
            addNeighbor(getNodeByIndex(k), getNodeByIndex(n));
            return;
        }
        if (doLog) System.out.format("Either Nodes %d, %d do not exist.\n", k, n);
    }
    
    //add neighbour using Node as key itself
    public void addNeighbor(Node k, Node n){
        if (map.containsKey(k) && map.containsKey(n)){
            ArrayList<Node> k_n = map.get(k), n_n = map.get(n);
            if (!k_n.contains(n) && !n_n.contains(k)){
                k_n.add(n);
                n_n.add(k);
            }
            else {
                if (doLog) System.out.println("Neighbors already exist.");
            }
            return;
        }
        if (doLog) System.out.format("Either Nodes %d, %d do not exist.\n", k.getId(), n.getId());
    }
    
    //remove Node from graph using integer id
    public void removeNode(int k){
        if (nodes.containsKey(k)){
            removeNode(getNodeByIndex(k));
            return;
        }
        if (doLog) System.out.format("Key Node %d does not exist.\n", k);
    }
    
    //remove Node from graph, as well as itself as other nodes' neighbours
    public void removeNode(Node k){
        if (map.containsKey(k)){
            for (Node neighbour: map.get(k)){
                map.get(neighbour).remove(k);
            }
            map.remove(k);
            nodes.remove(k.getId());
            return;
        }
        if (doLog) System.out.format("Key Node %d does not exist.\n", k.getId());
    }
    
    //remove Node neighbour using integer id
    public void removeNeighbor(int k, int n){
        if (nodes.containsKey(k) && nodes.containsKey(n)){
            removeNeighbor(getNodeByIndex(k), getNodeByIndex(n));
            return;
        }
        if (doLog) System.out.format("Either Nodes %d, %d do not exist.\n", k, n);
    }
    
    //remove Node neighbour
    public void removeNeighbor(Node k, Node n){
        if (map.containsKey(k) && map.containsKey(n)){
            ArrayList<Node> ls1 = map.get(k);
            ArrayList<Node> ls2 = map.get(n);

            if (ls1.contains(n)){
                ls1.remove(n);
            }
            else {
                if (doLog) System.out.format("Node %d does not have neighbour Node %d.\n", k.getId(), n.getId());
            }
            
            if (ls2.contains(k)){
                ls2.remove(k);
            }
            else {
                if (doLog) System.out.format("Node %d does not have neighbour Node %d.\n", n.getId(), k.getId());
            }
            return;
        }
        if (doLog) System.out.format("Either Nodes %d, %d do not exist.\n", k.getId(), n.getId());
    }
    
    //checks if a Node exists based on its index
    public boolean nodeExists(int index){
        return nodes.get(index) != null;
    }
    
    //checks if a Node exists based on its object
    public boolean nodeExists(Node n){
        return map.get(n) != null;
    }
    
    //get Node using integer id
    public Node getNodeByIndex(int index){
        if (nodes.containsKey(index)){
            return nodes.get(index);
        }
        if (doLog) System.out.println("Node does not exist.");
        return null;
    }
    
    //returns the size of the graph
    public int getSize(){
        return map.size();
    }
    
    //gets all Node objects in an ArrayList
    public ArrayList<Node> getNodes(){
        return new ArrayList<>(map.keySet());
    }
    
    //gets all Node neighbours of a Node given its id
    public ArrayList<Node> getNeighbors(int index){
        return getNeighbors(nodes.get(index));
    }
    
    //gets all Node neighbours of a Node
    public ArrayList<Node> getNeighbors(Node n){
        if (map.containsKey(n)){
            return new ArrayList<>(map.get(n));
        }
        else {
            if (doLog) System.out.format("No such Node %d", n.getId());
            return new ArrayList<>();
        }
    }
    
    
    //gets all Node neighbour count of a Node given its id
    public int getNeighborsCount(int index){
        return getNeighborsCount(nodes.get(index));
    }
    
    //gets all Node neighbour count of a Node
    public int getNeighborsCount(Node n){
        if (map.containsKey(n)){
            return map.get(n).size();
        }
        else {
            if (doLog) System.out.format("No such Node %d", n.getId());
            return -1;
        }
    }
    
    //gets all degrees of a Node given its id
    public int getDegree(int index){
        return getDegree(nodes.get(index));
    }
    
    //gets all degrees of a Node
    public int getDegree(Node n){
        if (map.containsKey(n)){
            return map.get(n).size();
        }
        else {
            if (doLog) System.out.format("No such Node %d", n.getId());
            return -1;
        }
    }
    
    //clears the graph
    public void clear(){
        map.clear();
        nodes.clear();
    }
    
    //prints the graph
    public void printGraph(){
        if (map.isEmpty()){
            System.out.println("Graph is empty.");
            return;
        }
        for (Node n: map.keySet()){
            System.out.format("N(%d): [", n.getId());
            
            ArrayList<Node> neighbors = getNeighbors(n);
            for (int i = 0; i < neighbors.size(); i++){
                System.out.print(neighbors.get(i).getId());
                if (i < neighbors.size() - 1) System.out.print(", ");
            }
            System.out.println("]");
        }
    }
    
    public Map<Node, ArrayList<Node>> getAdjacencyList(){
        return Collections.unmodifiableMap(map);
    }
    
    public Map<Integer, Node> getNodesList(){
        return Collections.unmodifiableMap(nodes);
    }
}

