import java.io.PrintStream;
import java.util.Locale;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        run(new Scanner(System.in).useLocale(Locale.ROOT), System.out, new Logic());
    }

    static void run(Scanner scanner, PrintStream out, Logic logic) {
        run(scanner, out, logic, "output/output5.txt");
    }

    static void run(Scanner scanner, PrintStream out, Logic logic, String outputFile) {
        out.println("Valor inicial:");
        double initial = readDouble(scanner);
        out.println("Valor final:");
        double end = readDouble(scanner);
        out.println("DOF:");
        int dof = readInt(scanner);
        double result = logic.logic1a(initial, end, dof);
        String formatted = logic.formatResult(result);
        out.println("Resultado: " + formatted);
        try {
            logic.writeResult(outputFile, result);
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("No se pudo guardar el resultado", exception);
        }
    }

    private static double readDouble(Scanner scanner) {
        if (!scanner.hasNextDouble()) throw new IllegalArgumentException("Se esperaba un valor numérico");
        double value = scanner.nextDouble();
        if (!Double.isFinite(value)) throw new IllegalArgumentException("El valor debe ser finito");
        return value;
    }

    private static int readInt(Scanner scanner) {
        if (!scanner.hasNextInt()) throw new IllegalArgumentException("DOF debe ser un entero");
        int value = scanner.nextInt();
        if (value <= 0) throw new IllegalArgumentException("DOF debe ser positivo");
        return value;
    }
}
