public class GammaFunction {
    private static final double[] COEFFICIENTS = {676.5203681218851, -1259.1392167224028, 771.3234287776531, -176.6150291621406, 12.507343278686905, -0.13857109526572012, 9.984369578019572E-6, 1.5056327351493116E-7};

    public double computeIntGamma(int value) {
        return computeDblGamma(value);
    }

    public double computeDblGamma(double value) {
        if (!Double.isFinite(value) || value <= 0.0) {
            throw new IllegalArgumentException("El argumento de gamma debe ser positivo y finito");
        }
        if (value < 0.5) {
            return Math.PI / (Math.sin(Math.PI * value) * computeDblGamma(1.0 - value));
        }
        double shifted = value - 1.0;
        double sum = 0.99999999999980993;
        for (int i = 0; i < COEFFICIENTS.length; i++) {
            sum += COEFFICIENTS[i] / (shifted + i + 1.0);
        }
        double base = shifted + 7.5;
        return Math.sqrt(2.0 * Math.PI) * Math.pow(base, shifted + 0.5) * Math.exp(-base) * sum;
    }
}
