package module_beyond;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PGraphics;
import processing.core.PConstants;

public class SubwayMarker extends CommonMarker implements Comparable<SubwayMarker> {
    public SubwayMarker(PointFeature pointFeature) {
        super(pointFeature.getLocation(), pointFeature.getProperties());
    }

    public char getCharPrefix() {
        return getStringProperty("station_complex_id").charAt(0);
    }

    public int getNumericalIndex() {
        String stationComplexID = getStringProperty("station_complex_id");

        // Station complex ID ends in a letter
        if (stationComplexID.matches("^[A-Z]\\d+[A-Z]$")) {
            return Integer.parseInt(stationComplexID.substring(1, stationComplexID.length() - 1));
        }

        // Station complex ID ends in digits only
        return Integer.parseInt(stationComplexID.substring(1));
    }

    // In practice, we should only be comparing subway markers whose
    // IDs have the same letter prefix, e.g. 'N' (to be clear, these
    // prefixes have nothing to do with the letter names of the routes
    // at those stations.)
    public int compareTo(SubwayMarker subwayMarker) {
        return Integer.compare(getNumericalIndex(), subwayMarker.getNumericalIndex());
    }

    public String toString() {
        return getStringProperty("station_complex_id");
    }

    public boolean sharesARoute(SubwayMarker subwayMarker) {
        String[] routes_1 = (String[])getProperty("routes");
        String[] routes_2 = (String[])subwayMarker.getProperty("routes");

        for (String route_1 : routes_1) {
            for (String route_2 : routes_2) {
                if (route_1.equals(route_2)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void drawMarker(PGraphics pg, float x, float y) {
        int blue = pg.color(0, 0, 255);
        int green = pg.color(0, 255, 0);
        int yellow = pg.color(255, 255, 0);
        int purple = pg.color(255, 0, 255);
        int red = pg.color(255, 0, 0);
        int black = pg.color(0, 0, 0);

        int totalRidership = (Integer)getProperty("total_ridership");
        int color = black;

        if (totalRidership < 250000) {
            color = blue;
        } else if (totalRidership >= 250000 && totalRidership < 500000) {
            color = green;
        } else if (totalRidership >= 500000 && totalRidership < 750000) {
            color = yellow;
        } else if (totalRidership >= 750000 && totalRidership < 1000000) {
            color = purple;
        } else if (totalRidership >= 1000000) {
            color = red;
        } else {
            assert(false) : "Unreachable code";
        }

        pg.fill(color);
        pg.ellipse(x, y, 5, 5);
    }

    @Override
    public void showTitle(PGraphics pg, float x, float y) {
        String stationComplex = getStringProperty("station_complex");
        String totalRidership = "";

        {
            int _totalRidership = (Integer)getProperty("total_ridership");
            totalRidership = String.format("Total ridership: %d", _totalRidership);
        }

        String stationComplexID = String.format("Station complex ID: %s", getStringProperty("station_complex_id"));

        pg.pushStyle();

        pg.rectMode(PConstants.CORNER);

        int xOffset = 0;
        int yOffset = 15;
        int textMarginX = 3;
        int textMarginY = 3;
        int cornerRadius = 5;
        int horizontalPadding = 2 * textMarginX;
        int boxWidth = (int)Math.max(pg.textWidth(stationComplexID),
                                     Math.max(pg.textWidth(totalRidership),
                                              pg.textWidth(stationComplex))) + horizontalPadding;
        int lineHeight = 15;

        // Use a reasonable hard-coded constant, lest we end up with a
        // somewhat complicated expression for this.
        int boxHeight = 4 * lineHeight;

        pg.stroke(110);
        pg.fill(255, 255, 0);
        pg.rect(x + xOffset, y + yOffset, boxWidth, boxHeight, cornerRadius);

        pg.textAlign(PConstants.LEFT, PConstants.TOP);
        pg.fill(0);
        pg.text(stationComplex, x + xOffset + textMarginX, y + yOffset + textMarginY);
        pg.text(totalRidership, x + xOffset + textMarginX, y + yOffset + textMarginY + lineHeight);
        pg.text(stationComplexID, x + xOffset + textMarginX, y + yOffset + textMarginY + 2 * lineHeight);

        pg.popStyle();
    }

}
