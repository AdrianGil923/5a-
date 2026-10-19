import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Scanner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class A5Test {
    @TempDir Path temp;

    @Test
    void pdfExamples() {
        SimpsonIntegration integration = new SimpsonIntegration();
        assertEquals(0.35006, integration.integrate(0, 1.1, 9), 0.0000051);
        assertEquals(0.36757, integration.integrate(0, 1.1812, 10), 0.0000051);
        assertEquals(0.49500, integration.integrate(0, 2.750, 30), 0.0000051);
    }

    @Test
    void gammaForIntegersAndDecimals() {
        GammaFunction gamma = new GammaFunction();
        assertEquals(1.0, gamma.computeIntGamma(1), 1.0e-12);
        assertEquals(24.0, gamma.computeIntGamma(5), 1.0e-10);
        assertEquals(Math.sqrt(Math.PI), gamma.computeDblGamma(0.5), 1.0e-12);
        assertEquals(Math.PI / Math.sin(Math.PI * 0.25), gamma.computeDblGamma(0.25) * gamma.computeDblGamma(0.75), 1.0e-12);
        assertEquals(1.0, gamma.computeDblGamma(1.0), 1.0e-12);
        assertEquals(2.0, gamma.computeDblGamma(0.5) / gamma.computeDblGamma(1.0), 0.9);
    }

    @Test
    void gammaRejectsInvalidValues() {
        GammaFunction gamma = new GammaFunction();
        assertThrows(IllegalArgumentException.class, () -> gamma.computeIntGamma(0));
        assertThrows(IllegalArgumentException.class, () -> gamma.computeDblGamma(-1));
        assertThrows(IllegalArgumentException.class, () -> gamma.computeDblGamma(Double.NaN));
    }

    @Test
    void integrationSupportsNonzeroAndReversedLimits() {
        SimpsonIntegration integration = new SimpsonIntegration();
        assertEquals(0.35006 - 0.146, integration.integrate(0.4, 1.1, 9), 0.01);
        assertEquals(-integration.integrate(0.3, 1.2, 12), integration.integrate(1.2, 0.3, 12), 1.0e-12);
        assertEquals(0.0, integration.integrate(2, 2, 7), 0.0);
        assertEquals(Math.atan(3.0) / Math.PI, integration.integrate(0, 3.0, 1), 0.00001);
    }

    @Test
    void integrationRejectsInvalidInputsAndDependencies() {
        SimpsonIntegration integration = new SimpsonIntegration();
        assertThrows(IllegalArgumentException.class, () -> integration.integrate(Double.NaN, 1, 3));
        assertThrows(IllegalArgumentException.class, () -> integration.integrate(0, Double.POSITIVE_INFINITY, 3));
        assertThrows(IllegalArgumentException.class, () -> integration.integrate(0, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> new SimpsonIntegration(null));
        assertThrows(IllegalArgumentException.class, () -> new Logic(null, new Output()));
        assertThrows(IllegalArgumentException.class, () -> new Logic(new SimpsonIntegration(), null));
    }

    @Test
    void outputHasOnlyFiveDecimalPlacesAndCreatesDirectories() throws Exception {
        Output output = new Output();
        assertEquals("0.35006", output.format(0.350056));
        Path file = temp.resolve("nested/output5.txt");
        output.writeData(file.toString(), output.format(0.350056));
        assertEquals("0.35006" + System.lineSeparator(), Files.readString(file));
    }

    @Test
    void appReadsThreeInputsPrintsAndWritesResult() throws Exception {
        Path file = temp.resolve("app/output5.txt");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        App.run(new Scanner("0\n1.1\n9\n").useLocale(Locale.ROOT), new PrintStream(bytes), new Logic(), file.toString());
        String printed = bytes.toString();
        assertTrue(printed.contains("Valor inicial:"));
        assertTrue(printed.contains("Valor final:"));
        assertTrue(printed.contains("DOF:"));
        assertTrue(printed.contains("0.35006"));
        assertEquals("0.35006" + System.lineSeparator(), Files.readString(file));
    }

    @Test
    void mainReadsFromStandardInput() {
        var originalIn = System.in;
        var originalOut = System.out;
        var originalLocale = Locale.getDefault();
        try {
            new App();
            Locale.setDefault(Locale.GERMANY);
            System.setIn(new java.io.ByteArrayInputStream("0 1.1 9".getBytes()));
            System.setOut(new PrintStream(new ByteArrayOutputStream()));
            App.main(new String[0]);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    void appRejectsInvalidInputsAndHandlesWriteFailure() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> App.run(new Scanner("x"), new PrintStream(new ByteArrayOutputStream()), new Logic()));
        assertThrows(IllegalArgumentException.class, () -> App.run(new Scanner("NaN 1 9"), new PrintStream(new ByteArrayOutputStream()), new Logic()));
        assertThrows(IllegalArgumentException.class, () -> App.run(new Scanner("0 1 x"), new PrintStream(new ByteArrayOutputStream()), new Logic()));
        assertThrows(IllegalArgumentException.class, () -> App.run(new Scanner("0 1 0"), new PrintStream(new ByteArrayOutputStream()), new Logic()));
        Path blocker = temp.resolve("blocker");
        Files.writeString(blocker, "file");
        assertThrows(IllegalStateException.class, () -> App.run(new Scanner("0 1 9"), new PrintStream(new ByteArrayOutputStream()), new Logic(), blocker.resolve("out.txt").toString()));
    }
}
