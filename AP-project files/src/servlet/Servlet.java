package servlet;

import java.io.IOException;
import java.io.OutputStream;
import server.RequestParser.RequestInfo;

/**
 * Represents a servlet that processes HTTP requests and generates HTTP responses.
 * <p>
 * Implementations of this interface handle incoming requests and produce responses to be sent back to the client.
 * </p>
 */
public interface Servlet {

    /**
     * Processes an HTTP request and sends the response to the client.
     *
     * @param ri       The {@link RequestInfo} object containing details about the HTTP request.
     * @param toClient The {@link OutputStream} to which the response should be written.
     * @throws IOException If an I/O error occurs while processing the request or writing the response.
     */
    void handle(RequestInfo ri, OutputStream toClient) throws IOException;

    /**
     * Closes any resources associated with the servlet.
     * <p>
     * This method is typically used to perform cleanup operations and release resources that the servlet might be holding.
     * </p>
     *
     * @throws IOException If an I/O error occurs while closing resources.
     */
    void close() throws IOException;
}

