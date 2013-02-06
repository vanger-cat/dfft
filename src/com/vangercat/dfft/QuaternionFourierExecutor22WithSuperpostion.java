package com.vangercat.dfft;

import com.vangercat.data.DataMatrix;
import com.vangercat.data.DataMatrixCyclic;
import com.vangercat.data.Quaternion;
import com.vangercat.data.helpers.BinaryInverseOrder;
import com.vangercat.data.helpers.CyclicListOfQuaternions;
import com.vangercat.data.helpers.SuperpositionHelper;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

public class QuaternionFourierExecutor22WithSuperpostion implements Serializable {
    public static DataMatrix<Quaternion> fourier(DataMatrix<Quaternion> from) throws Exception {
        DataMatrix<Quaternion> toMatrix = new DataMatrix<Quaternion>(from.getSize());
        fourier(from, toMatrix, false);
        return toMatrix;
    }

    public static void fourier(DataMatrix<Quaternion> from, DataMatrix<Quaternion> to, Boolean isOmegaInversed) throws Exception {
        if (from.getSize() != to.getSize()) {
            throw new IllegalArgumentException("from.getSize() != to.getSize()");
        }

        DataMatrix<Quaternion> dm_in_new = SuperpositionHelper.executeSuperposition(from);

        List<List<Quaternion>> newData;
        newData = new BinaryInverseOrder<List<Quaternion>>().makeBinaryInverseOrder(dm_in_new.getData());

        BinaryInverseOrder<Quaternion> inverser = new BinaryInverseOrder<Quaternion>();
        for (int i = 0; i < newData.size(); i++) {
            newData.set(i, inverser.makeBinaryInverseOrder(newData.get(i)));
        }
        DataMatrix<Quaternion> inversedFrom = new DataMatrix<Quaternion>(newData, newData.size());

        int initialN = inversedFrom.getSize();

        CyclicListOfQuaternions w1 = new CyclicListOfQuaternions(initialN, Quaternion.i);
        CyclicListOfQuaternions w2 = new CyclicListOfQuaternions(initialN, Quaternion.j);

        DataMatrix<Quaternion> tmpRes = calculate(dm_in_new, isOmegaInversed, w1, w2);
        DataMatrixCyclic Q = new DataMatrixCyclic(tmpRes.getData(), tmpRes.getSize());

        List<List<DataMatrix<Quaternion>>> Sab = SuperpositionHelper.Sab(Q);

        /*System.out.println("Q = " + Q);
        System.out.println("S00 = " + Sab.get(0).get(0));
        System.out.println("S01 = " + Sab.get(0).get(1));
        System.out.println("S10 = " + Sab.get(1).get(0));
        System.out.println("S11 = " + Sab.get(1).get(1));*/

//        SuperpositionHelper.Decompose(to, Q, Sab);
    }

    private static DataMatrix<Quaternion> calculate(DataMatrix<Quaternion> from, Boolean isOmegaInversed, CyclicListOfQuaternions w1, CyclicListOfQuaternions w2) {
        DataMatrix<Quaternion> to = new DataMatrix<Quaternion>(from.getSize());
        int N = from.getSize();

        int[] idx = new int[N];
        if (isOmegaInversed) {
            idx = BinaryInverseOrder.generateFor(N);
        } else {
            for (int i = 0; i < N; i++) {
                idx[i] = i;
            }
        }

        long fullIdx = 0;
        Boolean isOutputRequired = N > 4;
        long initialTime = 0;

        if (isOutputRequired) {
            System.out.println("Calculating dfft for NxN:");
            initialTime = Calendar.getInstance().getTimeInMillis();
        }

        long Npow4 = Math.round(Math.pow(N, 4));
        String s = " of " + Npow4 + ". Left ";
        for (int m1 = 0; m1 < N; m1++) {
            for (int m2 = 0; m2 < N; m2++) {
                Quaternion value = Quaternion.zero;
                for (int n1 = 0; n1 < N; n1++) {
                    for (int n2 = 0; n2 < N; n2++) {
//                        value = value.sum(w1.get(m1*idx[n1]).mul(from.getValue(n1, n2)).mul(w2.get(m2*idx[n2])));
                        value = value.sum((from.getValue(n1, n2)).mulABOn(w1.get(m1*idx[n1])).mulOnAC(w2.get(m2*idx[n2])));
                        if (isOutputRequired && fullIdx % 10000000 == 0 && fullIdx != 0) {
                            System.out.println("Calculated " +
                                            fullIdx +
                                            s +
                                            Math.round((Calendar.getInstance().getTimeInMillis() - initialTime) / (double) fullIdx * (Npow4 - fullIdx)) +
                                            " seconds"
                            );
                        }
                        fullIdx++;
                    }
                }
                to.setValue(m1, m2, value);
            }
        }
        return to;
    }
}