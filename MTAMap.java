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
        int offset = 50;

        size(mapWidth, mapHeight, OPENGL);

        map = new UnfoldingMap(this, offset, offset, mapWidth - offset, mapHeight - offset);
        MapUtils.createDefaultEventDispatcher(this, map);

        map.addMarkers(subwayMarkers);
    }

    public void draw() {
        background(200);
        map.draw();
    }

    public static void main(String[] args) {
        String dataFilename = args[0];
        List<PointFeature> pointFeatures = Parser.parse(dataFilename);

        MTAMap app = new MTAMap(pointFeatures);
        PApplet.runSketch(new String[]{"MTAMap"}, app);
    }
}
