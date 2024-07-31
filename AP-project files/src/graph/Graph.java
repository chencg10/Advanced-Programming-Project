package graph;

import configs.Node;
import java.util.ArrayList;
import java.util.HashMap;
import graph.TopicManagerSingleton.TopicManager;

/**
 * The {@code Graph} class represents a graph structure where each node
 * is an instance of the {@link Node} class. It extends {@link java.util.ArrayList}
 * to manage a collection of nodes. This class is used to represent topics and agents
 * in a publish-subscribe system.
 *
 * <p>The graph structure allows for efficient management and manipulation
 * of nodes and edges representing the relationships between topics and agents.</p>
 *
 * @see Node
 */
public class Graph extends ArrayList<Node> {

    /**
     * Constructs an empty Graph.
     */
    public Graph() {
        super();
    }

    /**
     * Checks if the graph contains any cycles.
     *
     * @return true if the graph contains a cycle, false otherwise.
     */
    public boolean hasCycles() {
        // for each node in the graph, check if it has a cycle in its subcomponents
        for (Node node : this) {
            if (node.hasCycles()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a graph structure from the topics and agents in the TopicManager.
     * Each topic and agent becomes a node, with edges representing the publish-subscribe relationships.
     */
    public void createFromTopics() {
    TopicManager tm = TopicManagerSingleton.get();

    // get topics
    Topic[] topics = tm.getTopics().values().toArray(new Topic[0]);

    // create hashmap to remember the nodes
    HashMap<String, Node> nodes = new HashMap<>();

    // for each topic, create a node
    for (Topic curTopic : topics) {
        // create a node
        Node curNode = nodes.get("T" + curTopic.getName());

        if (curNode == null) {
            // create a new node
            curNode = new Node("T" + curTopic.getName());
            // add it to the hashmap
            nodes.put(curNode.getName(), curNode);
            // add it to the graph
            this.add(curNode);
        }

        // get the subscribers
        for (Agent agent : curTopic.getSubscribers()) {
            // create a unique identifier for the agent
            String agentKey = "A" + agent.getName() + "_" + System.identityHashCode(agent);
            // get the node
            Node subNode = nodes.get(agentKey);
            if (subNode == null) {
                // create a new node
                subNode = new Node(agentKey);
                // add it to the hashmap
                nodes.put(agentKey, subNode);
                // add it to the graph
                this.add(subNode);
            }
            // add an edge from the current node to the subscriber node
            curNode.addEdge(subNode);
        }

        // get the publishers
        for (Agent agent : curTopic.getPublishers()) {
            // create a unique identifier for the agent
            String agentKey = "A" + agent.getName() + "_" + System.identityHashCode(agent);
            // get the node
            Node pubNode = nodes.get(agentKey);
            if (pubNode == null) {
                // create a new node
                pubNode = new Node(agentKey);
                // add it to the hashmap
                nodes.put(agentKey, pubNode);
                // add it to the graph
                this.add(pubNode);
            }
            // add an edge from the publisher node to the current node
            pubNode.addEdge(curNode);
        }
    }
}
//    public void createFromTopics() {
//        TopicManager tm = TopicManagerSingleton.get();
//
//        // get topics
//        Topic[] topics = tm.getTopics().values().toArray(new Topic[0]);
//
//        // create hashmap to remember the nodes
//        HashMap<String, Node> nodes = new HashMap<>();
//
//        // for each topic, create a node
//        for (Topic curTopic : topics) {
//            // create a node
//            Node curNode = nodes.get("T" + curTopic.getName());
//
//            if (curNode == null) {
//                // create a new node
//                curNode = new Node("T" + curTopic.getName());
//                // add it to the hashmap
//                nodes.put(curNode.getName(), curNode);
//                // add it to the graph
//                this.add(curNode);
//            }
//
//            // get the subscribers
//            for (Agent agent : curTopic.getSubscribers()) {
//                // get the node
//                Node subNode = nodes.get("A" + agent.getName());
//                if (subNode == null) {
//                    // create a new node
//                    subNode = new Node("A" + agent.getName());
//                    // add it to the hashmap
//                    nodes.put(subNode.getName(), subNode);
//                    // add it to the graph
//                    this.add(subNode);
//                }
//                // add an edge from the current node to the subscriber node
//                curNode.addEdge(subNode);
//            }
//
//            // get the publishers
//            for (Agent agent : curTopic.getPublishers()) {
//                // get the node
//                Node pubNode = nodes.get("A" + agent.getName());
//                if (pubNode == null) {
//                    // create a new node
//                    pubNode = new Node("A" + agent.getName());
//                    // add it to the hashmap
//                    nodes.put(pubNode.getName(), pubNode);
//                    // add it to the graph
//                    this.add(pubNode);
//                }
//                // add an edge from the publisher node to the current node
//                pubNode.addEdge(curNode);
//            }
//        }
//    }

    /**
     * Retrieves a node from the graph based on its topic name.
     *
     * @param topic The name of the topic to search for.
     * @return The Node object corresponding to the given topic name.
     */
    private Node getNode(String topic) {
        return this.get(this.indexOf(new Node(topic)));
    }
}