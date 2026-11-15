import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler {
    private final History history;
    private final Validator validator;

    public RequestHandler(History history, Validator validator) {
        this.history = history;
        this.validator = validator;
    }

    public void handle(long start) throws IOException {
        Map<String, String> params = parseBody(readBody());

        Validator.ValidatedInput input;
        try {
            input = validator.validate(
                    params.get("x"),
                    params.get("y"),
                    params.get("r")
            );
        } catch (Validator.ValidationException e) {
            HttpResp.send(400, "Bad Request",
                    "{\"error\":\"" + e.getMessage() + "\"}");
            return;
        }

        boolean hit = AreaChecker.isHit(input.getX(), input.getY(), input.getR());
        long now = System.currentTimeMillis();
        long execNanos = System.nanoTime() - start;

        Point point = new Point(
                input.getX(), input.getY(), input.getR(), hit, now, execNanos
        );

        history.add(point);
        HttpResp.send(200, "OK", point.toJson());
    }

    private byte[] readBody() throws IOException {
        String cl = System.getProperty("CONTENT_LENGTH");
        int len = 0;

        try {
            if (cl != null) len = Integer.parseInt(cl.trim());
        } catch (NumberFormatException ignored) {
        }

        if (len < 0 || len > 4096) len = 0;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[512];
        int left = len;

        while (left > 0) {
            int n = System.in.read(buf, 0, Math.min(buf.length, left));
            if (n < 0) break;

            out.write(buf, 0, n);
            left -= n;
        }

        return out.toByteArray();
    }

    private Map<String, String> parseBody(byte[] body) throws IOException {
        Map<String, String> map = new HashMap<>();
        String s = new String(body, StandardCharsets.UTF_8);
        if (s.isEmpty()) return map;

        for (String pair : s.split("&")) {
            int i = pair.indexOf('=');
            String key = i < 0 ? pair : pair.substring(0, i);
            String value = i < 0 ? "" : pair.substring(i + 1);
            map.put(URLDecoder.decode(key, "UTF-8"),
                    URLDecoder.decode(value, "UTF-8"));
        }
        return map;
    }
}