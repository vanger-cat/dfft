package com.vangercat.dfft;

import com.vangercat.data.DataMatrix;
import com.vangercat.data.Quaternion;
import com.vangercat.data.QuaternionDataMatrixSubSquare;
import com.vangercat.data.helpers.BinaryInverseOrder;
import com.vangercat.data.helpers.CyclicListOfQuaternions;

import java.io.Serializable;
import java.util.*;

public class QuaternionFourierExecutorFinalInversedCyclic implements Serializable {
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

    private static final int[] idx4 = BinaryInverseOrder.generateFor(4);

    private static class TransformData {
        DataMatrix<Quaternion> from;
        DataMatrix<Quaternion> to;

        DataMatrix<Quaternion> X00;
        Map<Pair, DataMatrix<Quaternion>> Xab;

        private TransformData(DataMatrix<Quaternion> from, DataMatrix<Quaternion> to) {
            this.from = from;
            this.to = to;
        }
    }
    public static DataMatrix<Quaternion> fourier(DataMatrix<Quaternion> from) throws Exception {
        DataMatrix<Quaternion> toMatrix = new DataMatrix<Quaternion>(from.getSize());
        fourier(from, toMatrix);
        return toMatrix;
    }

    private static void fourier(DataMatrix<Quaternion> initialFrom, DataMatrix<Quaternion> to) throws Exception {
        if (initialFrom.getSize() != to.getSize()) {
            throw new IllegalArgumentException("from.getSize() != to.getSize()");
        }

        List<List<Quaternion>> newData;
        newData = new BinaryInverseOrder<List<Quaternion>>().makeBinaryInverseOrder(initialFrom.getData());

        BinaryInverseOrder<Quaternion> inverser = new BinaryInverseOrder<Quaternion>();
        for (int i = 0; i < newData.size(); i++) {
            newData.set(i, inverser.makeBinaryInverseOrder(newData.get(i)));
        }
        DataMatrix<Quaternion> inversedFrom = new DataMatrix<Quaternion>(newData, newData.size());

        int initialN = inversedFrom.getSize();

        new CyclicListOfQuaternions(initialN, Quaternion.i);
        new CyclicListOfQuaternions(initialN, Quaternion.j);

        List<TransformData> queue = new ArrayList<TransformData>();
        queue.add(new TransformData(inversedFrom, to));
        
        while (!queue.isEmpty())
        {
            int initialQueueSize = queue.size();
            TransformData transformData = queue.get(initialQueueSize - 1);
            DataMatrix<Quaternion> from = transformData.from;

            int N = from.getSize();
            int halfN = N / 2;
            int quaterN = N / 4;

            if (transformData.X00 == null) {
                Map<Pair, DataMatrix<Quaternion>> Xab;
                {
                    Xab = new HashMap<Pair, DataMatrix<Quaternion>>();

                    for (Pair pair: PAIRS) {
                        int y = idx4[pair.a] * quaterN;
                        int x = idx4[pair.b] * quaterN;
                        DataMatrix<Quaternion> currentXab = new DataMatrix<Quaternion>(quaterN);
                        QuaternionDataMatrixSubSquare xab = new QuaternionDataMatrixSubSquare(from, y, x, quaterN);

                        if (quaterN <= 4) {
                            QuaternionFourierExecutor22.fourier(xab, currentXab, true);
                        } else {
                            queue.add(new TransformData(xab, currentXab));
                        }

                        Xab.put(pair, currentXab);
                    }
                }
                transformData.Xab = Xab;

                DataMatrix<Quaternion> X00;
                {
                    X00 = new DataMatrix<Quaternion>(halfN);
                    QuaternionDataMatrixSubSquare x00
                                = new QuaternionDataMatrixSubSquare(from, 0, 0, halfN);
                    if (halfN <= 4) {
                        QuaternionFourierExecutor22.fourier(x00, X00, true);
                    } else {
                        queue.add(new TransformData(x00, X00));
                    }
                }

                transformData.X00 = X00;
            } else {
                queue.remove(queue.size() - 1);
                calculateX(
                        transformData.to,
                        new CyclicListOfQuaternions(N, Quaternion.i),
                        new CyclicListOfQuaternions(N, Quaternion.j),
                        quaterN,
                        transformData.Xab,
                        transformData.X00);
            }
        }
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
                                    Xab_cur.getValue(m1, m2).
                                            mulABOn(w1.get(pair.a*m1)).
                                            mulOnAC(w2.get(pair.b*m2)).
                                            mulPowOfIOn(pair.a*r).
                                            mulOnPowOfJ(pair.b*s)
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
}