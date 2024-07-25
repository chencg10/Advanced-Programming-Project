package views;

import configs.Node;
import graph.GenericConfig;
import graph.Graph;
import servlet.HtmlLoader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * A utility class for generating an HTML representation of a graph.
 * <p>
 * This class provides a static method to create an HTML file that visualizes a {@link Graph}. It reads a template HTML file,
 * inserts nodes and edges information into it, and saves the result as a new HTML file.
 * </p>
 */
public class HtmlGraphWriter {

    /**
     * Generates an HTML file representing the given graph.
     * <p>
     * This method reads the content of the template HTML file (`temp_graph.html`), which should contain placeholders for nodes
     * and edges. It then populates these placeholders with the nodes and edges of the provided {@link Graph} object and saves
     * the resulting HTML content to a new file (`graph.html`).
     * </p>
     *
     * @param g The {@link Graph} object to be visualized in the HTML file.
     * @throws RuntimeException If an error occurs while reading or writing files.
     */
    public static void getGraphHTML(Graph g) {

        // Read the content of the template HTML file
        String path = System.getProperty("user.dir") + "/html_files";
        String htmlContent = "";
        try {
            htmlContent = HtmlLoader.readFileToString(new File(path + "/temp_graph.html"));
        } catch (Exception ignored) {
            // Handle the exception or log the error as needed
        }

        // Build the nodes data in the format required for the HTML file
        StringBuilder nodes = new StringBuilder();
        Map<String, Integer> nodeToIndex = new HashMap<>();
        nodes.append("[ ");
        for (int i = 0; i < g.size(); i++) {
            Node node = g.get(i);
            nodes.append("{id: ").append(i + 1).append(", label: \"").append(node.getName()).append("\"} ");
            if (i != g.size() - 1) {
                nodes.append(", ");
            }
            nodeToIndex.put(node.getName(), i + 1);
        }
        nodes.append("];");

        // Build the edges data in the format required for the HTML file
        StringBuilder edges = new StringBuilder();
        edges.append("[ ");
        for (int i = 0; i < g.size(); i++) {
            for (Node node : g.get(i).getEdges()) {
                int index = nodeToIndex.get(node.getName());
                edges.append("{source: ").append(i + 1).append(", target: ").append(index).append("}");
                if (i != g.size() - 1) {
                    edges.append(", ");
                }
            }
        }
        edges.append("];");

        // Insert the nodes and edges data into the HTML content
        htmlContent = htmlContent.replace("NODE_PLACEHOLDER;", nodes);
        htmlContent = htmlContent.replace("LINK_PLACEHOLDER;", edges);

        // Save the updated HTML content to a new file
        try {
            Files.write(Paths.get(path + "/graph.html"), htmlContent.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Optionally, log success message or handle successful creation
        // System.out.println("Graph HTML file created successfully.");
    }
}
