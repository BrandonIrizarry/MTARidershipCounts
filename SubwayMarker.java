package module_beyond;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PGraphics;
import processing.core.PConstants;

public class SubwayMarker extends CommonMarker {
    public SubwayMarker(PointFeature pointFeature) {
        super(pointFeature.getLocation(), pointFeature.getProperties());
    }

    @Override
    public void drawMarker(PGraphics pg, float x, float y) {
        pg.fill(255, 255, 0);
        pg.ellipse(x, y, 5, 5);
    }

    @Override
    public void showTitle(PGraphics pg, float x, float y) {
        String stationComplex = (String)getProperty("station_complex");

        pg.pushStyle();

        pg.rectMode(PConstants.CORNER);

        pg.stroke(110);
        pg.fill(255, 255, 0);
        pg.rect(x, y + 15, pg.textWidth(stationComplex) +6, 18, 5);

        pg.textAlign(PConstants.LEFT, PConstants.TOP);
        pg.fill(0);
        pg.text(stationComplex, x + 3 , y +18);

        pg.popStyle();
    }

}
