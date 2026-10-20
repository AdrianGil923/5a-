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
    void pdfExampleDof9() {
        assertEquals(0.36006, new SimpsonIntegration().integrate(0, 1.1, 9), 0.0000051);
    }

    @Test
    void pdfExampleDof10() {
        assertEquals(0.36757, new SimpsonIntegration().integrate(0, 1.1812, 10), 0.0000051);
    }

    @Test
    void pdfExampleDof30() {
        assertEquals(0.49500, new SimpsonIntegration().integrate(0, 2.750, 30), 0.0000051);
    }

    @Test
    void gammaIntegerOne() {
        assertEquals(1.0, new GammaFunction().computeIntGamma(1), 1.0e-12);
    }

    @Test
    void gammaIntegerFive() {
        assertEquals(24.0, new GammaFunction().computeIntGamma(5), 1.0e-10);
    }

    @Test
    void gammaHalf() {
        assertEquals(Math.sqrt(Math.PI), new GammaFunction().computeDblGamma(0.5), 1.0e-12);
    }

    @Test
    void gammaReflectionForQuarters() {
        GammaFunction gamma = new GammaFunction();
        assertEquals(Math.PI / Math.sin(Math.PI * 0.25), gamma.computeDblGamma(0.25) * gamma.computeDblGamma(0.75), 1.0e-12);
    }

    @Test
    void gammaOne() {
        assertEquals(1.0, new GammaFunction().computeDblGamma(1.0), 1.0e-12);
    }

    @Test
    void gammaRejectsIntegerZero() {
        assertThrows(IllegalArgumentException.class, () -> new GammaFunction().computeIntGamma(0));
    }

    @Test
    void gammaRejectsNegativeValue() {
        assertThrows(IllegalArgumentException.class, () -> new GammaFunction().computeDblGamma(-1));
    }

    @Test
    void gammaRejectsNan() {
        assertThrows(IllegalArgumentException.class, () -> new GammaFunction().computeDblGamma(Double.NaN));
    }

    @Test
    void integrationSupportsNonzeroInitialValue() {
        assertEquals(0.35006 - 0.146, new SimpsonIntegration().integrate(0.4, 1.1, 9), 0.01);
    }

    @Test
    void integrationSupportsReversedLimits() {
        SimpsonIntegration integration = new SimpsonIntegration();
        assertEquals(-integration.integrate(0.3, 1.2, 12), integration.integrate(1.2, 0.3, 12), 1.0e-12);
    }

    @Test
    void integrationSupportsEqualLimits() {
        assertEquals(0.0, new SimpsonIntegration().integrate(2, 2, 7), 0.0);
    }

    @Test
    void integrationRefinesForWideInterval() {
        assertEquals(Math.atan(3.0) / Math.PI, new SimpsonIntegration().integrate(0, 3.0, 1), 0.00001);
    }

    @Test
    void integrationRejectsNanInitialValue() {
        assertThrows(IllegalArgumentException.class, () -> new SimpsonIntegration().integrate(Double.NaN, 1, 3));
    }

    @Test
    void integrationRejectsInfiniteEndValue() {
        assertThrows(IllegalArgumentException.class, () -> new SimpsonIntegration().integrate(0, Double.POSITIVE_INFINITY, 3));
    }

    @Test
    void integrationRejectsZeroDof() {
        assertThrows(IllegalArgumentException.class, () -> new SimpsonIntegration().integrate(0, 1, 0));
    }

    @Test
    void simpsonIntegrationRejectsNullGamma() {
        assertThrows(IllegalArgumentException.class, () -> new SimpsonIntegration(null));
    }

    @Test
    void logicRejectsNullIntegration() {
        assertThrows(IllegalArgumentException.class, () -> new Logic(null, new Output()));
    }

    @Test
    void logicRejectsNullOutput() {
        assertThrows(IllegalArgumentException.class, () -> new Logic(new SimpsonIntegration(), null));
    }

    @Test
    void outputFormatsFiveDecimals() {
        assertEquals("0.35006", new Output().format(0.350056));
    }

    @Test
    void outputWritesNumericResultAndCreatesDirectories() throws Exception {
        Path file = temp.resolve("nested/output5.txt");
        new Output().writeData(file.toString(), new Output().format(0.350056));
        assertEquals("0.35006" + System.lineSeparator(), Files.readString(file));
    }

    @Test
    void appPrintsThreePromptsAndResult() throws Exception {
        new App();
        Path file = temp.resolve("prompts/output5.txt");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        App.run(new Scanner("0\n1.1\n9\n").useLocale(Locale.ROOT), new PrintStream(bytes), new Logic(), file.toString());
        assertEquals("Valor inicial:" + System.lineSeparator() + "Valor final:" + System.lineSeparator() + "DOF:" + System.lineSeparator() + "Resultado: 0.35006" + System.lineSeparator(), bytes.toString());
    }

    @Test
    void appWritesNumericResult() throws Exception {
        Path file = temp.resolve("result/output5.txt");
        App.run(new Scanner("0 1.1 9").useLocale(Locale.ROOT), new PrintStream(new ByteArrayOutputStream()), new Logic(), file.toString());
        assertEquals("0.35006" + System.lineSeparator(), Files.readString(file));
    }

    @Test
    void mainReadsStandardInputWithDotDecimalLocale() {
        var originalIn = System.in;
        var originalOut = System.out;
        var originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.GERMANY);
            System.setIn(new java.io.ByteArrayInputStream("0 1.1 9".getBytes()));
            System.setOut(new PrintStream(new ByteArrayOutputStream()));
            assertDoesNotThrow(() -> App.main(new String[0]));
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    void appRejectsNonNumericInitialValue() {
        assertThrows(IllegalArgumentException.class, () -> App.run(new Scanner("x"), new PrintStream(new ByteArrayOutputStream()), new Logic()));
    }

    @Test
    void appRejectsNonFiniteInitialValue() {
        assertThrows(IllegalArgumentException.class, () -> App.run(new Scanner("NaN 1 9"), new PrintStream(new ByteArrayOutputStream()), new Logic()));
    }

    @Test
    void appRejectsNonIntegerDof() {
        assertThrows(IllegalArgumentException.class, () -> App.run(new Scanner("0 1 x"), new PrintStream(new ByteArrayOutputStream()), new Logic()));
    }

    @Test
    void appRejectsNonPositiveDof() {
        assertThrows(IllegalArgumentException.class, () -> App.run(new Scanner("0 1 0"), new PrintStream(new ByteArrayOutputStream()), new Logic()));
    }

    @Test
    void appReportsOutputWriteFailure() throws Exception {
        Path blocker = temp.resolve("blocker");
        Files.writeString(blocker, "file");
        assertThrows(IllegalStateException.class, () -> App.run(new Scanner("0 1 9"), new PrintStream(new ByteArrayOutputStream()), new Logic(), blocker.resolve("out.txt").toString()));
    }
}
