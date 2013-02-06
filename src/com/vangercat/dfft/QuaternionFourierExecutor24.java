package com.vangercat.dfft;

import com.vangercat.data.Quaternion;
import com.vangercat.data.DataMatrix;
import com.vangercat.data.helpers.CyclicListOfQuaternions;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class QuaternionFourierExecutor24 implements Serializable {
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

        for (int r = 0; r < 2; r++) {
            for (int s = 0; s < 2; s++) {
                for (int m1 = 0; m1 < halfN; m1++) {
                    for (int m2 = 0; m2 < halfN; m2++) {
                        Quaternion value = Quaternion.zero; 

                        for (int a = 0; a < 2; a++) {
                            for (int b = 0; b < 2; b++) {
                                Quaternion curValue = w1.get(a*m1).
                                        mul(Xab.get(a).get(b).getValue(m1, m2)).
                                        mul(w2.get(b*m2));
                                if ((a * r + b * s) % 2 != 0) {
                                    curValue = curValue.mul(-1);
                                }

                                value = value.sum(curValue);
                            }
                        }
                        
                        to.setValue(
                                m1 + r*halfN,
                                m2 + s*halfN,
                                value);
                    }
                }
            }
        }


    }
}