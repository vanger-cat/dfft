package com.vangercat.dfft;

import com.vangercat.data.QuaternionDataMatrixSubSquare;
import com.vangercat.data.Quaternion;
import com.vangercat.data.DataMatrix;
import com.vangercat.data.DataMatrixSubSquare;
import com.vangercat.data.helpers.BinaryInverseOrder;
import com.vangercat.data.helpers.Pair;
import com.vangercat.data.helpers.CyclicListOfQuaternions;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class FastQuaternionFourierExecutor implements Serializable {
//    private static final List<Pair> SUBMATRIX_POSITIONS;
//    static {
//        SUBMATRIX_POSITIONS = new ArrayList<Pair>();
//        SUBMATRIX_POSITIONS.add(new Pair(0, 0));
//
//        SUBMATRIX_POSITIONS.add(new Pair(0, 1));
//        SUBMATRIX_POSITIONS.add(new Pair(0, 3));
//        SUBMATRIX_POSITIONS.add(new Pair(2, 1));
//        SUBMATRIX_POSITIONS.add(new Pair(2, 3));
//        SUBMATRIX_POSITIONS.add(new Pair(1, 0));
//        SUBMATRIX_POSITIONS.add(new Pair(1, 2));
//        SUBMATRIX_POSITIONS.add(new Pair(1, 1));
//        SUBMATRIX_POSITIONS.add(new Pair(1, 3));
//        SUBMATRIX_POSITIONS.add(new Pair(3, 0));
//        SUBMATRIX_POSITIONS.add(new Pair(3, 2));
//        SUBMATRIX_POSITIONS.add(new Pair(3, 1));
//        SUBMATRIX_POSITIONS.add(new Pair(3, 3));
//    }
    
    public static DataMatrix<Quaternion> fourier(DataMatrix<Quaternion> from) throws Exception {
        DataMatrix<Quaternion> toMatrix = new DataMatrix<Quaternion>(from.getSize());
        fourier(from, toMatrix);
        return toMatrix;
    }

    public static void fourier(DataMatrix<Quaternion> from, DataMatrix<Quaternion> to) throws Exception {
        if (from.getSize() == 2) {
            fourierForSquare2x2(from, to);
            return;
        }

        if (from.getSize() == 4) {
            fourierForSquare4x4(from, to);
            return;
        }

        fourierForSquareNxN(from, to);
        return;

//        throw new Exception("Can execute fourier only for 2*2 and 4*4 matrixes");
    }

    // executes local decomposition of matrix on 13 sub matrixes
    private static List<DataMatrixSubSquare<Quaternion>> decompose(DataMatrix<Quaternion> matrix) {
        List<DataMatrixSubSquare<Quaternion>> res = new ArrayList<DataMatrixSubSquare<Quaternion>>(13);
        res.add(new DataMatrixSubSquare<Quaternion>(matrix, matrix.getY(), matrix.getX(), matrix.getSize() / 2));

        matrix.getSize();

        for (int i = 0; i < matrix.getSize(); i += matrix.getSize() / 4) {
            for (int j = 0; j < matrix.getSize(); j += matrix.getSize() / 4) {
                if ((i >= matrix.getSize() / 2) || (j >= matrix.getSize() / 2)) {
                    res.add(
                            new DataMatrixSubSquare<Quaternion>(
                                    matrix,
                                    matrix.getY() + i,
                                    matrix.getX() + j,
                                    matrix.getSize() / 4
                            )
                    );
                }
            }
        }

        return res;
    }

    private static void fourierForSquare2x2(DataMatrix<Quaternion> from, DataMatrix<Quaternion> toMatrix) {
//       image22(0,0)   = image(i,j).Qa + image (i,j+1).Qa + image (i+1,j).Qa + image (i+1,j+1).Qa;
//       image22(0,1)   = image(i,j).Qa - image (i,j+1).Qa + image (i+1,j).Qa - image (i+1,j+1).Qa;
//       image22(1,0)   = image(i,j).Qa + image (i,j+1).Qa - image (i+1,j).Qa - image (i+1,j+1).Qa;
//       image22(1,1)   = image(i,j).Qa - image (i,j+1).Qa - image (i+1,j).Qa + image (i+1,j+1).Qa;

        toMatrix.setValue(
                0,
                0,
                from.getValue(0, 0).sum(from.getValue(0, 1)).sum(from.getValue(1, 0)).sum(from.getValue(1, 1))
        );

        toMatrix.setValue(
                0,
                1,
                from.getValue(0, 0).sum(from.getValue(0, 1).mul(-1)).sum(from.getValue(1, 0)).sum(from.getValue(1, 1).mul(-1))
        );

        toMatrix.setValue(
                1,
                0,
                from.getValue(0, 0).sum(from.getValue(0, 1)).sum(from.getValue(1, 0).mul(-1)).sum(from.getValue(1, 1).mul(-1))
        );

        toMatrix.setValue(
                1,
                1,
                from.getValue(0, 0).sum(from.getValue(0, 1).mul(-1)).sum(from.getValue(1, 0).mul(-1)).sum(from.getValue(1, 1))
        );
    }

    private static void fourierForSquare4x4(DataMatrix<Quaternion> from, DataMatrix<Quaternion> toMatrix) {
//        List<List<Quaternion>> data = this.getData();
//        for (int i = 0; i < this.getSize(); i++) {
//            for (int j = 0; j < this.getSize(); j++) {
//                Quaternion value = data.get(i).get(j);
//                toMatrix.setValue(i, j, this.getValue(i, j));
//            }
//        }

        // 00
        toMatrix.setValue(
                0,
                0,
                (from.getValue(0, 0)).sum(from.getValue(0, 1)).sum(from.getValue(0, 2)).sum(from.getValue(0, 3)).
                        sum(from.getValue(1, 0)).sum(from.getValue(1, 1)).sum(from.getValue(1, 2)).sum(from.getValue(1, 3)).
                        sum(from.getValue(2, 0)).sum(from.getValue(2, 1)).sum(from.getValue(2, 2)).sum(from.getValue(2, 3)).
                        sum(from.getValue(3, 0)).sum(from.getValue(3, 1)).sum(from.getValue(3, 2)).sum(from.getValue(3, 3))
        );
        // 01
        toMatrix.setValue(
                0,
                1,
                (from.getValue(0, 0)).sum(from.getValue(0, 1).mulOnJ()).sum(from.getValue(0, 2).mul(-1)).sum(from.getValue(0, 3).mulOnJ().mul(-1)).
                        sum(from.getValue(1, 0)).sum(from.getValue(1, 1).mulOnJ()).sum(from.getValue(1, 2).mul(-1)).sum(from.getValue(1, 3).mulOnJ().mul(-1)).
                        sum(from.getValue(2, 0)).sum(from.getValue(2, 1).mulOnJ()).sum(from.getValue(2, 2).mul(-1)).sum(from.getValue(2, 3).mulOnJ().mul(-1)).
                        sum(from.getValue(3, 0)).sum(from.getValue(3, 1).mulOnJ()).sum(from.getValue(3, 2).mul(-1)).sum(from.getValue(3, 3).mulOnJ().mul(-1))
        );
        // 02
        toMatrix.setValue(
                0,
                2,
                (from.getValue(0, 0)).sum(from.getValue(0, 1).mul(-1)).sum(from.getValue(0, 2)).sum(from.getValue(0, 3).mul(-1)).
                        sum(from.getValue(1, 0)).sum(from.getValue(1, 1).mul(-1)).sum(from.getValue(1, 2)).sum(from.getValue(1, 3).mul(-1)).
                        sum(from.getValue(2, 0)).sum(from.getValue(2, 1).mul(-1)).sum(from.getValue(2, 2)).sum(from.getValue(2, 3).mul(-1)).
                        sum(from.getValue(3, 0)).sum(from.getValue(3, 1).mul(-1)).sum(from.getValue(3, 2)).sum(from.getValue(3, 3).mul(-1))
        );
        // 03
        toMatrix.setValue(
                0,
                3,
                (from.getValue(0, 0)).sum(from.getValue(0, 1).mulOnJ().mul(-1)).sum(from.getValue(0, 2).mul(-1)).sum(from.getValue(0, 3).mulOnJ()).
                        sum(from.getValue(1, 0)).sum(from.getValue(1, 1).mulOnJ().mul(-1)).sum(from.getValue(1, 2).mul(-1)).sum(from.getValue(1, 3).mulOnJ()).
                        sum(from.getValue(2, 0)).sum(from.getValue(2, 1).mulOnJ().mul(-1)).sum(from.getValue(2, 2).mul(-1)).sum(from.getValue(2, 3).mulOnJ()).
                        sum(from.getValue(3, 0)).sum(from.getValue(3, 1).mulOnJ().mul(-1)).sum(from.getValue(3, 2).mul(-1)).sum(from.getValue(3, 3).mulOnJ())
        );
        // 10
        toMatrix.setValue(
                1,
                0,
                (from.getValue(0, 0)).sum(from.getValue(0, 1)).sum(from.getValue(0, 2)).sum(from.getValue(0, 3)).
                        sum((from.getValue(1, 0).sum(from.getValue(1, 1)).sum(from.getValue(1, 2)).sum(from.getValue(1, 3))).mulIOn()).
                        sum((from.getValue(2, 0).sum(from.getValue(2, 1)).sum(from.getValue(2, 2)).sum(from.getValue(2, 3))).mul(-1)).
                        sum((from.getValue(3, 0).sum(from.getValue(3, 1)).sum(from.getValue(3, 2)).sum(from.getValue(3, 3))).mulIOn().mul(-1))
        );
        // 11
        toMatrix.setValue(
                1,
                1,
                (from.getValue(0, 0)).sum(from.getValue(0, 1).mulOnJ()).sum(from.getValue(0, 2).mul(-1)).sum(from.getValue(0, 3).mulOnJ().mul(-1)).
                        sum((from.getValue(1, 0).sum(from.getValue(1, 1).mulOnJ()).sum(from.getValue(1, 2).mul(-1)).sum(from.getValue(1, 3).mulOnJ().mul(-1))).mulIOn()).
                        sum((from.getValue(2, 0).sum(from.getValue(2, 1).mulOnJ()).sum(from.getValue(2, 2).mul(-1)).sum(from.getValue(2, 3).mulOnJ().mul(-1))).mul(-1)).
                        sum((from.getValue(3, 0).sum(from.getValue(3, 1).mulOnJ()).sum(from.getValue(3, 2).mul(-1)).sum(from.getValue(3, 3).mulOnJ().mul(-1))).mulIOn().mul(-1))
        );
        // 12
        toMatrix.setValue(
                1,
                2,
                (from.getValue(0, 0)).sum(from.getValue(0, 1).mul(-1)).sum(from.getValue(0, 2)).sum(from.getValue(0, 3).mul(-1)).
                        sum((from.getValue(1, 0).sum(from.getValue(1, 1).mul(-1)).sum(from.getValue(1, 2)).sum(from.getValue(1, 3).mul(-1))).mulIOn()).
                        sum((from.getValue(2, 0).sum(from.getValue(2, 1).mul(-1)).sum(from.getValue(2, 2)).sum(from.getValue(2, 3).mul(-1))).mul(-1)).
                        sum((from.getValue(3, 0).sum(from.getValue(3, 1).mul(-1)).sum(from.getValue(3, 2)).sum(from.getValue(3, 3).mul(-1))).mulIOn().mul(-1))
        );
        // 13
        toMatrix.setValue(
                1,
                3,
                (from.getValue(0, 0)).sum(from.getValue(0, 1).mulOnJ().mul(-1)).sum(from.getValue(0, 2).mul(-1)).sum(from.getValue(0, 3).mulOnJ()).
                        sum((from.getValue(1, 0).sum(from.getValue(1, 1).mulOnJ().mul(-1)).sum(from.getValue(1, 2).mul(-1)).sum(from.getValue(1, 3).mulOnJ())).mulIOn()).
                        sum((from.getValue(2, 0).sum(from.getValue(2, 1).mulOnJ().mul(-1)).sum(from.getValue(2, 2).mul(-1)).sum(from.getValue(2, 3).mulOnJ())).mul(-1)).
                        sum((from.getValue(3, 0).sum(from.getValue(3, 1).mulOnJ().mul(-1)).sum(from.getValue(3, 2).mul(-1)).sum(from.getValue(3, 3).mulOnJ())).mulIOn().mul(-1))
        );
        // 20
        toMatrix.setValue(
                2,
                0,
                (from.getValue(0, 0)).sum(from.getValue(0, 1)).sum(from.getValue(0, 2)).sum(from.getValue(0, 3)).
                        sum((from.getValue(1, 0).sum(from.getValue(1, 1)).sum(from.getValue(1, 2)).sum(from.getValue(1, 3))).mul(-1)).
                        sum(from.getValue(2, 0)).sum(from.getValue(2, 1)).sum(from.getValue(2, 2)).sum(from.getValue(2, 3)).
                        sum((from.getValue(3, 0).sum(from.getValue(3, 1)).sum(from.getValue(3, 2)).sum(from.getValue(3, 3))).mul(-1))
        );
        // 21
        toMatrix.setValue(
                2,
                1,
                (from.getValue(0, 0)).sum(from.getValue(0, 1).mulOnJ()).sum(from.getValue(0, 2).mul(-1)).sum(from.getValue(0, 3).mulOnJ().mul(-1)).
                        sum((from.getValue(1, 0).sum(from.getValue(1, 1).mulOnJ()).sum(from.getValue(1, 2).mul(-1)).sum(from.getValue(1, 3).mulOnJ().mul(-1))).mul(-1)).
                        sum(from.getValue(2, 0)).sum(from.getValue(2, 1).mulOnJ()).sum(from.getValue(2, 2).mul(-1)).sum(from.getValue(2, 3).mulOnJ().mul(-1)).
                        sum((from.getValue(3, 0).sum(from.getValue(3, 1).mulOnJ()).sum(from.getValue(3, 2).mul(-1)).sum(from.getValue(3, 3).mulOnJ().mul(-1))).mul(-1))
        );
        // 22
        toMatrix.setValue(
                2,
                2,
                (from.getValue(0, 0)).sum(from.getValue(0, 1).mul(-1)).sum(from.getValue(0, 2)).sum(from.getValue(0, 3).mul(-1)).
                        sum((from.getValue(1, 0).sum(from.getValue(1, 1).mul(-1)).sum(from.getValue(1, 2)).sum(from.getValue(1, 3).mul(-1))).mul(-1)).
                        sum(from.getValue(2, 0)).sum(from.getValue(2, 1).mul(-1)).sum(from.getValue(2, 2)).sum(from.getValue(2, 3).mul(-1)).
                        sum((from.getValue(3, 0).sum(from.getValue(3, 1).mul(-1)).sum(from.getValue(3, 2)).sum(from.getValue(3, 3).mul(-1))).mul(-1))
        );
        // 23
        toMatrix.setValue(
                2,
                3,
                (from.getValue(0, 0)).sum(from.getValue(0, 1).mulOnJ().mul(-1)).sum(from.getValue(0, 2).mul(-1)).sum(from.getValue(0, 3).mulOnJ()).
                        sum((from.getValue(1, 0).sum(from.getValue(1, 1).mulOnJ().mul(-1)).sum(from.getValue(1, 2).mul(-1)).sum(from.getValue(1, 3).mulOnJ())).mul(-1)).
                        sum(from.getValue(2, 0)).sum(from.getValue(2, 1).mulOnJ().mul(-1)).sum(from.getValue(2, 2).mul(-1)).sum(from.getValue(2, 3).mulOnJ()).
                        sum((from.getValue(3, 0).sum(from.getValue(3, 1).mulOnJ().mul(-1)).sum(from.getValue(3, 2).mul(-1)).sum(from.getValue(3, 3).mulOnJ())).mul(-1))
        );
        // 30
        toMatrix.setValue(
                3,
                0,
                (from.getValue(0, 0)).sum(from.getValue(0, 1)).sum(from.getValue(0, 2)).sum(from.getValue(0, 3)).
                        sum((from.getValue(1, 0).sum(from.getValue(1, 1)).sum(from.getValue(1, 2)).sum(from.getValue(1, 3))).mulIOn().mul(-1)).
                        sum((from.getValue(2, 0).sum(from.getValue(2, 1)).sum(from.getValue(2, 2)).sum(from.getValue(2, 3))).mul(-1)).
                        sum((from.getValue(3, 0).sum(from.getValue(3, 1)).sum(from.getValue(3, 2)).sum(from.getValue(3, 3))).mulIOn())
        );
        // 31
        //TODO: here we made changes in sign
        toMatrix.setValue(
                3,
                1,
                (from.getValue(0, 0)).sum(from.getValue(0, 1).mulOnJ()).sum(from.getValue(0, 2).mul(-1)).sum(from.getValue(0, 3).mulOnJ().mul(-1)).
                        sum((from.getValue(1, 0).sum(from.getValue(1, 1).mulOnJ()).sum(from.getValue(1, 2).mul(-1)).sum(from.getValue(1, 3).mulOnJ().mul(-1))).mulIOn().mul(-1)).
                        sum((from.getValue(2, 0).sum(from.getValue(2, 1).mulOnJ()).sum(from.getValue(2, 2).mul(-1)).sum(from.getValue(2, 3).mulOnJ().mul(-1))).mul(-1)).
                        sum((from.getValue(3, 0).sum(from.getValue(3, 1).mulOnJ()).sum(from.getValue(3, 2).mul(-1)).sum(from.getValue(3, 3).mulOnJ().mul(-1))).mulIOn())
        );
        // 32
        toMatrix.setValue(
                3,
                2,
                (from.getValue(0, 0)).sum(from.getValue(0, 1).mul(-1)).sum(from.getValue(0, 2)).sum(from.getValue(0, 3).mul(-1)).
                        sum((from.getValue(1, 0).sum(from.getValue(1, 1).mul(-1)).sum(from.getValue(1, 2)).sum(from.getValue(1, 3).mul(-1))).mulIOn().mul(-1)).
                        sum((from.getValue(2, 0).sum(from.getValue(2, 1).mul(-1)).sum(from.getValue(2, 2)).sum(from.getValue(2, 3).mul(-1))).mul(-1)).
                        sum((from.getValue(3, 0).sum(from.getValue(3, 1).mul(-1)).sum(from.getValue(3, 2)).sum(from.getValue(3, 3).mul(-1))).mulIOn())
        );
        // 33
        toMatrix.setValue(
                3,
                3,
                (from.getValue(0, 0)).sum(from.getValue(0, 1).mulOnJ().mul(-1)).sum(from.getValue(0, 2).mul(-1)).sum(from.getValue(0, 3).mulOnJ()).
                        sum((from.getValue(1, 0).sum(from.getValue(1, 1).mulOnJ().mul(-1)).sum(from.getValue(1, 2).mul(-1)).sum(from.getValue(1, 3).mulOnJ())).mulIOn().mul(-1)).
                        sum((from.getValue(2, 0).sum(from.getValue(2, 1).mulOnJ().mul(-1)).sum(from.getValue(2, 2).mul(-1)).sum(from.getValue(2, 3).mulOnJ())).mul(-1)).
                        sum((from.getValue(3, 0).sum(from.getValue(3, 1).mulOnJ().mul(-1)).sum(from.getValue(3, 2).mul(-1)).sum(from.getValue(3, 3).mulOnJ())).mulIOn())
        );
    }

    private static void fourierForSquareNxN(DataMatrix<Quaternion> from, DataMatrix<Quaternion> to) throws Exception {
        if (from.getSize() != to.getSize()) {
            throw new IllegalArgumentException("from.getSize() != to.getSize()");
        }

        int[] idxs = BinaryInverseOrder.generateFor(from.getSize());

        System.out.println(from);
        System.out.println();

        int N = from.getSize();
        List<DataMatrixSubSquare<Quaternion>> decomposition =
                        decompose(from);

        for (DataMatrixSubSquare<Quaternion> val: decomposition) {
            fourier(val, new QuaternionDataMatrixSubSquare(to, val.getY(), val.getX(), val.getSize()));
        }

        decomposition = decompose(to);

        System.out.println(to);
        System.out.println();

        CyclicListOfQuaternions w1 = new CyclicListOfQuaternions(from.getSize(), new Quaternion(0, 1, 0, 0));
        CyclicListOfQuaternions w2 = new CyclicListOfQuaternions(from.getSize(), new Quaternion(0, 0, 1, 0));

        int QuarterN = N / 4;
        for (int m1 = 0; m1 < QuarterN; m1++) {
            for (int m2 = 0; m2 < QuarterN; m2++) {
                for (int r = 0; r < 4; r++) {
                    for (int s = 0; s < 4; s++) {
                        Quaternion value = Quaternion.zero;
                        value = value.sum(
                                decomposition.get(0).getValue(
                                        m1 + (r % 2) * QuarterN,
                                        m2 + (s % 2) * QuarterN
                                )
                        );

                        for (int i = 1; i < 13; i++) {
                            DataMatrixSubSquare<Quaternion> Xab = decomposition.get(i);
                            int a = idxs[Xab.getY()];
                            int b = idxs[Xab.getX()];

                            Quaternion sumElem;

                            sumElem =
                                Quaternion.iPow(a * r).
                                    mul(w1.get(a * m1)).
                                        mul(Xab.getValue(m1, m2)).
                                    mul(w2.get(b * m2)).
                                mul(Quaternion.jPow(b * s)
                            );

                            value = value.sum(sumElem);
                        }

                        to.setValue(
                                m1 + r * QuarterN,
                                m2 + s * QuarterN,
                                value
                        );
                    }
                }
            }
        }
    }
}