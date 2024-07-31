package servlet;

import server.RequestParser;
import java.io.*;

/**
 * A servlet class that loads HTML or CSS files based on the request URI
 * and sends the file content back to the client.
 */
public class HtmlLoader implements Servlet {
    private final String path;
    private PrintWriter clientOut;

    /**
     * Constructs an HtmlLoader with the specified path to the HTML files.
     *
     * @param path The path to the directory containing HTML files.
     */
    public HtmlLoader(String path) {
        // Save the path to the HTML files
        this.path = path;
    }

    /**
     * Handles the HTTP request, checks if the requested file exists,
     * and sends the file content back to the client.
     *
     * @param ri       The RequestInfo object containing details about the HTTP request.
     * @param toClient The OutputStream to write the response to the client.
     * @throws IOException If an I/O error occurs while processing the request.
     */
    @Override
    public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
        clientOut = new PrintWriter(toClient);

        // Get the URI
        String uri = ri.getUri();

        // Check if the URI starts with /app
        if (uri.startsWith("/app/")) {
            // Get the HTML file name
            String fileName = ri.getUriSegments()[1];

            // Check if the HTML file exists in the html_files directory
            File file = new File(path + "/" + fileName);
            if (!file.exists()) {
                // Write the response
                clientOut.println("HTTP/1.1 404 Not Found");
                clientOut.println();
                clientOut.println("404 Not Found");
                clientOut.flush();
                return;
            }

            // Get the HTML file content
            String fileContent = readFileToString(file);
            // Write the response headers
            toClient.write("HTTP/1.1 200 OK\n".getBytes());
            if (fileName.endsWith(".html"))
                toClient.write("Content-Type: text/html\n".getBytes());
            else if (fileName.endsWith(".css"))
                toClient.write("Content-Type: text/css\n".getBytes());

            toClient.write(("Content-Length: " + fileContent.length() + "\n\n").getBytes());
            // Write the file content
            toClient.write(fileContent.getBytes());
            toClient.flush();
        }
    }

    /**
     * Reads the content of a file and returns it as a string.
     *
     * @param file The file to read.
     * @return The content of the file as a string.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public static String readFileToString(File file) throws IOException {
        StringBuilder fileContent = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                fileContent.append(line).append("\n"); // Append newline for each line read
            }
        }
        return fileContent.toString();
    }

    /**
     * Closes any resources associated with the servlet.
     *
     * @throws IOException If an I/O error occurs while closing resources.
     */
    @Override
    public void close() throws IOException {
        clientOut.close();
    }
}
