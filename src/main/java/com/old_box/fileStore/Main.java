package com.old_box.fileStore;

import com.old_box.StorageEngine.Backends.StorageEntry;
import com.old_box.StorageEngine.StorageEngine;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;


public class Main {


    /***
     * Entrypoint of application.
     * @param args
     */
    public void run(String[] args) {

        Environment env = new Environment();
        HttpServer server = null;

        // Which port should we use? Check environment or use default.
        String port = env.getValue("STORAGE_PORT");
        if(port == null) port = "10000";
        try {
            server = HttpServer.create(new InetSocketAddress(Integer.parseInt(port)), 0);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to start HTTP server");
            System.exit(0); // If the http server creation fails we are useless anyway.
        }
        server.createContext("/", new Handler(env)); // Set the handler for all requests.
        System.out.println("Server is running on port: " + port);
        server.start();
    }

    class Handler implements HttpHandler {

        Environment env;
        StorageEngine engine;

        public Handler(Environment env){
            this.env = env;
            try {
                this.engine = new StorageEngine(env);
            }catch (Exception e){
                System.out.println("Failed to access storage");
                System.exit(0); // Same as the previous exit, if this fail there is nothing for us to do.
            }
        }


        /***
         * Handler that handles every request that the webserver receives.
         *
         * @param t
         * @throws IOException
         */
        public void handle(HttpExchange t) throws IOException {
            // If the method is POST we take the data and store it.
            if(t.getRequestMethod().equals("POST")){
                // Get the content from the HTTP request
                byte[] bytes = IOUtils.toByteArray(t.getRequestBody());
                // Use the path of the request as key
                String key = t.getRequestURI().getPath();

                // Get the HTTP_CONTENT_TYPE header to store as metadata for the content.
                Headers headers = t.getRequestHeaders();
                this.engine.store(key, bytes, headers.getFirst("Content-Type"));

                // Set the HTTP_CONTENT_TYPE header for the response, basic text in this case
                Headers resHeaders = t.getResponseHeaders();
                resHeaders.add("Content-Type", "text/plain");
                t.sendResponseHeaders(200, key.length());
                // Write the message into the response and close the stream.
                OutputStream os = t.getResponseBody();
                os.write(key.getBytes());
                os.close();


            // If the method is get try to deliver stored data.
            }else if(t.getRequestMethod().equals("GET")){
                // Get the key from the request path.
                String key = t.getRequestURI().getPath();
                // Retrieve the data from the Storage backend.
                StorageEntry data = engine.retrieve(key);

                // If no data was found send a 404 reponse.
                if(data == null || data.getData().length == 0){
                    String msg = "Nothing found with key";
                    Headers resHeaders = t.getResponseHeaders();
                    resHeaders.add("Content-Type", "text/plain");
                    t.sendResponseHeaders(404, msg.length());
                    OutputStream os = t.getResponseBody();
                    os.write(msg.getBytes());
                    os.close();
                    return;
                }
                // Set the HTTP_CONTENT_TYPE header to the same header that was given when the data was POSTed.
                Headers headers = t.getResponseHeaders();
                headers.add("Content-Type", data.getContentType());
                t.sendResponseHeaders(200, data.getData().length);
                // Send response.
                OutputStream os = t.getResponseBody();
                os.write(data.getData());
                os.close();

            // Nothing but GET and POST allowed here.
            }else {
                String response = "Method not allowed";
                t.sendResponseHeaders(403, response.length());
                t.close();
            }
        }
    }


    public static void main(String[] args){
        new Main().run(args);
    }
}
