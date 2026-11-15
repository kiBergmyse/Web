import java.util.ArrayList;
import java.util.List;

public class History {
    private final List<Point> points = new ArrayList<>();

    public synchronized void add(Point point) {
        points.add(point);}

    public synchronized List<Point> getAll() {
        return new ArrayList<>(points);}
}