import com.fastcgi.FCGIInterface;

public class Main {
    public static void main(String[] args) {
        FCGIInterface fcgi = new FCGIInterface();

        HttpResp httpResp = new HttpResp();
        History history = new History();
        Validator validator = new Validator();
        AreaChecker areaChecker = new AreaChecker();
        RequestHandler requestHandler = new RequestHandler(
                history, validator, areaChecker, httpResp
        );
        Router router = new Router(history, requestHandler, httpResp);

        while (fcgi.FCGIaccept() >= 0) {
            long start = System.nanoTime();
            try {
                router.handle(start);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    httpResp.send(500, "Internal Server Error",
                            new ErrorResp("ошибка сервера"));
                } catch (Exception ignored) {
                }
            }
        }
    }
}
