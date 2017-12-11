package com.danylo.imagecomparison;

public class RectArea {
    private int upperBound;
    private int lowerBound;
    private int leftBound;
    private int rightBound;

    public RectArea(int x, int y) {
        upperBound = y;
        lowerBound = y;
        leftBound = x;
        rightBound = x;
    }

    public RectArea(int top, int right, int bottom, int left) {
        upperBound = top;
        lowerBound = bottom;
        leftBound = left;
        rightBound = right;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(int upperBound) {
        this.upperBound = upperBound;
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(int lowerBound) {
        this.lowerBound = lowerBound;
    }

    public int getLeftBound() {
        return leftBound;
    }

    public void setLeftBound(int leftBound) {
        this.leftBound = leftBound;
    }

    public int getRightBound() {
        return rightBound;
    }

    public void setRightBound(int rightBound) {
        this.rightBound = rightBound;
    }

    @Override
    public String toString() {
        return "RectArea{" +
                "upperBound=" + upperBound +
                ", lowerBound=" + lowerBound +
                ", leftBound=" + leftBound +
                ", rightBound=" + rightBound +
                '}';
    }
}
