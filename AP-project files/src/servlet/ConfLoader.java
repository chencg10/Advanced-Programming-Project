package servlet;

import graph.GenericConfig;
import graph.Graph;
import server.RequestParser;
import views.HtmlGraphWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * A servlet class that handles configuration file uploads and generates
 * corresponding HTML graphs.
 */
public class ConfLoader implements Servlet {

    private PrintWriter clientOut;

    /**
     * Handles the HTTP request, processes the configuration file, and generates
     * an HTML response.
     *
     * @param ri       The RequestInfo object containing details about the HTTP request.
     * @param toClient The OutputStream to write the response to the client.
     * @throws IOException If an I/O error occurs while processing the request.
     */
    @Override
    public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {

        // Use a PrintWriter to write the response
        clientOut = new PrintWriter(toClient);

        // Reset the topics map in the TopicDisplayer servlet
        TopicDisplayer.resetTopicsAndMessages();

        // Get the file name from the parameters
        String fileName = ri.getParameters().get("filename");

        // Trim the quotes from the file name
        if (fileName != null) {
            fileName = fileName.replace("\"", "");
        }

        // Get the file content from the body
        String fileContent = new String(ri.getContent(), StandardCharsets.UTF_8);

        Path filePath;
        // If file was uploaded, save it
        if (fileName != null) {
            String currentWorkingDirectory = System.getProperty("user.dir") + "/uploads";
            Path directoryPath = Paths.get(currentWorkingDirectory);
            Files.createDirectories(directoryPath);
            // Save the content as a .conf file
            filePath = Paths.get(currentWorkingDirectory + "/" + fileName);
            Files.writeString(filePath, fileContent);
        } else {
            System.out.println("No file uploaded.");
            filePath = null;
        }

        // Create graph from config file
        GenericConfig config = new GenericConfig();
        config.setConfFile(String.valueOf(filePath));
        config.create();

        // Create graph
        Graph graph = new Graph();
        graph.createFromTopics();

        // Verify that the graph has no cycles
        if (graph.hasCycles()) {
            // Send an HTML response to the client that the graph has cycles
            String path = System.getProperty("user.dir") + "/html_files";
            String htmlContent = HtmlLoader.readFileToString(new File(path + "/cycles.html"));
            clientOut.println("HTTP/1.1 200 OK");
            clientOut.println("Content-Type: text/html");
            clientOut.println("Connection: close");
            clientOut.println("Content-Length: " + htmlContent.length());
            clientOut.println();
            clientOut.println(htmlContent);
            clientOut.flush();
            return;
        }

        // Create the graph HTML
        HtmlGraphWriter.getGraphHTML(graph);

        // Change path to the html_files directory
        String path = System.getProperty("user.dir") + "/html_files";
        String htmlContent = HtmlLoader.readFileToString(new File(path + "/graph.html"));
        clientOut.println("HTTP/1.1 200 OK");
        clientOut.println("Content-Type: text/html");
        clientOut.println("Connection: close");
        clientOut.println("Content-Length: " + htmlContent.length());
        clientOut.println();
        clientOut.println(htmlContent);
        clientOut.flush();
    }

    /**
     * Closes any resources associated with the servlet.
     *
     * @throws IOException If an I/O error occurs while closing resources.
     */
    @Override
    public void close() throws IOException {
        // Any cleanup code can go here
        clientOut.close();
    }

    /**
     * Extracts the boundary string from the content type.
     *
     * @param contentType The content type containing the boundary.
     * @return The boundary string, or null if not found.
     */
    private String getBoundary(String contentType) {
        if (contentType == null) return null;
        for (String param : contentType.split(";")) {
            param = param.trim();
            if (param.startsWith("boundary=")) {
                return param.substring("boundary=".length());
            }
        }
        return null;
    }

    /**
     * Extracts the file name from the content disposition.
     *
     * @param contentDisposition The content disposition containing the file name.
     * @return The file name, or null if not found.
     */
    private String getFileName(String contentDisposition) {
        for (String param : contentDisposition.split(";")) {
            param = param.trim();
            if (param.startsWith("filename=")) {
                return param.substring("filename=".length()).replace("\"", "");
            }
        }
        return null;
    }
}




/*
 implemnet the ConfLoader servlet so that it handels the first form of the form.html file : . It will exctract the name of the file and its content, save the content in the server side. Load the GenericConfig according to the file content and create an instance of the Graph from the config file. Anser an http answer that conatins the HTML of the graphic calc graph
*/