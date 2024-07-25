package servlet;

import graph.Message;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;
import server.RequestParser;
import graph.GenericConfig;
import graph.Graph;
import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A servlet that displays and manages topics and their last messages.
 * <p>
 * This servlet handles HTTP requests to display topics and their last messages in an HTML table. It also allows publishing
 * messages to specific topics. The servlet uses a singleton instance of the {@link TopicManager} to interact with topics.
 * </p>
 */
public class TopicDisplayer implements Servlet {
    private static final Map<String, String> topicsAndMessages = new ConcurrentHashMap<>();
    private PrintWriter clientOut;
    private static StringBuilder htmlContent = new StringBuilder();

    /**
     * Handles an HTTP request by displaying topics and their last messages, or publishing a message to a specified topic.
     * <p>
     * If there are topics available, it retrieves and displays the last message of each topic in an HTML table. If the HTTP
     * request contains a topic name and message, it publishes the message to the specified topic and updates the display.
     * If no topics are available, it returns a predefined HTML page (temp.html).
     * </p>
     *
     * @param ri The {@link RequestParser.RequestInfo} object containing details about the HTTP request, including parameters.
     * @param toClient The {@link OutputStream} to which the HTTP response should be written.
     * @throws IOException If an I/O error occurs while processing the request or writing the response.
     */
    @Override
    public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
        // Initialize the table
        TopicManager tm = TopicManagerSingleton.get();
        clientOut = new PrintWriter(toClient);

        // Check if there are any topics in the topic manager
        if (!tm.getTopics().isEmpty()) {
            // Get all topics and their last messages
            for (String topicName : tm.getTopics().keySet()) {
                // Remove the 'T' prefix if present
                if (topicName.startsWith("T")) {
                    topicName = topicName.substring(1);
                }
                topicsAndMessages.put("T" + topicName, tm.getTopic(topicName).getLastMessage().getContent());
            }


            // Get the topic name and message from the HTTP request
            Map<String, String> params = ri.getParameters();
            String topicName = params.get("Topic+name");
            String message = params.get("Message");
            // Check if the topic name and message are not null
            if (topicName == null || message == null) {
                // check if there was any html content sent to the client
                if (htmlContent.isEmpty()) {
                    // Load and send temp.html if no topics are available
                    loadTempHtml();
                } else { // if its not, just send the temp html page
                    // if its not, just send the last html page
                    sendHtmlResponse();
                }
                return;
            }


            // Check if the topic name is in the map
            Set<String> topicNamesSet = topicsAndMessages.keySet();
            if (!topicNamesSet.contains(topicName)) {
                // check if there was any html content sent to the client
                if (htmlContent.isEmpty()) {
                    // Load and send temp.html if no topics are available
                    loadTempHtml();
                } else {
                    // if it's not, send the last html page
                    sendHtmlResponse();
                }
            }
            else {
                // Publish the message to the topic
                tm.getTopic(topicName.substring(1)).publish(new Message(message));

                // Update the map with the latest messages
                for (String topic : topicNamesSet) {
                    topicsAndMessages.put(topic, tm.getTopic(topic.substring(1)).getLastMessage().getContent());
                }

                // Generate HTML content for the response
                htmlContent.append("<html>" +
                        "<body>" +
                        "<table border='1'>" +
                        "<tr><th>Topic Name</th><th>Last Message</th></tr>");
                for (Map.Entry<String, String> entry : topicsAndMessages.entrySet()) {
                    htmlContent.append("<tr><td>")
                            .append(entry.getKey())
                            .append("</td><td>")
                            .append(entry.getValue())
                            .append("</td></tr>");
                }
                htmlContent.append("</table>" + "</body></html>");

                // Write the HTTP response
                sendHtmlResponse();

                // Clear the html content
                htmlContent.setLength(0);
            }
        }
        else {
            // Load and send temp.html if no topics are available
            loadTempHtml();
        }
    }

    private void sendHtmlResponse() {
        clientOut.println("HTTP/1.1 200 OK");
        clientOut.println("Content-Type: text/html");
        clientOut.println("Connection: close");
        clientOut.println("Content-Length: " + htmlContent.length());
        clientOut.println();
        clientOut.println(htmlContent);
        clientOut.flush();
    }

    private void loadTempHtml() throws IOException {
        String path = System.getProperty("user.dir") + "/html_files";
        String htmlContent = HtmlLoader.readFileToString(new File(path + "/temp.html"));
        clientOut.println("HTTP/1.1 200 OK");
        clientOut.println("Content-Type: text/html");
        clientOut.println("Connection: close");
        clientOut.println("Content-Length: " + htmlContent.length());
        clientOut.println();
        clientOut.println(htmlContent);
        clientOut.flush();
    }

    /**
     * Resets the topics and messages map by clearing it and resetting the {@link TopicManager}.
     * <p>
     * This method is useful for reinitializing the servlet's state, typically when starting a new session or resetting data.
     * </p>
     */
    public static void resetTopicsAndMessages() {
        // Reset the map by overwriting it with a new one
        topicsAndMessages.clear();
        // clear the html content
        htmlContent.setLength(0);
        TopicManager tm = TopicManagerSingleton.get();
        // clear the graph
        tm.clear();

    }

    /**
     * Closes any resources associated with this servlet.
     * <p>
     * This implementation closes the {@link PrintWriter} used to write the HTTP response.
     * </p>
     *
     * @throws IOException If an I/O error occurs while closing the resource.
     */
    @Override
    public void close() throws IOException {
        clientOut.close();
    }

