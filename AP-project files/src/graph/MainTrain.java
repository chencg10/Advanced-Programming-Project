package graph;
import server.*;
import servlet.*;

public class MainTrain {
    public static void main(String[] args) throws Exception {
        HTTPServer server = new MyHTTPServer(8080, 5);
        server.addServlet("GET", "/publish", new TopicDisplayer());
        server.addServlet("POST", "/upload", new ConfLoader());
        server.addServlet("GET", "/app/", new HtmlLoader(System.getProperty("user.dir") + "/html_files"));

        server.start();
        System.in.read();
        server.close();
        System.out.println("done");

    }
}