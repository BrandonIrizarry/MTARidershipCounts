package module_beyond;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

// Specific to parsing
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.csv.*;

public class StationComplexIDTable {
    private HashMap<String, HashMap<String, Object>> stationComplexIDTable;

    public StationComplexIDTable(String filename) {
        CSVParser parser = null;

        try {
            File csvData = new File(filename);
            parser = CSVParser.parse(csvData, StandardCharsets.UTF_8, getCSVFormat());
        } catch (IOException e) {
            System.out.println("An error occurred when parsing the data file");
        }

        stationComplexIDTable = new HashMap<>();

        for (CSVRecord record : parser) {
            String stationComplexID = record.get("station_complex_id");
            HashMap<String, Object> dataTable = stationComplexIDTable.getOrDefault(stationComplexID, new HashMap<>());

            if (dataTable.size() == 0) {
                dataTable.put("station_complex", record.get("station_complex"));
                dataTable.put("borough", record.get("borough"));
                dataTable.put("routes", fixRoutes(record.get("routes")));
                dataTable.put("latitude", record.get("latitude"));
                dataTable.put("longitude", record.get("longitude"));
                dataTable.put("total_ridership", Integer.parseInt(record.get("ridership")));
            } else {
                checkDataTableSize(dataTable);
                int totalRidership = (Integer)dataTable.get("total_ridership");
                totalRidership += Integer.parseInt(record.get("ridership"));
                dataTable.put("total_ridership", totalRidership);
            }

            stationComplexIDTable.put(stationComplexID, dataTable);
        }
    }

    private void checkDataTableSize(HashMap<String, Object> dataTable) {
        int dataTableSize = dataTable.size();
        assert(dataTableSize == 6) : String.format("Wrong data table size: %d%n", dataTableSize);
    }

    private String[] fixRoutes(String routes) {
        String[] routesArray = routes.split(",,?");
        Arrays.sort(routesArray);

        return routesArray;
    }

    private CSVFormat getCSVFormat() {
        CSVFormat.Builder builder = CSVFormat.Builder.create();
        builder.setHeader();
        CSVFormat format = builder.build();

        return format;
    }

    public static void main(String[] args) {
        if (args.length != 1) { System.out.println("Missing command line arg"); System.exit(1); }

        StationComplexIDTable table = new StationComplexIDTable(args[0]);

        for (Map.Entry<String, HashMap<String, Object>> entry : table.stationComplexIDTable.entrySet()) {
            String fieldName = entry.getKey();
            System.out.println(fieldName);

            HashMap<String, Object> dataTable = entry.getValue();

            for (Map.Entry<String, Object> subEntry : dataTable.entrySet()) {
                String key = subEntry.getKey();
                Object value = subEntry.getValue();

                if (key.equals("routes")) {
                    value = Arrays.toString((String[])value);
                }

                System.out.printf("\t%s: %s%n", key, value);
            }
        }
    }
}
