package module_beyond;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PGraphics;

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

    }

}
