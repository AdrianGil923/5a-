public class Logic {
    private final SimpsonIntegration integration;
    private final Output output;

    public Logic() {
        this(new SimpsonIntegration(), new Output());
    }

    public Logic(SimpsonIntegration integration, Output output) {
        if (integration == null || output == null) throw new IllegalArgumentException("Las dependencias son obligatorias");
        this.integration = integration;
        this.output = output;
    }

    public double logic1a(double initial, double end, int dof) {
        return integration.integrate(initial, end, dof);
    }

    public String formatResult(double result) {
        return output.format(result);
    }

    public void writeResult(String file, double result) throws java.io.IOException {
        output.writeData(file, formatResult(result));
    }
}
