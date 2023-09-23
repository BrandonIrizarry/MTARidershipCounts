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
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;

import processing.core.PApplet;

import org.apache.commons.csv.CSVRecord;

public class MTAMap extends PApplet {
    private UnfoldingMap map;
    private List<Marker> subwayMarkers;
    private List<Marker> paths;
    private List<Marker> allMarkers; // used for hiding and showing
    private CommonMarker lastSelected;
    private boolean allHidden = false;

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

        // Define the paths between stations
        paths = new ArrayList<>();

        for (List<SubwayMarker> stations : routeTable.values()) {
            // We can sort, since the stations' order on a given route
            // is _more or less_ determined by the order implied by
            // their numerical indices. It turns out this gets us most
            // of the way there!
            Collections.sort(stations);

            // Add a line marker between each consecutive pair of
            // stations
            for (int i = 0; i < stations.size() - 1; i++) {
                SubwayMarker first = stations.get(i);
                SubwayMarker second = stations.get(i + 1);

                // This is the one case where numerically
                // "consecutive" stations which share a line (in this
                // case, the 'A') aren't, in fact, consecutive along a
                // given route. So we "remove" this edge by skipping
                // its creation.
                if (first.getStringProperty("station_complex_id").equals("N187")
                    && second.getStringProperty("station_complex_id").equals("N191")) {
                    continue;
                }

                Location locationOfFirst = first.getLocation();
                Location locationOfSecond = second.getLocation();
                double distance = locationOfFirst.getDistance(locationOfSecond);

                // Based on code in a debug branch that prints out the
                // top 10 greatest distances between stations, there
                // are four outlier distances, the smallest of which
                // is approximately 14.48 km. It's reasonable to
                // assume these four are bogus, so rule them out.
                if (first.sharesARoute(second) && distance < 14.0) {
                    SimpleLinesMarker path = new SimpleLinesMarker(locationOfFirst, locationOfSecond);
                    paths.add(path);
                }
            }
        }

        // Add paths not found by our station-id heuristic
        for (CSVRecord record : (new LocalCSVParser("data/additional-paths.csv")).getCSVParser()) {
            String firstID = record.get("first");
            String secondID = record.get("second");

            Location firstLocation = getLocationFromID(table, firstID);
            Location secondLocation = getLocationFromID(table, secondID);

            paths.add(new SimpleLinesMarker(firstLocation, secondLocation));
        }

        // Collect all markers into a single ArrayList
        allMarkers = new ArrayList<>();
        allMarkers.addAll(subwayMarkers);
        allMarkers.addAll(paths);
    }

    private Location getLocationFromID(StationComplexIDTable table, String id) {
        Object latitude = table.stationComplexIDTable.get(id).get("latitude");
        Object longitude = table.stationComplexIDTable.get(id).get("longitude");

        return new Location((Double)latitude, (Double)longitude);
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
        map.addMarkers(paths);
    }

    public void draw() {
        background(165, 103, 41);
        map.draw();
        addKey();
    }

    private void addKey() {
        // Remember you can use Processing's graphics methods here
        fill(255, 250, 240);

        int xbase = 25;
        int ybase = 25;
        int lineHeight = 20;
        int leftMargin = 10;
        int topMargin = 10;

        rect(xbase, ybase, 75, 150);

        fill(0);
        textAlign(LEFT, CENTER);
        textSize(12);
        text("Key", 2 * xbase, ybase + topMargin);

        int blue = color(0, 0, 255);
        int green = color(0, 255, 0);
        int yellow = color(255, 255, 0);
        int purple = color(255, 0, 255);
        int red = color(255, 0, 0);
        int black = color(0, 0, 0);

        fill(blue);
        ellipse(xbase + leftMargin, ybase + 2 * lineHeight, 12, 12);
        fill(green);
        ellipse(xbase + leftMargin, ybase + 3 * lineHeight, 12, 12);
        fill(yellow);
        ellipse(xbase + leftMargin, ybase + 4 * lineHeight, 12, 12);
        fill(purple);
        ellipse(xbase + leftMargin, ybase + 5 * lineHeight, 12, 12);
        fill(red);
        ellipse(xbase + leftMargin, ybase + 6 * lineHeight, 12, 12);

        fill(black);
        textAlign(LEFT, CENTER);
        text("<250K", xbase + 20, ybase + 2 * lineHeight);
        text("250-500K", xbase + 20, ybase + 3 * lineHeight);
        text("500-750K", xbase + 20, ybase + 4 * lineHeight);
        text("750K-1M", xbase + 20, ybase + 5 * lineHeight);
        text("+1M", xbase + 20, ybase + 6 * lineHeight);
    }

    /* Event handling */
    public void mouseMoved() {
        // clear the last selection
        if (lastSelected != null) {
            lastSelected.setSelected(false);
            lastSelected = null;
        }

        CommonMarker foundMarker = overSubwayMarker();

        if (foundMarker != null) {
            for (Marker marker : allMarkers) {
                if (!marker.equals(foundMarker)) {
                    marker.setHidden(true);
                }
            }

            lastSelected = foundMarker;
            lastSelected.setSelected(true);
            allHidden = true;
        } else {
            for (Marker marker : allMarkers) {
                marker.setHidden(false);
            }

            allHidden = false;
        }
    }

    private CommonMarker overSubwayMarker() {
        for (Marker marker : subwayMarkers) {
            CommonMarker commonMarker = (CommonMarker)marker;

            if (commonMarker.isInside(map, mouseX, mouseY)) {
                return commonMarker;
            }
        }

        return null;
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
