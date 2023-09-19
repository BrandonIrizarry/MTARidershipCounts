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
        String totalRidership = (String)getProperty("total_ridership");

        pg.pushStyle();

        pg.rectMode(PConstants.CORNER);

        int xOffset = 0;
        int yOffset = 15;
        int textMarginX = 3;
        int textMarginY = 3;
        int boxHeight = 50;
        int cornerRadius = 5;
        int horizontalPadding = 2 * textMarginX;
        int boxWidth = (int)Math.max(pg.textWidth(stationComplex), pg.textWidth(totalRidership)) + horizontalPadding;
        int lineHeight = 15;

        pg.stroke(110);
        pg.fill(255, 255, 0);
        pg.rect(x + xOffset, y + yOffset, boxWidth, boxHeight, cornerRadius);

        pg.textAlign(PConstants.LEFT, PConstants.TOP);
        pg.fill(0);
        pg.text(stationComplex, x + xOffset + textMarginX, y + yOffset + textMarginY);
        pg.text(totalRidership, x + xOffset + textMarginX, y + yOffset + textMarginY + lineHeight);

        pg.popStyle();
    }

}
