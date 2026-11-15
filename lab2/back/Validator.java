public class Validator {

    public ValidatedInput validate(String xText, String yText, String rText)
            throws ValidationException {
        if (xText == null || yText == null || rText == null) {
            throw new ValidationException("Переданы не все данные");
        }

        xText = xText.trim();
        yText = yText.trim();
        rText = rText.trim();

        if (!xText.matches("-?\\d+")
                || !rText.matches("\\d+")
                || !yText.matches("-?\\d{1,10}(\\.\\d{1,15})?")) {
            throw new ValidationException("Некорректный формат данных");
        }

        int x;
        int r;
        double y;

        try {
            x = Integer.parseInt(xText);
            r = Integer.parseInt(rText);
            y = Double.parseDouble(yText);
        } catch (NumberFormatException e) {
            throw new ValidationException("Некорректный формат данных");
        }

        if (x < -4 || x > 4) {
            throw new ValidationException("X должен быть целым от -4 до 4");
        }

        if (!(y > -5 && y < 5)) {
            throw new ValidationException("Y должен быть от -5 до 5");
        }

        if (r < 1 || r > 5) {
            throw new ValidationException("R должен быть целым от 1 до 5 включительно");
        }

        return new ValidatedInput(x, y, r);
    }

    public static class ValidatedInput {
        private final int x;
        private final double y;
        private final int r;

        public ValidatedInput(int x, double y, int r) {
            this.x = x;
            this.y = y;
            this.r = r;
        }

        public int getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public int getR() {
            return r;
        }
    }

    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}
