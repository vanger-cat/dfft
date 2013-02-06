package com.vangercat.dfft;

import com.vangercat.data.Quaternion;
import com.vangercat.data.DataMatrix;
import com.vangercat.data.helpers.CyclicListOfQuaternions;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class QuaternionFourierExecutor23 implements Serializable {
    public static DataMatrix<Quaternion> fourier(DataMatrix<Quaternion> from) throws Exception {
        DataMatrix<Quaternion> toMatrix = new DataMatrix<Quaternion>(from.getSize());
        fourier(from, toMatrix);
        return toMatrix;
    }

    public static void fourier(DataMatrix<Quaternion> from, DataMatrix<Quaternion> to) throws Exception {
         if (from.getSize() != to.getSize()) {
            throw new IllegalArgumentException("from.getSize() != to.getSize()");
        }

        int N = from.getSize();

        CyclicListOfQuaternions w1 = new CyclicListOfQuaternions(N, new Quaternion(0, 1, 0, 0));
        CyclicListOfQuaternions w2 = new CyclicListOfQuaternions(N, new Quaternion(0, 0, 1, 0));

        int halfN = N / 2;

        List<List<DataMatrix<Quaternion>>> Xab = new ArrayList<List<DataMatrix<Quaternion>>>();

        for (int a = 0; a < 2; a++) {
            List<DataMatrix<Quaternion>> row = new ArrayList<DataMatrix<Quaternion>>();
            Xab.add(row);
            for (int b = 0; b < 2; b++) {
                DataMatrix<Quaternion> currentXab = new DataMatrix<Quaternion>(halfN);
                row.add(currentXab);

                for (int m1 = 0; m1 < halfN; m1++) {
                    for (int m2 = 0; m2 < halfN; m2++) {
                        Quaternion value = Quaternion.zero;
                        for (int n1 = 0; n1 < halfN; n1++) {
                            for (int n2 = 0; n2 < halfN; n2++) {
                                Quaternion curValue = w1.get(2*m1*n1).
                                                mul(from.getValue(2*n1 + a, 2*n2 + b)).
                                                mul(w2.get(2*m2*n2))
                                        ;

                                value = value.sum(curValue);
                            }
                        }
                        currentXab.setValue(m1, m2, value);
                    }
                }
            }
        }

        for (int m1 = 0; m1 < halfN; m1++) {
            for (int m2 = 0; m2 < halfN; m2++) {
                Quaternion v00 = Xab.get(0).get(0).getValue(m1, m2);
                Quaternion v01 = w1.get(m1).mul(Xab.get(1).get(0).getValue(m1, m2));
                Quaternion v10 = (Xab.get(0).get(1).getValue(m1, m2)).mul(w2.get(m2));
                Quaternion v11 = w1.get(m1).mul(Xab.get(1).get(1).getValue(m1, m2)).mul(w2.get(m2));

                to.setValue(
                        m1,
                        m2,
                        v00.sum(v01).sum(v10).sum(v11)
                );

                to.setValue(
                        m1 + halfN,
                        m2,
                        v00.sum(v01.mul(-1)).sum(v10).sum(v11.mul(-1))
                );
                to.setValue(
                        m1,
                        m2 + halfN,
                        v00.sum(v01).sum(v10.mul(-1)).sum(v11.mul(-1))
                );
                to.setValue(
                        m1 + halfN,
                        m2 + halfN,
                        v00.sum(v01.mul(-1)).sum(v10.mul(-1)).sum(v11)
                );
            }
        }
    }
}