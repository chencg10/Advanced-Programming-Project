package graph;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;

import server.RequestParser.RequestInfo;
import servlet.SubServlet;



public class EX5_TESTER {
     private static void testParseRequest() {
        // Test data
        String request = "GET /api/resource?id=123&name=test HTTP/1.1\n" +
                "Host: example.com\n" +
                "Content-Length: 5\n"+
                "\n" +
                "filename=\"hello_world.txt\"\n"+
                "\n" +
                "hello world!\n"+
                "\n" ;
        /*String request = "GET /calculate?a=5&b=3 HTTP/1.1\n" +
                "Host: localhost\n" +
                "\n";*/

        BufferedReader input=new BufferedReader(new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
        try {
            server.RequestParser.RequestInfo requestInfo = server.RequestParser.parseRequest(input);

            // Test HTTP command
            if (!requestInfo.getHttpCommand().equals("GET")) {
                System.out.println("HTTP command test failed (-5)");
            }

            // Test URI
            if (!requestInfo.getUri().equals("/api/resource?id=123&name=test")) {
                System.out.println("URI test failed (-5)");
            }

            // Test URI segments
            String[] expectedUriSegments = {"api", "resource"};
            if (!Arrays.equals(requestInfo.getUriSegments(), expectedUriSegments)) {
                System.out.println("URI segments test failed (-5)");
                for(String s : requestInfo.getUriSegments()){
                    System.out.println(s);
                }
            }
            // Test parameters
            Map<String, String> expectedParams = new HashMap<>();
            expectedParams.put("id", "123");
            expectedParams.put("name", "test");
            expectedParams.put("filename","\"hello_world.txt\"");
            if (!requestInfo.getParameters().equals(expectedParams)) {
                System.out.println("Parameters test failed (-5)");
            }

            // Test content
            byte[] expectedContent = "hello world!\n".getBytes();
            if (!Arrays.equals(requestInfo.getContent(), expectedContent)) {
                System.out.println("Content test failed (-5)");
            }
            input.close();
        } catch (IOException e) {
            System.out.println("Exception occurred during parsing: " + e.getMessage() + " (-5)");
        }
    }


    public static void testServer() throws Exception
    {
        try {
            // Count active threads before starting the server
            int initialThreadCount = Thread.activeCount();
            //System.out.println("initialThreadCount: "+initialThreadCount);
            // Step 1: Create the server
            server.MyHTTPServer server = new server.MyHTTPServer(8080, 5);
            server.addServlet("GET", "/calculate", new SubServlet());



            // Step 2: Start the server
            server.start();

            // Give the server some time to start up
            Thread.sleep(1000);

            // Count active threads after starting the server
            int threadCountAfterStart = Thread.activeCount();
            //System.out.println("threadCountAfterStart: "+threadCountAfterStart);

//            // Check that only one new thread has been created
//            if (threadCountAfterStart == initialThreadCount + 1) {
//                System.out.println("Server started with one additional thread.");
//            } else {
//                System.err.println("Server did not start with one additional thread. Initial: "
//                        + initialThreadCount + ", After start: " + threadCountAfterStart);
//            }

            //active count This represents the approximate number of threads that are actively executing task.
            //This represents the current number of threads in the pool. This includes both active threads (executing tasks) and idle threads (waiting for tasks).
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) server.getThreadPool();
            int initialActiveCount = threadPoolExecutor.getActiveCount();
            int initialPoolSize = threadPoolExecutor.getPoolSize();




            // Step 3: Create a client socket to connect to the server
            try (Socket clientSocket = new Socket("localhost", 8080))
            {
                // Step 4: Send an appropriate HTTP request to the server
                OutputStream outputStream = clientSocket.getOutputStream();
                String request = "GET /calculate?a=5&b=3 HTTP/1.1\n" +
                        "Host: localhost\n" +
                        "\n";
                outputStream.write(request.getBytes());
                //make sure that the request is sent immediately after it is written to the output stream
                outputStream.flush();


                // Give the server some time to process the request so we can see the change in the pool size and active count
                Thread.sleep(1000);

                // Step 6: Monitor thread pool state after client connection,we want to check that if the server is work
                //when a client is connected to him it create for him a new thread
                int currentActiveCount = threadPoolExecutor.getActiveCount();
                int currentPoolSize = threadPoolExecutor.getPoolSize();
                //get the number of active threads
                int current_num_therad= Thread.activeCount();
                //System.out.println("current_num_therad: "+current_num_therad);



                // Step 5: Read and validate the server's response
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null)
                {
                    System.out.println(line); // Print server response for verification
                    if (line.contains("Sum: "))
                    {
                        break;
                    }
                }
                //close all the resources
                outputStream.close();
                reader.close();
            }

            //int check=Thread.activeCount();
            //System.out.println("check: "+check);
            // Step 7: Shut down the server
            server.close();


        } catch (Exception e) {
            e.printStackTrace();
        }



        // Give the server some time to shut down
        Thread.sleep(2000);
        //print the number of threads after the server is closed
        int final_num_thread=Thread.activeCount();
        //System.out.println("final_num_thread: "+final_num_thread);


    }


    public static void main(String[] args) {
        testParseRequest(); // 40 points
        try{
           testServer(); // 60
        }catch(Exception e){
            System.out.println("your server throwed an exception (-60)");
        }
        System.out.println("done");
        System.exit(0);
    }
}


