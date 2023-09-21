package module_beyond;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;

import processing.core.PApplet;

public class MTAMap extends PApplet {
    private UnfoldingMap map;
    private List<Marker> subwayMarkers;
    private CommonMarker lastSelected;

    // Map 'station_complex_id' to total ridership for this day
    // (e.g. Jan 1, 2023)
    private HashMap<String, Integer> totalRidershipTable;

    public MTAMap(List<PointFeature> pointFeatures) {
        subwayMarkers = new ArrayList<>();
        totalRidershipTable = new HashMap<>();

        // Find all the total ridership counts, and add those as
        // properties later
        for (PointFeature pointFeature : pointFeatures) {
            int currentRidershipCount = Integer.parseInt(getStringProperty(pointFeature, "ridership"));
            String stationComplexID = getStringProperty(pointFeature, "station_complex_id");
            int ridershipCount = totalRidershipTable.getOrDefault(stationComplexID, 0);

            ridershipCount += currentRidershipCount;
            totalRidershipTable.put(stationComplexID, ridershipCount);
        }

        for (PointFeature pointFeature : pointFeatures) {
            // Add the total ridership as a property
            String stationComplexID = getStringProperty(pointFeature, "station_complex_id");
            int ridershipCount = totalRidershipTable.get(stationComplexID);
            pointFeature.putProperty("total_ridership", Integer.toString(ridershipCount));

            // Create and include the marker
            SubwayMarker subwayMarker = new SubwayMarker(pointFeature);
            subwayMarkers.add(subwayMarker);
        }
    }

    public void setup() {
        int mapWidth = 800;
        int mapHeight = 600;
        int offset = 25;

        size(mapWidth, mapHeight, OPENGL);

        Location newYorkCity = new Location(40.712778, -73.75);
        map = new UnfoldingMap(this, offset, offset, mapWidth - 2 * offset, mapHeight - 2 * offset);
        map.zoomAndPanTo(10, newYorkCity);
        MapUtils.createDefaultEventDispatcher(this, map);

        map.addMarkers(subwayMarkers);
    }

    public void draw() {
        background(165, 103, 41);
        map.draw();
    }

    /* Event handling */
    public void mouseMoved() {
        // clear the last selection
        if (lastSelected != null) {
            lastSelected.setSelected(false);
            lastSelected = null;
        }

        for (Marker marker : subwayMarkers) {
            CommonMarker commonMarker = (CommonMarker)marker;

            if (commonMarker.isInside(map, mouseX, mouseY)) {
                lastSelected = commonMarker;
                commonMarker.setSelected(true);
                return;
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Missing datafile command-line argument");
            return;
        }

        String dataFilename = args[0];
        List<PointFeature> pointFeatures = Parser.parse(dataFilename);

        MTAMap app = new MTAMap(pointFeatures);
        PApplet.runSketch(new String[]{"MTAMap"}, app);
    }
}