//    public static void main(String[] args) {
//        String http_req = """
//                GET /publish?Topic+name=TD&Message=13 HTTP/1.1
//                Host: localhost:8080
//                Connection: keep-alive
//                sec-ch-ua: "Not/A)Brand";v="8", "Chromium";v="126", "Google Chrome";v="126"
//                sec-ch-ua-mobile: ?0
//                sec-ch-ua-platform: "macOS"
//                Upgrade-Insecure-Requests: 1
//                User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36
//                Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
//                Sec-Fetch-Site: same-origin
//                Sec-Fetch-Mode: navigate
//                Sec-Fetch-User: ?1
//                Sec-Fetch-Dest: iframe
//                Referer: http://localhost:8080/app/form.html
//                Accept-Encoding: gzip, deflate, br, zstd
//                Accept-Language: he-IL,he;q=0.9,en-US;q=0.8,en;q=0.7
//                Cookie: Idea-a24beeec=aa279d8e-d87e-4c09-b6e9-c2f360d27466
//
//                """;
//
//        BufferedReader input = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(http_req.getBytes())));
//        // create a graph
//        String path = System.getProperty("user.dir") + "/uploads/simple.conf";
//        GenericConfig config = new GenericConfig();
//        config.setConfFile(path);
//        config.create();
//
//        Graph graph = new Graph();
//        graph.createFromTopics();
//
//        TopicDisplayer topicDisplayer = new TopicDisplayer();
//
//        try {
//            server.RequestParser.RequestInfo requestInfo = server.RequestParser.parseRequest(input);
//            assert requestInfo != null;
//            topicDisplayer.handle(requestInfo, System.out);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//         http_req = """
//                GET /publish?Topic+name=TC&Message=13 HTTP/1.1
//                Host: localhost:8080
//                Connection: keep-alive
//                sec-ch-ua: "Not/A)Brand";v="8", "Chromium";v="126", "Google Chrome";v="126"
//                sec-ch-ua-mobile: ?0
//                sec-ch-ua-platform: "macOS"
//                Upgrade-Insecure-Requests: 1
//                User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36
//                Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
//                Sec-Fetch-Site: same-origin
//                Sec-Fetch-Mode: navigate
//                Sec-Fetch-User: ?1
//                Sec-Fetch-Dest: iframe
//                Referer: http://localhost:8080/app/form.html
//                Accept-Encoding: gzip, deflate, br, zstd
//                Accept-Language: he-IL,he;q=0.9,en-US;q=0.8,en;q=0.7
//                Cookie: Idea-a24beeec=aa279d8e-d87e-4c09-b6e9-c2f360d27466
//
//                """;
//        input = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(http_req.getBytes())));
//
//        try {
//            server.RequestParser.RequestInfo requestInfo = server.RequestParser.parseRequest(input);
//            assert requestInfo != null;
//            topicDisplayer.handle(requestInfo, System.out);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

}
