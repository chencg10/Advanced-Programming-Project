package graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import graph.TopicManagerSingleton.TopicManager;
import configs.Node;
import configs.Config;

public class EX3_TESTER {
     public static boolean hasCycles(List<Node> graph) {
        for (Node node : graph) {
            if (node.hasCycles()) {
                return true;
            }
        }
        return false;
    }

    public static void testCycles() {
        // Common case
        Node a = new Node("A");
        Node b = new Node("B");
        Node c = new Node("C");
        Node d = new Node("D");
        a.addEdge(b);
        b.addEdge(c);
        c.addEdge(d);
        List<Node> graph = new ArrayList<>(Arrays.asList(a, b, c, d));
        if (hasCycles(graph)) {
            System.out.println("Error: hasCycles() incorrectly detected cycles when there are none (-20)");
        }

        // Edge case: Adding a cycle
        d.addEdge(a);
        if (!hasCycles(graph)) {
            System.out.println("Error: hasCycles() failed to detect cycle (-10)");
        }

        // Edge case: Empty graph
        List<Node> emptyGraph = new ArrayList<>();
        if (hasCycles(emptyGraph)) {
            System.out.println("Error: hasCycles() detected cycles in an empty graph (-10)");
        }

        // Edge case: Single node with self-loop
        Node singleNode = new Node("E");
        singleNode.addEdge(singleNode);
        List<Node> singleNodeGraph = new ArrayList<>(Arrays.asList(singleNode));
        if (!hasCycles(singleNodeGraph)) {
            System.out.println("Error: hasCycles() failed to detect self-loop cycle (-10)");
        }

        // Edge case: Disconnected graph
        Node f = new Node("F");
        Node g = new Node("G");
        f.addEdge(g);
        List<Node> disconnectedGraph = new ArrayList<>(Arrays.asList(a, b, c, d, f, g));
        if (!hasCycles(disconnectedGraph)) {
            System.out.println("Error: hasCycles() incorrectly detected cycles in a disconnected graph (-10)");
        }
    }

    public static class GetAgent implements Agent {
        public Message msg;

        public GetAgent(String topic) {
            TopicManagerSingleton.get().getTopic(topic).subscribe(this);
        }

        @Override
        public String getName() {
            return "Get Agent";
        }

        @Override
        public void reset() {}

        @Override
        public void callback(String topic, Message msg) {
            this.msg = msg;
        }

        @Override
        public void close() {}
    }

    public static void testBinGraph() {
        TopicManager tm = TopicManagerSingleton.get();
        tm.clear();
        Config c = new MathExampleConfig();
        c.create();

        // Test basic operation
        GetAgent ga = new GetAgent("R3");
        Random r = new Random();
        int x = 1 + r.nextInt(100);
        int y = 1 + r.nextInt(100);
        tm.getTopic("A").publish(new Message(x));
        tm.getTopic("B").publish(new Message(y));
        double expected = (x + y) * (x - y);

        // Allow some time for message processing
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (Math.abs(expected - ga.msg.asDouble) > 0.05) {
            System.out.println("Error: BinOpAgent did not produce the desired result (-20)");
        }

        // Test edge cases
        // Negative numbers
        x = -r.nextInt(100);
        y = -r.nextInt(100);
        tm.getTopic("A").publish(new Message(x));
        tm.getTopic("B").publish(new Message(y));
        expected = (x + y) * (x - y);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (Math.abs(expected - ga.msg.asDouble) > 0.05) {
            System.out.println("Error: BinOpAgent did not handle negative numbers correctly (-20)");
        }

        // Zero values
        x = 0;
        y = 0;
        tm.getTopic("A").publish(new Message(x));
        tm.getTopic("B").publish(new Message(y));
        expected = (x + y) * (x - y);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (Math.abs(expected - ga.msg.asDouble) > 0.05) {
            System.out.println("Error: BinOpAgent did not handle zero values correctly (-20)");
        }
    }

    public static void testTopicsGraph() {
        TopicManager tm = TopicManagerSingleton.get();
        tm.clear();
        Config c = new MathExampleConfig();
        c.create();
        Graph g = new Graph();
        g.createFromTopics();

        // Test basic graph properties
        if (g.size() != 8) {
            System.out.println("Error: The graph created from topics has the wrong size (-10)");
        }

        List<String> expectedNodeNames = Arrays.asList("TA", "TB", "Aplus", "Aminus", "TR1", "TR2", "Amul", "TR3");
        boolean hasCorrectNames = true;
        for (Node n : g) {
            hasCorrectNames &= expectedNodeNames.contains(n.getName());
        }
        if (!hasCorrectNames) {
            System.out.println("Error: The graph created from topics has incorrect node names (-10)");
        }

        if (g.hasCycles()) {
            System.out.println("Error: hasCycles() incorrectly detected cycles in a graph with no cycles (-10)");
        }

        // Test with cycle introduction
        GetAgent ga = new GetAgent("R3");
        tm.getTopic("A").addPublisher(ga); // Introduce a cycle
        g.createFromTopics();

        if (!g.hasCycles()) {
            System.out.println("Error: hasCycles() failed to detect cycle in a graph with a cycle (-10)");
        }
    }

    public static void main(String[] args) {
        System.out.println("Running tests...");
        testCycles();
        testBinGraph();
        testTopicsGraph();
        System.out.println("All tests completed.");
    }
}

