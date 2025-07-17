package org.example;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class MyHttpServer {
    public static void main(String[] args) throws IOException {
        System.out.println("Argument received: '" + args[0] + "'");
        if(args.length == 0) {
            System.out.println("Select the rate limiting algorithm to enforce");
            return;
        }
        String algorithm = args[0].toLowerCase();

        RateLimiter rateLimiter;
        switch(algorithm) {
            case "fixedwindow":
                rateLimiter = new FixedWindowRateLimiter(10);
                break;
            case "slidingwindow":
                rateLimiter = new SlidingWindowRateLimiter(20);
                break;
            default:
                System.out.println("rate limiting algorithcm not supported");
                rateLimiter = null;
                break;
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new MyHandler(rateLimiter));
        server.setExecutor(null); // sets a default executor
        server.start();
        System.out.println("Server started on port 8000");
    }

    static class MyHandler implements HttpHandler {
        private final RateLimiter rateLimiter;
        //private final RateLimiter rateLimiter2;

        public MyHandler(RateLimiter rateLimiter){
            this.rateLimiter = rateLimiter;
            //this.rateLimiter2 = rateLimiter2;
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
