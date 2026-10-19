public class SimpsonIntegration {
    private final GammaFunction gammaFunction;

    public SimpsonIntegration() {
        this(new GammaFunction());
    }

    public SimpsonIntegration(GammaFunction gammaFunction) {
        if (gammaFunction == null) throw new IllegalArgumentException("La función gamma es obligatoria");
        this.gammaFunction = gammaFunction;
    }

    public double integrate(double initial, double end, int dof) {
        if (!Double.isFinite(initial) || !Double.isFinite(end) || dof <= 0) {
            throw new IllegalArgumentException("Los límites deben ser finitos y DOF debe ser positivo");
        }
        int segments = 10;
        double previous = simpson(initial, end, dof, segments);
        while (true) {
            segments *= 2;
            double current = simpson(initial, end, dof, segments);
            if (Math.abs(current - previous) < 0.00001) return current;
            previous = current;
        }
    }

    private double simpson(double initial, double end, int dof, int segments) {
        double coefficient = gammaFunction.computeDblGamma((dof + 1.0) / 2.0)
                / (Math.sqrt(dof * Math.PI) * gammaFunction.computeDblGamma(dof / 2.0));
        double step = (end - initial) / segments;
        double sum = density(initial, dof, coefficient) + density(end, dof, coefficient);
        for (int i = 1; i < segments; i++) {
            sum += (i % 2 == 0 ? 2.0 : 4.0) * density(initial + i * step, dof, coefficient);
        }
        return step * sum / 3.0;
    }

    private double density(double x, int dof, double coefficient) {
        return coefficient * Math.pow(1.0 + x * x / dof, -(dof + 1.0) / 2.0);
    }
}
