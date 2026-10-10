/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package friendrecommendationsystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class BFSRecommendation {

    /**
     * Finds friends-of-friends using Breadth-First Search.
     *
     * @param graph the social-network graph
     * @param sourceUser the user who needs recommendations
     * @return users exactly two edges away from sourceUser
     */
    public static List<Node> recommendFriends(
            AbstractGraph graph, Node sourceUser) {

        List<Node> recommendations = new ArrayList<>();

        if (graph == null
                || sourceUser == null
                || !graph.nodeExists(sourceUser)) {
            return recommendations;
        }

        Queue<Node> queue = new LinkedList<>();
        Set<Node> visited = new HashSet<>();
        Map<Node, Integer> distance = new HashMap<>();

        queue.offer(sourceUser);
        visited.add(sourceUser);
        distance.put(sourceUser, 0);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            int currentDistance = distance.get(current);

            // No need to search beyond friends-of-friends.
            if (currentDistance >= 2) {
                continue;
            }

            for (Node neighbor : graph.getNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                    distance.put(neighbor, currentDistance + 1);
                }
            }
        }

        // Only users exactly two edges away are recommendations.
        for (Node user : graph.getNodes()) {
            if (distance.getOrDefault(user, -1) == 2) {
                recommendations.add(user);
            }
        }

        return recommendations;
    }
}
