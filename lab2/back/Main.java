import com.fastcgi.FCGIInterface;

public class Main {
    public static void main(String[] args) {
        FCGIInterface fcgi = new FCGIInterface();
        History history = new History();
        Validator validator = new Validator();
        RequestHandler requestHandler = new RequestHandler(history, validator);

        while (fcgi.FCGIaccept() >= 0) {
            long start = System.nanoTime();
            try {
                Router.handle(start, history, requestHandler);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    HttpResp.send(500, "Internal Server Error", new ErrorResp("ошибка сервера"));
                } catch (Exception ignored) {}
            }
        }
    }
}