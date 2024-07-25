package configs;

import graph.Message;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

/**
 * Represents a node in a graph structure.
 * Each node has a name, a list of edges (connections to other nodes),
 * and can hold a message.
 */
public class Node {
    /** The name of the node. */
    private final String name;
    /** List of nodes this node is connected to. */
    private List<Node> edges = new ArrayList<>();
    /** Message held by this node. */
    private Message message;

    /**
     * Constructs a new Node with the given name.
     *
     * @param name The name of the node.
     */
    public Node(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the node.
     *
     * @return The name of the node.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the list of nodes this node is connected to.
     *
     * @return A list of Node objects representing the connections.
     */
    public List<Node> getEdges() {
        return this.edges;
    }

    /**
     * Gets the message held by this node.
     *
     * @return The Message object held by this node.
     */
    public Message getMessage() {
        return this.message;
    }

    /**
     * Sets the message for this node.
     *
     * @param message The Message object to be set.
     */
    public void setMessage(Message message) {
        this.message = new Message(message.data);
    }

    /**
     * Sets the list of edges for this node.
     *
     * @param edges A list of Node objects to set as edges.
     */
    public void setEdges(List<Node> edges) {
        this.edges = new ArrayList<>(edges);
    }

    /**
     * Adds a new edge (connection) to this node.
     *
     * @param node The Node object to connect to.
     */
    public void addEdge(Node node) {
        this.edges.add(node);
    }

    /**
     * Checks if there are any cycles in the graph starting from this node.
     *
     * @return true if a cycle is detected, false otherwise.
     */
    public boolean hasCycles() {
        Set<Node> visited = new HashSet<>();
        Set<Node> recStack = new HashSet<>();

        return hasCyclesUtil(this, visited, recStack);
    }

    /**
     * Utility method for cycle detection.
     *
     * @param node The current node being processed.
     * @param visited Set of nodes that have been visited.
     * @param recStack Set of nodes in the current recursion stack.
     * @return true if a cycle is detected, false otherwise.
     */
    private static boolean hasCyclesUtil(Node node, Set<Node> visited, Set<Node> recStack) {
        if (recStack.contains(node)) {
            // Cycle detected
            return true;
        }

        if (visited.contains(node)) {
            // A node has been completely processed
            return false;
        }

        // Mark the current node as visited and part of recursion stack
        visited.add(node);
        recStack.add(node);

        // for every neighbor, check if it has cycles
        for (Node neighbor : node.getEdges()) {
            if (hasCyclesUtil(neighbor, visited, recStack)) {
                return true;
            }
        }
        // remove the node from the recursion stack
        recStack.remove(node);
        return false;
    }
}