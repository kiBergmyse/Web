import java.io.IOException;

public class Router {
    public static void handle(long start, History history) throws IOException {
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
                RequestHandler.handlePost(start, history);
                return;
            case "GET /points":
                RequestHandler.handleGet(history);
                return;
            default:
                if ("/check".equals(path) || "/points".equals(path)) {
                    HttpResp.send(405, "Method Not Allowed",
                            "{\"error\":\"Метод не поддерживается для этого пути\"}");
                } else {
                    HttpResp.send(404, "Not Found",
                            "{\"error\":\"Маршрут не найден\"}");
                }
        }
    }
}