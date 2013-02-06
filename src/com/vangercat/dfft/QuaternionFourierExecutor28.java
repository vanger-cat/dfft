package com.vangercat.dfft;

import com.vangercat.data.DataMatrix;
import com.vangercat.data.Quaternion;
import com.vangercat.data.helpers.CyclicListOfQuaternions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuaternionFourierExecutor28 implements Serializable {
    private static class Pair {
        public int a;
        public int b;

        public Pair(int a, int b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Pair)) return false;

            Pair pair = (Pair) o;

            if (a != pair.a) return false;
            if (b != pair.b) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = a;
            result = 31 * result + b;
            return result;
        }
    }

    private static final Set<Pair> PAIRS;
    static {
        PAIRS = new HashSet<Pair>();
        for (int a = 0; a < 4; a++) {
            for (int b = 0; b < 4; b++) {
                if ((b % 2 == 1) || (a % 2 == 1)) {
                    PAIRS.add(new Pair(a, b));
                }
            }
        }
    }
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
        int quaterN = N / 4;

        List<List<DataMatrix<Quaternion>>> Xab = generateXab(from, w1, w2, quaterN);

        DataMatrix<Quaternion> X00 = calculateX00(from, w1, w2, halfN);

        //TODO: continue.....
        calculateX(to, w1, w2, quaterN, Xab, X00);
    }

    private static void calculateX(DataMatrix<Quaternion> to, CyclicListOfQuaternions w1, CyclicListOfQuaternions w2, int quaterN, List<List<DataMatrix<Quaternion>>> xab, DataMatrix<Quaternion> x00) {
        for (int r = 0; r < 4; r++) {
            for (int s = 0; s < 4; s++) {
                for (int m1 = 0; m1 < quaterN; m1++) {
                    for (int m2 = 0; m2 < quaterN; m2++) {
                        Quaternion value = Quaternion.zero;

                        value = value.sum(x00.getValue(m1 + (r % 2) * quaterN, m2 + (s % 2) * quaterN));

                        for (Pair pair: PAIRS) {
                            Quaternion pairValue = Quaternion.zero;

                            DataMatrix<Quaternion> Xab_cur = xab.get(pair.a).get(pair.b);

                            pairValue = w1.get(pair.a*m1).
                                    mul(Xab_cur.getValue(m1, m2)).
                                    mul(w2.get(pair.b*m2))
                            ;

                            if (((pair.a * r) % 4) > 1) {
                                pairValue = pairValue.mul(-1);
                            }

                            if (((pair.a * r) % 2) == 1) {
                                pairValue = pairValue.mulIOn();
                            }

                            if (((pair.b * s) % 4) > 1) {
                                pairValue = pairValue.mul(-1);
                            }

                            if (((pair.b * s) % 2) == 1) {
                                pairValue = pairValue.mulOnJ();
                            }

                            value = value.sum(pairValue);
                        }

                        to.setValue(
                                m1 + r * quaterN,
                                m2 + s * quaterN,
                                value
                        );
                    }
                }
            }
        }
    }

    private static DataMatrix<Quaternion> calculateX00(DataMatrix<Quaternion> from, CyclicListOfQuaternions w1, CyclicListOfQuaternions w2, int halfN) throws Exception {
        DataMatrix<Quaternion> X00 = new DataMatrix<Quaternion>(halfN);
//        if (halfN <= 4) {
//            QuaternionDataMatrixSubSquare x00
//                    = new QuaternionDataMatrixSubSquare(from, 0, 0, halfN);
//            QuaternionFourierExecutor22.fourier(x00, X00);
//        }
        
        for (int m1 = 0; m1 < halfN; m1++) {
            for (int m2 = 0; m2 < halfN; m2++) {
                Quaternion value = Quaternion.zero;
                for (int n1 = 0; n1 < halfN; n1++) {
                    for (int n2 = 0; n2 < halfN; n2++) {
                        Quaternion curValue = w1.get(2*m1*n1).
                                mul(from.getValue(2*n1, 2*n2)).
                                mul(w2.get(2*m2*n2));

                        value = value.sum(curValue);
                    }
                }
                X00.setValue(
                        m1,
                        m2,
                        value
                );
            }
        }
        return X00;
    }

    private static List<List<DataMatrix<Quaternion>>> generateXab(DataMatrix<Quaternion> from, CyclicListOfQuaternions w1, CyclicListOfQuaternions w2, int quaterN) {
        List<List<DataMatrix<Quaternion>>> Xab = new ArrayList<List<DataMatrix<Quaternion>>>();

        for (int a = 0; a < 4; a++) {
            List<DataMatrix<Quaternion>> row = new ArrayList<DataMatrix<Quaternion>>();
            Xab.add(row);
            for (int b = 0; b < 4; b++) {
                DataMatrix<Quaternion> currentXab = new DataMatrix<Quaternion>(quaterN);
                row.add(currentXab);

                for (int m1 = 0; m1 < quaterN; m1++) {
                    for (int m2 = 0; m2 < quaterN; m2++) {
                        Quaternion value = Quaternion.zero;
                        for (int n1 = 0; n1 < quaterN; n1++) {
                            for (int n2 = 0; n2 < quaterN; n2++) {
                                Quaternion curValue = w1.get(4*m1*n1).
                                                mul(
                                                        from.getValue(
                                                                4*n1 + a,
                                                                4*n2 + b)
                                                ).
                                                mul(w2.get(4*m2*n2))
                                        ;

                                value = value.sum(curValue);
                            }
                        }
                        currentXab.setValue(m1, m2, value);
                    }
                }
            }
        }
        return Xab;
    }
}