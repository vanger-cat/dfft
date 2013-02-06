package com.vangercat.data;

import com.vangercat.data.helpers.Coordinate;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.<p>
 * <p/>
 * Date: 04.11.2009<p>
 * Time: 23:29:54<p>
 */

// class of submatrixes. Instances of this class if like small frames to the DataMatrix 
public class DataMatrixSubSquare<T> extends DataMatrix<T> {
    public DataMatrixSubSquare(DataMatrix<T> dm, int y, int x, int size) {
        super(dm.getFullData(), size);
        this.coord = new Coordinate(y, x);
    }

    @Override
    public List<List<T>> getData() {
        List<List<T>> arr = new ArrayList<List<T>>();

        for (int i = 0; i < getSize(); i++) {
            arr.add(new ArrayList<T>());
            arr.get(i).addAll(
                    getFullData().get(coord.getY() + i).subList(
                            coord.getX(),
                            coord.getX() + this.getSize()
                    )
            );
        }
        return arr;
    }

    @Override
    public T getValue(int y, int x) {
        return getFullData().get(this.getY() + y).get(this.getX() + x);
    }

    @Override
    public void setValue(int y, int x, T value) {
        getFullData().get(this.getY() + y).set(this.getX() + x, value);
    }

    @Override
    public String toString() {
        return coord.toString() + ":\n" + super.toString();
    }
}
