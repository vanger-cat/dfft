package com.vangercat.dfft;

import com.vangercat.data.DataMatrix;
import com.vangercat.data.DataMatrixCyclic;
import com.vangercat.data.Quaternion;
import com.vangercat.data.helpers.SuperpositionHelper;

import java.io.Serializable;
import java.util.List;
//import java.util.Set;

public class QuaternionFourierExecutorFinalInversedCyclicWithSuperposition implements Serializable {
//    public static class Pair {
//        public int a;
//        public int b;
//
//        public Pair(int a, int b) {
//            this.a = a;
//            this.b = b;
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (!(o instanceof Pair)) return false;
//
//            Pair pair = (Pair) o;
//
//            return a == pair.a && b == pair.b;
//        }
//
//        @Override
//        public int hashCode() {
//            int result = a;
//            result = 31 * result + b;
//            return result;
//        }
//
//        @Override
//        public String toString() {
//            return "(" + a + ", " + b + ")";
//        }
//    }

//    private static final Set<Pair> PAIRS;
//    static {
//        PAIRS = new HashSet<Pair>();
//        for (int a = 0; a < 4; a++) {
//            for (int b = 0; b < 4; b++) {
//                if ((b % 2 == 1) || (a % 2 == 1)) {
//                    PAIRS.add(new Pair(a, b));
//                }
//            }
//        }
//    }

//    private static final int[] idx4 = BinaryInverseOrder.generateFor(4);

//    private static class TransformData {
//        DataMatrix<Quaternion> from;
//        DataMatrix<Quaternion> to;
//
//        DataMatrix<Quaternion> X00;
//        Map<Pair, DataMatrix<Quaternion>> Xab;
//
//        private TransformData(DataMatrix<Quaternion> from, DataMatrix<Quaternion> to) {
//            this.from = from;
//            this.to = to;
//        }
//    }
    public static DataMatrix<Quaternion> fourier(DataMatrix<Quaternion> from) throws Exception {
        DataMatrix<Quaternion> toMatrix = new DataMatrix<Quaternion>(from.getSize());
        fourier(from, toMatrix);
        return toMatrix;
    }

    private static void fourier(DataMatrix<Quaternion> initialFrom, DataMatrix<Quaternion> to) throws Exception {
        DataMatrix<Quaternion> dmSuperpositioned = SuperpositionHelper.executeSuperposition(initialFrom);

        DataMatrix<Quaternion> tmpRes = QuaternionFourierExecutorFinalInversedCyclic.fourier(dmSuperpositioned);
        DataMatrixCyclic Q = new DataMatrixCyclic(tmpRes.getData(), tmpRes.getSize());

        List<List<DataMatrix<Quaternion>>> Sab = SuperpositionHelper.Sab(Q);

        SuperpositionHelper.Decompose(to, Q, Sab);
    }
}