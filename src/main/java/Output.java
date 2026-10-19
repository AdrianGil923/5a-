import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class Output {
    public String format(double value) {
        return String.format(Locale.ROOT, "%.5f", value);
    }

    public void writeData(String outFile, String outText) throws IOException {
        Path path = Path.of(outFile);
        Path parent = path.toAbsolutePath().getParent();
        Files.createDirectories(parent);
        Files.writeString(path, outText + System.lineSeparator());
    }
}
