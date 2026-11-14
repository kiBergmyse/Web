import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler {

    public static void handle(long start) throws IOException {
        String method = System.getProperty("REQUEST_METHOD");
        if (!"POST".equals(method)) {
            HttpResp.send(405, "Method Not Allowed",
                    "{\"error\":\"Метод не POST\"}");
            return;
        }

        Map<String, String> params = parseBody(readBody());
        String xs = params.get("x");
        String ys = params.get("y");
        String rs = params.get("r");

        if (xs == null || ys == null || rs == null) {
            HttpResp.send(400, "Bad Request",
                    "{\"error\":\"Переданы не все данные\"}");
            return;}

        xs = xs.trim();
        ys = ys.trim();
        rs = rs.trim();
        int x, r;
        double y;

        try {
            if (!xs.matches("-?\\d+") ||
                !rs.matches("\\d+") ||
                !ys.matches("-?\\d{1,10}(\\.\\d{1,15})?")) {
                throw new NumberFormatException();}

            x = Integer.parseInt(xs);
            r = Integer.parseInt(rs);
            y = Double.parseDouble(ys);

        } catch (NumberFormatException e) {
            HttpResp.send(400, "Bad Request",
                    "{\"error\":\"Некорректный формат данных\"}");
            return;}

        if (x < -4 || x > 4) {
            HttpResp.send(400, "Bad Request",
                    "{\"error\":\"X должен быть целым от -4 до 4\"}");
            return;}

        if (!(y > -5 && y < 5)) {
            HttpResp.send(400, "Bad Request",
                    "{\"error\":\"Y должен быть от -5 до 5\"}");
            return;}

        if (r < 1 || r > 5) {
            HttpResp.send(400, "Bad Request",
                    "{\"error\":\"R должен быть целым от 1 до 5 включительно\"}");
            return;}

        boolean hit = AreaChecker.isHit(x, y, r);

        long now = System.currentTimeMillis();
        long execNanos = System.nanoTime() - start;

        String json = 
                "{\"x\":" + x 
                + ",\"y\":" + y 
                + ",\"r\":" + r
                + ",\"hit\":" + hit
                + ",\"time\":" + now
                + ",\"execNanos\":" + execNanos + "}";

        HttpResp.send(200, "OK", json);
    }

    private static byte[] readBody() throws IOException {
        String cl = System.getProperty("CONTENT_LENGTH");
        int len = 0;

        try {
            if (cl != null) len = Integer.parseInt(cl.trim());
        } catch (NumberFormatException ignored) {}

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

    private static Map<String, String> parseBody(byte[] body) throws IOException {
        Map<String, String> map = new HashMap<>();
        String s = new String(body, StandardCharsets.UTF_8);
        if (s.isEmpty()) return map;
        for (String pair : s.split("&")) {
            int i = pair.indexOf('=');
            String k = i < 0 ? pair : pair.substring(0, i);
            String v = i < 0 ? "" : pair.substring(i + 1);
            map.put(URLDecoder.decode(k, "UTF-8"),
                    URLDecoder.decode(v, "UTF-8"));
        }
        return map;
    }
}
