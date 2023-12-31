package module_beyond;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import org.apache.commons.csv.CSVRecord;

public class StationComplexIDTable {
    public HashMap<String, HashMap<String, Object>> stationComplexIDTable;

    public StationComplexIDTable(String filename) {
        stationComplexIDTable = new HashMap<>();

        for (CSVRecord record : (new LocalCSVParser(filename)).getCSVParser()) {
            String stationComplexID = record.get("station_complex_id");
            HashMap<String, Object> dataTable = stationComplexIDTable.getOrDefault(stationComplexID, new HashMap<>());

            if (dataTable.size() == 0) {
                dataTable.put("station_complex", record.get("station_complex"));
                dataTable.put("borough", record.get("borough"));
                dataTable.put("routes", fixRoutes(record.get("routes")));
                dataTable.put("latitude", Double.parseDouble(record.get("latitude")));
                dataTable.put("longitude", Double.parseDouble(record.get("longitude")));

                // Include transfer data along with the ridership data
                int count = Integer.parseInt(record.get("ridership")) + Integer.parseInt(record.get("transfers"));

                dataTable.put("total_ridership", count);
            } else {
                checkDataTableSize(dataTable);
                int totalRidership = (Integer)dataTable.get("total_ridership");
                totalRidership += Integer.parseInt(record.get("ridership")) + Integer.parseInt(record.get("transfers"));
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

    // Let's test what kind of table we get by printing it out.
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
