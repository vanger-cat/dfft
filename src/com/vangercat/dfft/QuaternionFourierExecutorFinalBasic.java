package com.vangercat.dfft;

import com.vangercat.data.DataMatrix;
import com.vangercat.data.Quaternion;
import com.vangercat.data.helpers.CyclicListOfQuaternions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class QuaternionFourierExecutorFinalBasic implements Serializable {
    public static class Pair {
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

            return a == pair.a && b == pair.b;
        }

        @Override
        public int hashCode() {
            int result = a;
            result = 31 * result + b;
            return result;
        }

        @Override
        public String toString() {
            return "(" + a + ", " + b + ")";
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

        Map<Pair, DataMatrix<Quaternion>> Xab = generateXab(from, w1, w2, quaterN);
//        System.out.println(Xab);

        DataMatrix<Quaternion> X00 = calculateX00(from, w1, w2, halfN);
//        System.out.println(X00);

        calculateX(to, w1, w2, quaterN, Xab, X00);
    }

    private static void calculateX(DataMatrix<Quaternion> to, CyclicListOfQuaternions w1, CyclicListOfQuaternions w2, int quaterN, Map<Pair, DataMatrix<Quaternion>> xab, DataMatrix<Quaternion> x00) {
        for (int r = 0; r < 4; r++) {
            for (int s = 0; s < 4; s++) {
                for (int m1 = 0; m1 < quaterN; m1++) {
                    for (int m2 = 0; m2 < quaterN; m2++) {
                        Quaternion value;
                        value = x00.getValue(m1 + (r % 2) * quaterN, m2 + (s % 2) * quaterN);

                        for (Pair pair: PAIRS) {
                            Quaternion pairValue;

                            DataMatrix<Quaternion> Xab_cur = xab.get(pair);

                            pairValue =
                                    Quaternion.iPow(pair.a*r).
                                    mul(w1.get(pair.a*m1)).
                                    mul(Xab_cur.getValue(m1, m2)).
                                    mul(w2.get(pair.b*m2)).
                                    mul(Quaternion.jPow(pair.b*s))
                            ;

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

        for (int m1 = 0; m1 < halfN; m1++) {
            for (int m2 = 0; m2 < halfN; m2++) {
                Quaternion value = Quaternion.zero;

                for (int n1 = 0; n1 < halfN; n1++) {
                    for (int n2 = 0; n2 < halfN; n2++) {
                        value =
                                value.sum(
                                    w1.get(2*m1*n1).
                                    mul(from.getValue(2*n1, 2*n2)).
                                    mul(w2.get(2*m2*n2))
                                );
                    }
                }

                X00.setValue(m1, m2, value);
            }
        }
//        System.out.print(X00.toString());

        return X00;
    }

    private static Map<Pair, DataMatrix<Quaternion>> generateXab(DataMatrix<Quaternion> from, CyclicListOfQuaternions w1, CyclicListOfQuaternions w2, int quaterN) throws Exception {
        Map<Pair, DataMatrix<Quaternion>> Xab = new HashMap<Pair, DataMatrix<Quaternion>>();

        for (Pair pair: PAIRS) {
            DataMatrix<Quaternion> currentXab = new DataMatrix<Quaternion>(quaterN);


            for (int m1 = 0; m1 < quaterN; m1++) {
                for (int m2 = 0; m2 < quaterN; m2++) {
                    Quaternion value = Quaternion.zero;

                    for (int n1 = 0; n1 < quaterN; n1++) {
                        for (int n2 = 0; n2 < quaterN; n2++) {
                            value =
                                    value.sum(
                                        w1.get(4*m1*n1).
                                        mul(from.getValue(4*n1 + pair.a, 4*n2 + pair.b)).
                                        mul(w2.get(4*m2*n2))
                                    );
                        }
                    }
                    currentXab.setValue(m1, m2, value);
                }
            }

            Xab.put(pair, currentXab);
        }
        return Xab;
    }
}