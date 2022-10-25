import java.io.IOException;
import java.nio.file.Path;

public class FileDifference {
    public static void main(String[] args) {
        try {
            Utils.fileDiffs(Path.of("lab1/resources/output/filteredMatrix-M10000-N10-m5-n5-th0"),
                    Path.of("lab1/resources/output/filteredMatrix-M10000-N10-m5-n5-th16"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
