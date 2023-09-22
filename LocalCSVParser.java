package module_beyond;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.csv.*;

/**
 * A class meant to hide the minor (but somewhat cumbersome)
 * complexities of using CSVParser.
 */
public class LocalCSVParser {
    private CSVParser parser;

    public LocalCSVParser(String filename) {
        try {
            parser = CSVParser.parse(new File(filename), StandardCharsets.UTF_8, getCSVFormat());
        } catch (IOException e) {
            System.out.println("An error occurred when parsing the data file");
            System.exit(1);
        }
    }

    private CSVFormat getCSVFormat() {
        CSVFormat.Builder builder = CSVFormat.Builder.create();
        builder.setHeader();
        CSVFormat format = builder.build();

        return format;
    }

    public CSVParser getCSVParser() {
        return parser;
    }
}
