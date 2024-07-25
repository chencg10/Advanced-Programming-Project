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


//    public static void main(String[] args) {
//        String httpCommand = """
//               GET /app/index.html HTTP/1.1
//               Host: localhost:8080
//               Connection: keep-alive
//               Cache-Control: max-age=0
//               sec-ch-ua: "Not/A)Brand";v="8", "Chromium";v="126", "Google Chrome";v="126"
//               sec-ch-ua-mobile: ?0
//               sec-ch-ua-platform: "macOS"
//               Upgrade-Insecure-Requests: 1
//               User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36
//               Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
//               Sec-Fetch-Site: cross-site
//               Sec-Fetch-Mode: navigate
//               Sec-Fetch-User: ?1
//               Sec-Fetch-Dest: document
//               Accept-Encoding: gzip, deflate, br, zstd
//               Accept-Language: he-IL,he;q=0.9,en-US;q=0.8,en;q=0.7
//
//               """;
//        BufferedReader input = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(httpCommand.getBytes())));
//        try {
//            server.RequestParser.RequestInfo requestInfo = server.RequestParser.parseRequest(input);
//            String currentWorkingDirectory = System.getProperty("user.dir") + "/html_files";
//            HtmlLoader htmlLoader = new HtmlLoader(currentWorkingDirectory);
//            assert requestInfo != null;
//            htmlLoader.handle(requestInfo, System.out);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
