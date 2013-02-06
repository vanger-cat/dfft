package com.vangercat.data.helpers;

import com.vangercat.data.DataMatrix;
import com.vangercat.data.DataMatrixCyclic;
import com.vangercat.data.Quaternion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.<p>
 * <p/>
 * Date: 09.12.2009<p>
 * Time: 9:54:49<p>
 */
public abstract class SuperpositionHelper {
    static public DataMatrix<Quaternion> executeSuperposition(DataMatrix<Quaternion> from) {
        DataMatrix<Quaternion> dm_in_new = new DataMatrix<Quaternion>(from.getSize() / 2);
        for (int m1 = 0; m1 < from.getSize(); m1 += 2) {
            for (int m2 = 0; m2 < from.getSize(); m2 += 2) {
                dm_in_new.setValue(
                        m1 / 2,
                        m2 / 2,
                        new Quaternion(
                                from.getValue(m1, m2).getQ0(),
                                from.getValue(m1, m2 + 1).getQ0(),
                                from.getValue(m1 + 1, m2).getQ0(),
                                from.getValue(m1 + 1, m2 + 1).getQ0()
                        )
                );
            }
        }
        return dm_in_new;
    }

    static public void Decompose(DataMatrix<Quaternion> to, DataMatrixCyclic q, List<List<DataMatrix<Quaternion>>> Sab) {
        CyclicListOfQuaternions w1 = new CyclicListOfQuaternions(q.getSize() * 2, Quaternion.i);
        CyclicListOfQuaternions w2 = new CyclicListOfQuaternions(q.getSize() * 2, Quaternion.j);
        int[][][] signs = new int[][][] {
                {
                        {1, 1, 1, 1},
                        {1, -1, 1, -1}},
                {
                        {1, 1, -1, -1},
                        {1, -1, -1, 1}
                }
        };
        for (int m1 = 0; m1 < q.getSize(); m1++) {
            for (int m2 = 0; m2 < q.getSize(); m2++) {
                Quaternion S00 = Sab.get(0).get(0).getValue(m1, m2);
                Quaternion w1S10 = w1.get(m1).mul(Sab.get(1).get(0).getValue(m1, m2));
                Quaternion S10w2 = (Sab.get(0).get(1).getValue(m1, m2)).mul(w2.get(m2));
                Quaternion w1S11w2 = w1.get(m1).mul(Sab.get(1).get(1).getValue(m1, m2)).mul(w2.get(m2));

                for (int a = 0; a < 2; a++) {
                    for (int b = 0; b < 2; b++) {
                        to.setValue(
                                m1 + q.getSize() * b,
                                m2 + q.getSize() * a,
                                S00.mul(signs[a][b][0]).
                                        sum(w1S10.mul(signs[a][b][1])).
                                        sum(S10w2.mul(signs[a][b][2])).
                                        sum(w1S11w2.mul(signs[a][b][3]))
                        );
                    }
                }
            }
        }
    }

    static public List<List<DataMatrix<Quaternion>>> Sab(DataMatrixCyclic q) {
        List<List<DataMatrix<Quaternion>>> Sab = new ArrayList<List<DataMatrix<Quaternion>>>();
        for (int a = 0; a < 2; a++) {
            List<DataMatrix<Quaternion>> row = new ArrayList<DataMatrix<Quaternion>>();
            for (int b = 0; b < 2; b++) {
                row.add(new DataMatrix<Quaternion>(q.getSize()));
            }
            Sab.add(row);
        }

        int[][][] signs = new int[][][] {
                {
                        {1, 1, 1, 1},
                        {1, 1, -1, -1}},
                {
                        {1, -1, 1, -1},
                        {1, -1, -1, 1}}
        };
        Quaternion[][][] multipliers = new Quaternion[][][] {
                {
                        {new Quaternion(0.25d, 0, 0, 0), new Quaternion(1, 0, 0, 0)},
                        {new Quaternion(0, -0.25d, 0, 0), new Quaternion(1, 0, 0, 0)},
                },
                {
                        {new Quaternion(1, 0, 0, 0), new Quaternion(0, 0, -0.25d, 0)},
                        {new Quaternion(0, -0.5d, 0, 0), new Quaternion(0, 0, -0.5d, 0)}
                }
        };

        for (int m1 = 0; m1 < q.getSize(); m1++) {
            for (int m2 = 0; m2 < q.getSize(); m2++) {
                for (int a = 0; a < 2; a++) {
                    for (int b = 0; b < 2; b++) {
                        Sab.get(a).get(b).setValue(
                                m1,
                                m2,
                                multipliers[a][b][0].
                                        mul(
                                            q.getValue(m1, m2).mul(signs[a][b][0]).
                                            sum(q.getValue(m1, -m2).epsilonI().mul(signs[a][b][1])).
                                            sum(q.getValue(-m1, m2).epsilonJ().mul(signs[a][b][2])).
                                            sum(q.getValue(-m1, -m2).epsilonK().mul(signs[a][b][3]))
                                        ).
                                        mul(multipliers[a][b][1])
                        );
                    }
                }
            }
        }

        return Sab;
    }
}
