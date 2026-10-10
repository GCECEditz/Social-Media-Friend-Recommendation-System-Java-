/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package friendrecommendationsystem;

/**
 *
 * @author User
 */
public class Edge {
    private Node n0 = null, n1 = null;
    
    public Edge(Node n0, Node n1){
        this.n0 = n0;
        this.n1 = n1;
    }
    
    public Node getN0(){
        return n0;
    }
    
     public Node getN1(){
        return n1;
    }
     
     public boolean equals(Object o){
        if (o instanceof Edge){
            Edge n = (Edge)o;
            return (n.n0 == n0) && (n.n1 == n1);
        }
        return false;
    }
}

