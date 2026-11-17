import java.io.IOException;

public class Router {
    private final History history;
    private final RequestHandler requestHandler;
    private final HttpResp httpResp;

    public Router(History history, RequestHandler requestHandler, HttpResp httpResp) {
        this.history = history;
        this.requestHandler = requestHandler;
        this.httpResp = httpResp;
    }

    public void handle(long start) throws IOException {
        String method = System.getProperty("REQUEST_METHOD", "");
        String requestUri = System.getProperty("REQUEST_URI");
        if (requestUri == null || requestUri.isEmpty()) {
            requestUri = System.getProperty("SCRIPT_NAME", "");
        }
        String path = requestUri;

        int queryIndex = path.indexOf('?');
        if (queryIndex >= 0) {
            path = path.substring(0, queryIndex);
        }

        String route = method + " " + path;

        switch (route) {
            case "POST /check":
                requestHandler.handle(start);
                return;
            case "GET /points":
                httpResp.send(200, "OK", history.getAll());
                return;
            default:
                if ("/check".equals(path) || "/points".equals(path)) {
                    httpResp.send(405, "Method Not Allowed",
                            new ErrorResp("Метод не поддерживается для этого пути"));
                } else {
                    httpResp.send(404, "Not Found", new ErrorResp("Маршрут не найден"));
                }
        }
    }
}