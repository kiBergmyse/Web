public class AreaChecker {
    public static boolean isHit(double x, double y, double r) {
        double h = r / 2;
        if (x <= 0 && x >= -h && y >= 0 && y <= h) {
            return true;}
        if (x <= 0 && y <= 0 && x * x + y * y <= h * h) {
            return true;}
        if (x >= 0 && y <= 0 && x - y <= h) {
            return true;}
        return false;
    }
}