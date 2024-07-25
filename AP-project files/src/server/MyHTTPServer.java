package server;

import servlet.Servlet;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.*;
import java.net.ServerSocket;

/**
 * An HTTP server implementation that manages servlets and handles HTTP requests.
 */
public class MyHTTPServer extends Thread implements HTTPServer {

    /** Map to store GET request servlets by URI. */
    private ConcurrentHashMap<String, Servlet> get_map = new ConcurrentHashMap<>();

    /** Map to store POST request servlets by URI. */
    private ConcurrentHashMap<String, Servlet> post_map = new ConcurrentHashMap<>();

    /** Map to store DELETE request servlets by URI. */
    private ConcurrentHashMap<String, Servlet> delete_map = new ConcurrentHashMap<>();

    /** Thread pool to handle client requests. */
    private ExecutorService threadPool;

    /** Server socket to accept client connections. */
    private ServerSocket serverSocket;

    /** Flag to indicate whether the server should stop running. */
    private volatile boolean stopServer = false;

    /** Port number the server listens on. */
    final private int portNum;

    /** Number of threads in the thread pool. */
    final private int nThreads;

    /**
     * Constructs a MyHTTPServer with the specified port number and number of threads.
     *
     * @param port The port number the server listens on.
     * @param nThreads The number of threads in the thread pool.
     */
    public MyHTTPServer(int port, int nThreads) {
        // Set up the thread pool with a maximum of nThreads threads
        threadPool = Executors.newFixedThreadPool(nThreads);
        // Set the port number
        portNum = port;
        // Set the number of threads
        this.nThreads = nThreads;
    }

    /**
     * Adds a servlet to handle requests matching the specified HTTP command and URI.
     *
     * @param httpCommand The HTTP command (e.g., GET, POST, DELETE) that the servlet will handle.
     * @param uri The URI that the servlet will handle.
     * @param s The servlet to be added.
     */
    public void addServlet(String httpCommand, String uri, Servlet s) {
        // Check for null inputs
        if (uri == null || s == null) {
            return;
        }

        httpCommand = httpCommand.toUpperCase();

        switch (httpCommand) {
            case "GET":
                get_map.put(uri, s);
                break;
            case "POST":
                post_map.put(uri, s);
                break;
            case "DELETE":
                delete_map.put(uri, s);
                break;
        }
    }

    /**
     * Removes a servlet that handles requests matching the specified HTTP command and URI.
     *
     * @param httpCommand The HTTP command (e.g., GET, POST, DELETE) that the servlet handles.
     * @param uri The URI that the servlet handles.
     */
    public void removeServlet(String httpCommand, String uri) {
        if (uri == null) {
            return;
        }

        httpCommand = httpCommand.toUpperCase();

        switch (httpCommand) {
            case "GET":
                get_map.remove(uri);
                break;
            case "POST":
                post_map.remove(uri);
                break;
            case "DELETE":
                delete_map.remove(uri);
                break;
        }
    }

    /**
     * Runs the HTTP server, accepting client connections and handling requests.
     */
    public void run() {
        // Create the server socket
        try (ServerSocket serverSocket = new ServerSocket(portNum)) {
            // Define 1s timeout
            serverSocket.setSoTimeout(1000);

            while (!stopServer) {
                try {
                    // Accept a new connection
                    Socket client = serverSocket.accept();

                    // Each connected client will go through this procedure when it connects to the server
                    threadPool.submit(() -> {
                        try {
                            // Delay for receiving the request correctly
                            Thread.sleep(125);
                            // Get the client input
                            BufferedReader reader = getBufferedReader(client);

                            // Parse the request
                            RequestParser.RequestInfo ri = RequestParser.parseRequest(reader);
                            ConcurrentHashMap<String, Servlet> servletMap;
                            if (ri != null) {
                                switch (ri.getHttpCommand()) {
                                    case "GET":
                                        servletMap = get_map;
                                        break;
                                    case "POST":
                                        servletMap = post_map;
                                        break;
                                    case "DELETE":
                                        servletMap = delete_map;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Unsupported HTTP command: " + ri.getHttpCommand());
                                }

                                // Search for the longest URI match
                                String longestMatch = "";
                                Servlet servlet = null;
                                for (Map.Entry<String, Servlet> entry : servletMap.entrySet()) {
                                    if (ri.getUri().startsWith(entry.getKey()) && entry.getKey().length() > longestMatch.length()) {
                                        longestMatch = entry.getKey();
                                        servlet = entry.getValue();
                                    }
                                }

                                // If servlet is not null, activate the handle() method
                                if (servlet != null) {
                                    servlet.handle(ri, client.getOutputStream());
                                }
                            }
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        } finally {
                            // Close the client socket
                            try {
                                client.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    // accept() timeout exception, do nothing
                    if (stopServer) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a BufferedReader for the specified client socket.
     *
     * @param client The client socket.
     * @return A BufferedReader for the client socket.
     * @throws IOException If an I/O error occurs.
     */
    private static BufferedReader getBufferedReader(Socket client) throws IOException {
        InputStream inputStream = client.getInputStream();
        int availableBytes = inputStream.available();
        byte[] buffer = new byte[availableBytes];
        int bytesRead = inputStream.read(buffer, 0, availableBytes);

        return new BufferedReader(
                new InputStreamReader(
                        new ByteArrayInputStream(buffer, 0, bytesRead)
                )
        );
    }

    /**
     * Starts the HTTP server, allowing it to begin handling requests.
     */
    public void start() {
        stopServer = false;
        super.start();
    }

    /**
     * Closes the HTTP server, stopping it from handling requests.
     */
    public void close() {
        stopServer = true;
        threadPool.shutdownNow();
    }

    /**
     * Gets the thread pool used by the server.
     *
     * @return The thread pool.
     */
    public Object getThreadPool() {
        return threadPool;
    }
}
