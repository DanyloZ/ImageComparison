package com.danylo.imagecomparison;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageComparator {
    private BufferedImage image1;
    private BufferedImage image2;
    private int maxDiff = (int)Math.sqrt((255* 255) *3)/10;
    private List<RectArea> rectAreas = new ArrayList<>();
    private RectArea excludeArea;

    public List<RectArea> getRectAreas() {
        return rectAreas;
    }

    public ImageComparator(BufferedImage image1, BufferedImage image2, RectArea excludeArea) {
        this.image1 = image1;
        this.image2 = image2;
        this.excludeArea = excludeArea;
    }

    public boolean compare() {
        boolean out = true;
        for (int i = 0; i < image1.getHeight(); i++) {
            for (int j = 0; j < image1.getWidth(); j++) {
                if(isMarked(j, i) || isExcluded(j, i)) {
                    continue;
                }
                if (!colorsAreEqual(j, i)) {
                    out = false;
                    markDifferArea(j, i);
                }
            }
        }
        return out;
    }

    private boolean colorsAreEqual(int x, int y) {
        Color color1 = new Color(image1.getRGB(x, y));
        Color color2 = new Color(image2.getRGB(x, y));
        if (color1.equals(color2)) {
            return true;
        } else {
            int redDif = Math.abs(color1.getRed() - color2.getRed());
            int greenDif = Math.abs(color1.getGreen() - color2.getGreen());
            int blueDif = Math.abs(color1.getBlue() - color2.getBlue());
            int diff = (int)Math.sqrt(Math.pow(redDif, 2) + Math.pow(greenDif, 2) + Math.pow(blueDif, 2));

            return diff < maxDiff;
        }
    }

    private boolean isMarked(int x, int y) {
        boolean out = false;
        for(RectArea area: rectAreas) {
            if (x >= area.getLeftBound() && x <= area.getRightBound()
                    &&  y >= area.getUpperBound() && y <= area.getLowerBound()) {
                out = true;
                break;
            }
        }
        return out;
    }

    private boolean isExcluded(int x, int y) {
        if(null != excludeArea) {
            if (x >= excludeArea.getLeftBound() && x <= excludeArea.getRightBound()
                    &&  y >= excludeArea.getUpperBound() && y <= excludeArea.getLowerBound()) {
                return true;
            }
        }
        return false;
    }

    private void markDifferArea(int startX, int startY) {
        RectArea area = new RectArea(startX, startY);
        int equalYCount = 0;
        rightBoundMove(area);
        while(equalYCount < 3 && area.getLowerBound() + 1 + equalYCount < image1.getHeight()) {
            boolean bottomMoved = bottomBoundMove(area, equalYCount);
            if(bottomMoved) {
                rightBoundMove(area);
                leftBoundMove(area);
            } else {
                equalYCount++;
            }

        }
        rectAreas.add(area);
    }


    private boolean bottomBoundMove(RectArea area, int equalYCount) {
        boolean out = false;
        int startX = area.getLeftBound() - 3 < 0 ? 0 :  area.getLeftBound() - 3;
        int endX = area.getRightBound() + 3 >= image1.getWidth() ? image1.getWidth()  - 1: area.getRightBound() + 3;
        int y = area.getLowerBound() + 1 + equalYCount;
        for(int i = startX; i <= endX; i++) {
            if(!colorsAreEqual(i, y)) {
                out = true;
                area.setLowerBound(y);
                break;
            }
        }
        return out;
    }
    private void rightBoundMove(RectArea area) {
        int y = area.getLowerBound();
        int equalXCount = 0;
        while (equalXCount < 3) {
            int currentX = area.getRightBound() + 1 + equalXCount;
            if (currentX > image1.getWidth() - 1) {
                break;
            }
            if (colorsAreEqual(currentX, y)) {
                equalXCount++;
            } else {
                area.setRightBound(currentX);
                equalXCount = 0;
            }
        }
    }

    private void leftBoundMove(RectArea area) {
        int y = area.getLowerBound();
        int equalXCount = 0;
        while (equalXCount < 3) {
            int currentX = area.getLeftBound() - 1 - equalXCount;
            if (currentX < 0) {
                break;
            }
            if (colorsAreEqual(currentX, y)) {
                equalXCount++;
            } else {
                area.setLeftBound(currentX);
                equalXCount = 0;
            }

        }
    }

}
