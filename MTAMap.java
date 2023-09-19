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

    public MTAMap(List<PointFeature> pointFeatures) {
        subwayMarkers = new ArrayList<>();

        for (PointFeature pointFeature : pointFeatures) {
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
        String dataFilename = args[0];
        List<PointFeature> pointFeatures = Parser.parse(dataFilename);

        MTAMap app = new MTAMap(pointFeatures);
        PApplet.runSketch(new String[]{"MTAMap"}, app);
    }
}
