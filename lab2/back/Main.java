import com.fastcgi.FCGIInterface;

public class Main {
    public static void main(String[] args) {
        FCGIInterface fcgi = new FCGIInterface();
        while (fcgi.FCGIaccept() >= 0) {
            long start = System.nanoTime();
            try {
                RequestHandler.handle(start);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    HttpResp.send(500, "Internal Server Error",
                            "{\"error\":\"ошибка сервера\"}");
                } catch (Exception ignored) {
                }
            }
        }
    }
}