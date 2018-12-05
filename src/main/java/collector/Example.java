package collector;


import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Example {
    public static void main(String[] args) throws Exception {
        // extends AbstractCollector to use the project
        AbstractCollector crawler = new Crawler();
        buildServer(crawler);
        crawler.run();
    }

    public static void buildServer(AbstractCollector collector) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8500), 0);
        HttpContext context = server.createContext("/");
        context.setHandler(exchange -> handleRequest(exchange, collector));
        server.start();
    }

    public static void handleRequest(HttpExchange exchange, AbstractCollector collector)
            throws IOException {
        String response = collector.results();
        exchange.sendResponseHeaders(200, response.getBytes().length); //results code and length
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}