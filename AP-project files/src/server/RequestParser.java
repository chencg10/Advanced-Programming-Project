package server;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A utility class for parsing HTTP requests.
 */
public class RequestParser {

    /**
     * Parses an HTTP request from the provided BufferedReader.
     *
     * @param reader The BufferedReader to read the HTTP request from.
     * @return A RequestInfo object containing details about the parsed request.
     * @throws IOException If an I/O error occurs while reading the request.
     */
    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {
        // print the http command:
        // Mark the BufferedReader before reading
        reader.mark(1000000);
        System.out.println("----------------Start of http command---------------------");
        printHttpCommand(new BufferedReader(reader));
        System.out.println("----------------End of http command---------------------");
        // Reset the BufferedReader to the marked position
        reader.reset();

        // Read the request line
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            return null;
        }

        // Split the request line into parts
        String[] requestParts = requestLine.split(" ");
        if (requestParts.length < 3) {
            return null;
        }
        String httpCommand = requestParts[0];
        String uri = requestParts[1];

        // Split the URI into path and query string
        String[] uriParts = Arrays.stream(uri.split("\\?")[0].split("/"))
                .filter(segment -> !segment.isEmpty())
                .toArray(String[]::new);

        // Parse the parameters from the query string
        Map<String, String> parameters = new HashMap<>();
        String[] uriParams = uri.split("\\?");
        if (uriParams.length == 2) {
            uriParams = uriParams[1].split("&");
        }
        for (String param : uriParams) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                parameters.put(keyValue[0], keyValue[1]);
            } else if (keyValue.length == 1) {
                parameters.put(keyValue[0], ""); // Handle case where parameter has no value
            }
        }

        // Initialize headers map
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            if (line.startsWith("------")) {
                break;
            }
            String[] headerParts = line.split(": ");
            if (headerParts.length == 2) {
                headers.put(headerParts[0].trim(), headerParts[1].trim());
            }
        }

        // Get the additional settings
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            if (httpCommand.equals("GET")) {
                String[] pair = line.split("=");
                parameters.put(pair[0].trim(), pair[1].trim());
            } else if (httpCommand.equals("POST")) {
                String[] parts = line.split("; ");
                for (String pair : parts) {
                    String[] keyValue = pair.split("=");
                    if (keyValue.length == 2) {
                        parameters.put(keyValue[0].trim(), keyValue[1].trim());
                    }
                }
            }
        }

        // Read the content
        ByteArrayOutputStream contentStream = new ByteArrayOutputStream();
        boolean readChar = false;
        int currentChar;
        while ((currentChar = reader.read()) != -1) {
            if (currentChar == '-') {
                break;
            }
            contentStream.write(currentChar);
            if (currentChar == '\n') {
                if (readChar) {
                    reader.mark(1);
                    int nextChar = reader.read();
                    reader.reset();
                    if (nextChar == '\n' || nextChar == -1) {
                        break;
                    }
                } else {
                    break;
                }
            } else {
                readChar = true;
            }
        }

        reader.close();
        return new RequestInfo(httpCommand, uri, uriParts, parameters, contentStream.toByteArray(), headers);
    }

    /**
     * Parses the parameters from a query string.
     *
     * @param paramString The query string to parse.
     * @return A map of parameter names to values.
     */
    private static Map<String, String> parseParameters(String paramString) {
        Map<String, String> parameters = new HashMap<>();
        String[] pairs = paramString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                parameters.put(keyValue[0], keyValue[1]);
            } else if (keyValue.length == 1) {
                parameters.put(keyValue[0], ""); // Handle case where parameter has no value
            }
        }
        return parameters;
    }

    /**
     * Prints the HTTP command from the provided BufferedReader.
     *
     * @param reader The BufferedReader to read the HTTP command from.
     * @throws IOException If an I/O error occurs while reading the command.
     */
    private static void printHttpCommand(BufferedReader reader) throws IOException {
        BufferedReader copy_of_reader = new BufferedReader(reader);
        String line;
        StringBuilder headersAndBody = new StringBuilder();

        // Read and print headers
        while ((line = copy_of_reader.readLine()) != null && !line.isEmpty()) {
            headersAndBody.append(line).append("\n");
        }

        // Attempt to find the Content-Length header to read the body
        int contentLength = -1;
        for (String headerLine : headersAndBody.toString().split("\n")) {
            if (headerLine.startsWith("Content-Length: ")) {
                contentLength = Integer.parseInt(headerLine.split(": ")[1].trim());
                break;
            }
        }

        // If Content-Length is found, read that many bytes as the body
        if (contentLength > -1) {
            char[] bodyChars = new char[contentLength];
            copy_of_reader.read(bodyChars, 0, contentLength);
            headersAndBody.append(new String(bodyChars));
        }

        System.out.println(headersAndBody.toString());
    }

    /**
     * A class to store information about an HTTP request.
     */
    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final byte[] content;
        private final Map<String, String> headers;

        /**
         * Constructs a RequestInfo object with the specified details.
         *
         * @param httpCommand The HTTP command (e.g., GET, POST, DELETE) of the request.
         * @param uri The URI of the request.
         * @param uriSegments The segments of the URI.
         * @param parameters The parameters of the request.
         * @param content The content of the request.
         * @param headers The headers of the request.
         */
        public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, byte[] content, Map<String, String> headers) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.content = content;
            this.headers = new HashMap<>(headers);
        }

        /**
         * Gets the HTTP command of the request.
         *
         * @return The HTTP command.
         */
        public String getHttpCommand() {
            return httpCommand;
        }

        /**
         * Gets the URI of the request.
         *
         * @return The URI.
         */
        public String getUri() {
            return uri;
        }

        /**
         * Gets the segments of the URI.
         *
         * @return The URI segments.
         */
        public String[] getUriSegments() {
            return uriSegments;
        }

        /**
         * Gets the parameters of the request.
         *
         * @return The parameters.
         */
        public Map<String, String> getParameters() {
            return parameters;
        }

        /**
         * Gets the content of the request.
         *
         * @return The content.
         */
        public byte[] getContent() {
            return content;
        }

        /**
         * Gets the headers of the request.
         *
         * @return The headers.
         */
        public Map<String, String> getHeaders() {
            return headers;
        }
    }
}


