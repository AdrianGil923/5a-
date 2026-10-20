public class GammaFunction {
    private static final double[] COEFFICIENTS = {
        676.5203681218851,
        -1259.1392167224028,
        771.3234287776531,
        -176.6150291621406,
        12.507343278686905,
        -0.13857109526572012,
        9.984369578019572E-6,
        1.5056327351493116E-7
    };
    private static final double INITIAL_SUM = 0.99999999999980993;
    private static final double LANCZOS_OFFSET = 7.5;
    private static final double HALF = 0.5;

    public double computeIntGamma(int value) {
        return computeDblGamma(value);
    }

    public double computeDblGamma(double value) {
        validateArgument(value);

        if (requiresReflection(value)) {
            return computeByReflection(value);
        }

        return computeLanczosApproximation(value);
    }

    private void validateArgument(double value) {
        boolean isFinite = Double.isFinite(value);
        boolean isPositive = value > 0.0;

        if (!isFinite || !isPositive) {
            throw new IllegalArgumentException("El argumento de gamma debe ser positivo y finito");
        }
    }

    private boolean requiresReflection(double value) {
        return value < HALF;
    }

    private double computeByReflection(double value) {
        double numerator = Math.PI;
        double sineTerm = Math.sin(Math.PI * value);
        double reflectedArgument = 1.0 - value;
        double reflectedGamma = computeDblGamma(reflectedArgument);

        return numerator / (sineTerm * reflectedGamma);
    }

    private double computeLanczosApproximation(double value) {
        double shifted = value - 1.0;
        double sum = calculateCoefficientSum(shifted);
        double base = calculateBase(shifted);
        double power = Math.pow(base, shifted + HALF);
        double exponential = Math.exp(-base);
        double scale = Math.sqrt(2.0 * Math.PI);

        return scale * power * exponential * sum;
    }

    private double calculateCoefficientSum(double shiftedValue) {
        double sum = INITIAL_SUM;

        for (int i = 0; i < COEFFICIENTS.length; i++) {
            double denominator = shiftedValue + i + 1.0;
            double term = COEFFICIENTS[i] / denominator;
            sum += term;
        }

        return sum;
    }

    private double calculateBase(double shiftedValue) {
        return shiftedValue + LANCZOS_OFFSET;
    }
}
//
