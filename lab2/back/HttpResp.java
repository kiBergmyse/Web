import com.google.gson.Gson;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpResp {
    private final Gson gson = new Gson();

    public void send(int status, String reason, Object bodyObject)
            throws IOException {
        byte[] body = gson.toJson(bodyObject).getBytes(StandardCharsets.UTF_8);
        String head = "HTTP/1.1 " + status + " " + reason + "\r\n"
                + "Content-Type: application/json; charset=UTF-8\r\n"
                + "Content-Length: " + body.length + "\r\n"
                + "Cache-Control: no-store\r\n"
                + "Connection: close\r\n\r\n";
        byte[] h = head.getBytes(StandardCharsets.US_ASCII);
        System.out.write(h, 0, h.length);
        System.out.write(body, 0, body.length);
        System.out.flush();
    }
}
