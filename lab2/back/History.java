import java.util.ArrayList;
import java.util.List;

public class History {
    private final List<Point> points = new ArrayList<>();

    public synchronized void add(Point point) {
        points.add(point);
    }

    public synchronized List<Point> getAll() {
        return new ArrayList<>(points);
    }

    public String toJson() {
        List<Point> copy = getAll();
        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < copy.size(); i++) {
            if (i > 0) json.append(',');
            json.append(copy.get(i).toJson());
        }

        return json.append(']').toString();
    }
}
