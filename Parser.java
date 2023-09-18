package module_beyond;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;

// Specific to the parser
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.csv.*;

public class Parser {
    public static List<PointFeature> parse(String filename) {
        List<PointFeature> pointFeatures = new ArrayList<PointFeature>();

        File csvData = new File(filename);
        CSVParser parser;

        try {
            parser = CSVParser.parse(csvData, StandardCharsets.UTF_8, CSVFormat.EXCEL.withHeader());
        } catch (IOException e) {
            System.out.println("nope");
            return null;
        }

        for (CSVRecord record : parser) {
            String transitTimestamp = record.get("transit_timestamp");

            double latitude = Double.parseDouble(record.get("latitude"));
            double longitude = Double.parseDouble(record.get("longitude"));
            Location location = new Location(latitude, longitude);
            PointFeature pointFeature = new PointFeature(location);

            HashMap<String, Object> propertiesTable = buildPropertiesTableFromCSVRecord(record);
            pointFeature.setProperties(propertiesTable);

            pointFeatures.add(pointFeature);

            // System.out.println(record);
        }

        return pointFeatures;
    }

    private static HashMap<String, Object> buildPropertiesTableFromCSVRecord(CSVRecord record) {
        String[] headers = new String[]{"transit_timestamp","station_complex_id","station_complex","borough","routes","payment_method","ridership","transfers","latitude","longitude","Georeference"};

        HashMap<String, Object> propertiesTable = new HashMap<>();

        for (String header : headers) {
            propertiesTable.put(header, record.get(header));
        }

        return propertiesTable;
    }
}
