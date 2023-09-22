package module_beyond;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.utils.MapUtils;

import processing.core.PApplet;

public class MTAMap extends PApplet {
    private UnfoldingMap map;
    private List<Marker> subwayMarkers;
    private CommonMarker lastSelected;

    public MTAMap(StationComplexIDTable table) {
        subwayMarkers = new ArrayList<>();

        for (Map.Entry<String, HashMap<String, Object>> stationEntry : table.stationComplexIDTable.entrySet()) {
            HashMap<String, Object> subTable = stationEntry.getValue();
            Location stationLocation = new Location((double)subTable.get("latitude"), (double)subTable.get("longitude"));

            PointFeature point = new PointFeature(stationLocation);
            point.setProperties(subTable);
            point.putProperty("station_complex_id", stationEntry.getKey());
            subwayMarkers.add(new SubwayMarker(point));
        }

        // Use station complex id prefixes to identify routes; map
        // prefixes to markers belonging in that route.
        HashMap<Character, List<SubwayMarker>> routeTable = new HashMap<>();

        for (Marker subwayMarker : subwayMarkers) {
            char prefix = ((SubwayMarker) subwayMarker).getCharPrefix();
            List<SubwayMarker> stations = routeTable.getOrDefault(prefix, new ArrayList<>());
            stations.add((SubwayMarker)subwayMarker);
            routeTable.put(prefix, stations);
        }

        // Sort each list of markers (by numerical index)
        for (List<SubwayMarker> stations : routeTable.values()) {
            Collections.sort(stations);
            System.out.println(stations);
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
            System.exit(1);
        }

        String dataFilename = args[0];
        StationComplexIDTable table = new StationComplexIDTable(dataFilename);

        MTAMap app = new MTAMap(table);
        PApplet.runSketch(new String[]{"MTAMap"}, app);
    }
}
