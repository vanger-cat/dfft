package com.vangercat.data;

import java.util.List;

/**
 * Created by IntelliJ IDEA.<p>
 * <p/>
 * Date: 09.12.2009<p>
 * Time: 9:30:55<p>
 */
public class DataMatrixCyclic extends DataMatrix<Quaternion> {
    public DataMatrixCyclic(List<List<Quaternion>> data, int size) {
        super(data, size);
    }

    public DataMatrixCyclic(int size) {
        super(size);
    }

    @Override
    public Quaternion getValue(int y, int x) {
        if (y < 0) {
            y += this.getSize();
        }
        if (x < 0) {
            x += this.getSize();
        }
        return super.getValue(y, x);
    }
}
