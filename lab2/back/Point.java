public class Point {
    private final int x;
    private final double y;
    private final int r;
    private final boolean hit;
    private final long time;
    private final long execNanos;

    public Point(int x, double y, int r, boolean hit, long time, long execNanos) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.time = time;
        this.execNanos = execNanos;
    }
}