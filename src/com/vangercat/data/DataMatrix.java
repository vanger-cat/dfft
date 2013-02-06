package com.vangercat.data;

import com.vangercat.data.helpers.Coordinate;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.<p>
 * <p/>
 * Date: 04.11.2009<p>
 * Time: 22:34:55<p>
 */
// class of basic matrix of full size
public class DataMatrix<T> implements Serializable {
    transient protected Coordinate coord;
    private int size;
    private List<List<T>> data;

    public DataMatrix(List<List<T>> data, int size) {
        coord = new Coordinate(0, 0);
        this.data = data;
        this.size = size;
    }

    public DataMatrix(int size) {
        coord = new Coordinate(0, 0);
        this.size = size;
        data = new ArrayList<List<T>>();
        for (int i = 0; i < size; i++) {
            ArrayList<T> list = new ArrayList<T>();
            data.add(list);
            for (int j = 0; j < size; j++) {
                list.add(null);
            }
        }
    }

    public int getX() {
        return coord.getX();
    }

    public int getY() {
        return coord.getY();
    }

    public Coordinate getCoord() {
        return coord;
    }

    public List<List<T>> getData() {
        return data;
    }

    public T getValue(int y, int x) {
        return data.get(y).get(x);
    }

    public void setValue(int y, int x, T value) {
        data.get(y).set(x, value);
    }

    protected List<List<T>> getFullData() {
        return data;
    }

    public int getSize() {
        return size;
    }

    private static boolean isPowerOf2(int N) {
        while (N > 1) {
            if (N % 2 == 0) {
                N = N / 2;
            } else {
                return false;
            }
        }

        return true;
    }

    public static void isSizeValid(int N) throws Exception {
        if (!(N >= 2 && isPowerOf2(N))) {
            throw new Exception("N should be bigger or equal to 2 and should be power of 2");
        }
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append(size).append("\n");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                res.append(getData().get(i).get(j)).append("\t");
            }
//            if (i != size - 1)
            res.append("\n");
        }

        return res.toString();
    }

    public void write(Writer writer) throws IOException {
        writer.write(size + "\n");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                writer.append(String.valueOf(getValue(i, j))).append("\t");
            }
            writer.append("\n");
        }
        writer.flush();/**/
    }

    public void setData(List<List<T>> data) {
        this.data = data;
    }
}
