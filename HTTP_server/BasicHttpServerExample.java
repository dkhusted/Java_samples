import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Headers;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class BasicHttpServerExample {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(4444), 0);
        HttpContext context = server.createContext("/");
        context.setHandler(BasicHttpServerExample::handleRequest);
        server.start();
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        switch (requestMethod) {
            case "GET":
                handleGet(exchange);
                break;
            case "POST":
                handlePost(exchange);
                break;
            default:
                handleDefault(exchange);
        }
    }

    private static void handleGet(HttpExchange exchange) throws IOException {
        String response = "This is the response";
        exchange.sendResponseHeaders(200, response.getBytes().length);//response code and length
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static void handlePost(HttpExchange exchange) throws IOException {
        Headers headers = exchange.getRequestHeaders();
        String contentType = headers.getFirst("Content-Type");
        if (contentType.equals("application/x-www-form-urlencoded")) {
            handlePostForm(exchange);
        } else if (contentType.equals("application/json")) {
            handlePostJson(exchange);
        } else {
            handleDefault(exchange);
        }
    }

    private static void handlePostForm(HttpExchange exchange) throws IOException {
        //Get HTTP content
        String content = new String(exchange.getRequestBody().readAllBytes());
        String response = "Hi there " + content;
        System.out.println(response);
        exchange.sendResponseHeaders(200, response.getBytes().length);//response code and length
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static void handlePostJson(HttpExchange exchange) throws IOException {
        //handle json data
        String jSOString = new String(exchange.getRequestBody().readAllBytes());
        System.out.println(jSOString);
        exchange.sendResponseHeaders(200, jSOString.getBytes().length);//response code and length
        OutputStream os = exchange.getResponseBody();
        os.write(jSOString.getBytes());
        os.close();
    }

    private static void handleDefault(HttpExchange exchange) throws IOException {
        String response = "Unsupported http method";
        exchange.sendResponseHeaders(405, response.getBytes().length);//response code and length
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
