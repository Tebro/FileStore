package com.old_box.fileStore;

import com.old_box.StorageEngine.StorageEngine;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;

public class Main {

    public void run(String[] args) {

        Environment env = new Environment();

        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(Integer.parseInt(env.getValue("STORAGE_PORT"))), 0);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (NumberFormatException e){
            System.out.println("Environment variable 'STORAGE_PORT' is not set");
            System.exit(0);
        }
        server.createContext("/", new Handler(env));
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
                System.exit(0);
            }
        }

        public void handle(HttpExchange t) throws IOException {
            if(t.getRequestMethod().equals("POST")){
                byte[] bytes = IOUtils.toByteArray(t.getRequestBody());
                String key = t.getRequestURI().getPath();
                this.engine.store(key, bytes);
                t.sendResponseHeaders(200, key.length());
                OutputStream os = t.getResponseBody();
                os.write(key.getBytes());
                os.close();
            }else if(t.getRequestMethod().equals("GET")){
                String key = t.getRequestURI().getPath();
                byte[] data = engine.retrieve(key);

                if(data.length == 0 ){
                    String msg = "Nothing found with key";
                    t.sendResponseHeaders(404, msg.length());
                    OutputStream os = t.getResponseBody();
                    os.write(msg.getBytes());
                    os.close();
                }

                t.sendResponseHeaders(200, data.length);
                OutputStream os = t.getResponseBody();
                os.write(data);
                os.close();

            }else {
                String response = "Method not allowed";
                t.sendResponseHeaders(403, response.length());
                t.close();
            }
            Headers requestHeaders = t.getRequestHeaders();

        }
    }


    public static void main(String[] args){
        new Main().run(args);
    }
}
