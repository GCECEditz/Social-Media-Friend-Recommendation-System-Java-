/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package friendrecommendationsystem;

/**
 *
 * @author User
 */
public class Node {
    
    private int id;
    private String name, desc;
    private static int id_count = 0;
    
    public Node(String name, String desc){
        this.id = id_count++;
        this.name = name;
        this.desc = desc;        
    }
    
    public int getId(){
        return id;
    }
    
    public String getName(){
        return name;
    }
    
    public String getDesc(){
        return desc;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void setDesc(String desc){
        this.desc = desc;
    }
    
    public String toString(){
        return String.format("Id: %d | Name: %s\n%s", id, name, desc);
    }
    
    public boolean equals(Object o){
        if (o instanceof Node){
            Node n = (Node)o;
            return n.id == id;
        }
        return false;
    }
}
