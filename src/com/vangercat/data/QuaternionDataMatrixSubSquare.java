package com.vangercat.data;

/**
 * Created by IntelliJ IDEA.<p>
 * <p/>
 * Date: 06.11.2009<p>
 * Time: 0:12:39<p>
 */

// Implementation of submatrixes with fourier transfrom for quaternions. 
public class QuaternionDataMatrixSubSquare extends DataMatrixSubSquare<Quaternion> {
    public QuaternionDataMatrixSubSquare(DataMatrix<Quaternion> dm, int y, int x, int size) {
        super(dm, dm.getY() + y, dm.getX() + x, size);
    }
}
