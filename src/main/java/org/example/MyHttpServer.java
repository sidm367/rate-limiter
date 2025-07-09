package org.example;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class MyHttpServer {
    public static void main(String[] args) throws IOException {
        RateLimiter rateLimiter = new FixedWindowRateLimiter(10);

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new MyHandler(rateLimiter));
        server.setExecutor(null); // sets a default executor
        server.start();
        System.out.println("Server started on port 8000");
    }

    static class MyHandler implements HttpHandler {
        private final RateLimiter rateLimiter;

        public MyHandler(RateLimiter rateLimiter){
            this.rateLimiter = rateLimiter;
        }
        @Override
        public void handle(HttpExchange exchange) throws IOException {

            if(!rateLimiter.tryAcquire()){
                String response ="sent too many requests";
                exchange.sendResponseHeaders(429, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                return; // Exit without processing

            }

            String response = "This is the response";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
