package servlet;

import server.RequestParser;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * A servlet that handles HTTP requests for performing a subtraction operation.
 * <p>
 * This servlet processes HTTP requests, extracts parameters to perform a subtraction, and sends the result back as an HTTP response.
 * </p>
 */
public class SubServlet implements Servlet {

    /**
     * Handles an HTTP request by performing a subtraction operation based on parameters provided in the request.
     * <p>
     * The servlet expects two parameters: "a" and "b". It parses these parameters as integers, calculates the difference (a - b),
     * and returns the result in the HTTP response body.
     * </p>
     *
     * @param requestInfo The {@link RequestParser.RequestInfo} object containing details about the HTTP request, including parameters.
     * @param responseStream The {@link OutputStream} to which the HTTP response should be written.
     * @throws IOException If an I/O error occurs while processing the request or writing the response.
     */
    @Override
    public void handle(RequestParser.RequestInfo requestInfo, OutputStream responseStream) throws IOException {
        // Extract parameters from the request
        Map<String, String> parameters = requestInfo.getParameters();
        int a = Integer.parseInt(parameters.getOrDefault("a", "0"));
        int b = Integer.parseInt(parameters.getOrDefault("b", "0"));
        int sub = a - b;
        String response = "Result: " + sub;

        // Write the HTTP response
        responseStream.write(("HTTP/1.1 200 OK\n").getBytes());
        responseStream.write(("Content-Length: " + response.length() + "\n").getBytes());
        responseStream.write(("\n").getBytes());
        responseStream.write(response.getBytes());
        responseStream.write(("\n").getBytes());
        responseStream.flush();
    }

    /**
     * Closes any resources associated with this servlet.
     * <p>
     * This implementation does not hold any resources to close and hence this method does nothing.
     * </p>
     *
     * @throws IOException If an I/O error occurs while closing resources. (Not applicable in this implementation.)
     */
    @Override
    public void close() throws IOException {
        // No resources to close
    }
}
