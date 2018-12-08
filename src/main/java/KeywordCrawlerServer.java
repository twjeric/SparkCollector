import collector.AbstractCollector;
import collector.KeywordCrawler;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class KeywordCrawlerServer {

    static AbstractCollector collector = null;
    static int second = 3;

    public static void main(String[] args) throws Exception {
        setupServer();
    }

    public static void setupServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8503), 0);
        HttpContext crawlerContext = server.createContext("/Crawler");  // context for crawler application
        crawlerContext.setHandler(exchange -> {
            try {
                crawlerHandle(exchange);
            }
            catch (InterruptedException ex) {
                System.err.println("InterruptedException!");
                throw new RuntimeException();
            }
        });
        server.start();
        System.out.println("Server starts");
    }

    public static void crawlerHandle(HttpExchange exchange)
            throws IOException, InterruptedException {
        String requestMethod = exchange.getRequestMethod();
        System.out.println(exchange.getRequestURI().getQuery());
        if (requestMethod.equalsIgnoreCase("GET")) {
            URI requestedUri = exchange.getRequestURI();
            Map<String, String> parameters = splitQuery(requestedUri);
            if (parameters.containsKey("start")) {
                int start = Integer.parseInt(parameters.get("start"));
                if (start == 1) {  // start a new collector
                    if (!parameters.containsKey("url")) {
                        errorHandle(exchange, "Needs initial URL!");
                        return;
                    }
                    String initalURL = parameters.get("url");
                    if (collector != null) {
                        errorHandle(exchange, "Crawler already started!");
                        return;
                    }
                    collector = new KeywordCrawler(initalURL, second);
                    new Thread(() -> {
                        try {
                            collector.run();
                        }
                        catch (InterruptedException ex) {
                            System.err.println("InterruptedException!");
                            throw new RuntimeException();
                        }
                    }).start();
                    String response = "Success!";
                    exchange.sendResponseHeaders(200, response.getBytes().length); //results code and length
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
                else if (start == 0) {  // stop the crawler
                    if (collector == null) {
                        errorHandle(exchange, "Crawler NOT started yet!");
                        return;
                    }
                    collector.stop();  // stop previous collector
                    String response = "Successfully stop crawler!";
                    exchange.sendResponseHeaders(200, response.getBytes().length); //results code and length
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                    Thread.sleep(1100);
                }
                else {  // request data from the running collect
                    if (collector == null) {
                        errorHandle(exchange, "Crawler NOT started yet!");
                        return;
                    }
                    String response = collector.results();
                    exchange.sendResponseHeaders(200, response.getBytes().length); //results code and length
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            }
            else
                errorHandle(exchange, "Needs start parameter!");
        }
    }

    public static void errorHandle(HttpExchange exchange, String msg) throws IOException {
        String response = "Invalid Request! " + msg;
        exchange.sendResponseHeaders(400, response.getBytes().length); //results code and length
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public static Map<String, String> splitQuery(URI uri) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = uri.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                    URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }
}