//    public static void main(String[] args) {
//        String request = """
//                POST /upload HTTP/1.1
//                Host: localhost:8080
//                Connection: keep-alive
//                Content-Length: 251
//                Cache-Control: max-age=0
//                sec-ch-ua: "Not/A)Brand";v="8", "Chromium";v="126", "Google Chrome";v="126"
//                sec-ch-ua-mobile: ?0
//                sec-ch-ua-platform: "macOS"
//                Upgrade-Insecure-Requests: 1
//                Origin: http://localhost:63342
//                Content-Type: multipart/form-data; boundary=----WebKitFormBoundarysX4KKY95zxBZmrJf
//                User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36
//                Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
//                Sec-Fetch-Site: same-site
//                Sec-Fetch-Mode: navigate
//                Sec-Fetch-User: ?1
//                Sec-Fetch-Dest: document
//                Referer: http://localhost:63342/
//                Accept-Encoding: gzip, deflate, br, zstd
//                Accept-Language: he-IL,he;q=0.9,en-US;q=0.8,en;q=0.7
//                Cookie: Idea-a24beeec=aa279d8e-d87e-4c09-b6e9-c2f360d27466
//                ------WebKitFormBoundarysX4KKY95zxBZmrJf
//                Content-Disposition: form-data; name="file"; filename="simple.conf"
//                Content-Type: application/octet-stream
//
//                EX1.configs.PlusAgent
//                A,B
//                C
//                EX1.configs.IncAgent
//                C
//                D
//                ------WebKitFormBoundarysX4KKY95zxBZmrJf--
//                """;
//        BufferedReader reader = new BufferedReader(new StringReader(request));
//        try {
//            RequestInfo requestInfo = parseRequest(reader);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
