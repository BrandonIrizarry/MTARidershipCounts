package module_beyond;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import processing.core.PApplet;

public class MTAMap extends PApplet {
    public MTAMap(List<PointFeature> pointFeatures) {

    }

    public void setup() {

    }

    public void draw() {

    }

    public static void main(String[] args) {
        List<PointFeature> pointFeatures = Parser.parse("data/jan-2023.csv");

        MTAMap app = new MTAMap(pointFeatures);
        PApplet.runSketch(new String[]{"MTAMap"}, app);
    }
}
