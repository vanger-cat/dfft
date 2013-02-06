package com.vangercat.data.helpers;

/**
 * Created by IntelliJ IDEA.<p>
 * <p/>
 * Date: 05.11.2009<p>
 * Time: 0:01:09<p>
 */
public class Coordinate {
    private int x;
    private int y;

    public Coordinate(int y, int x) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinate)) return false;

        Coordinate that = (Coordinate) o;

        return x == that.x && y == that.y;
    }

    //TODO: maybe don't use standart hash code and use simple comparator? 
    @Override
    public int hashCode() {
        int result = x;
        result = 10000 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return y + ", " + x;
    }
}
