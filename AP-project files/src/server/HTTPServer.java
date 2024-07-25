package server;

import servlet.Servlet;

/**
 * An interface representing an HTTP server that can manage servlets and handle HTTP requests.
 */
public interface HTTPServer extends Runnable {

    /**
     * Adds a servlet to handle requests matching the specified HTTP command and URI.
     *
     * @param httpCommand The HTTP command (e.g., GET, POST) that the servlet will handle.
     * @param uri The URI that the servlet will handle.
     * @param s The servlet to be added.
     */
    public void addServlet(String httpCommand, String uri, Servlet s);

    /**
     * Removes a servlet that handles requests matching the specified HTTP command and URI.
     *
     * @param httpCommand The HTTP command (e.g., GET, POST) that the servlet handles.
     * @param uri The URI that the servlet handles.
     */
    public void removeServlet(String httpCommand, String uri);

    /**
     * Starts the HTTP server, allowing it to begin handling requests.
     */
    public void start();

    /**
     * Closes the HTTP server, stopping it from handling requests.
     */
    public void close();
}
