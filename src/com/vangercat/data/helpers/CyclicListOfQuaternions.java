package com.vangercat.data.helpers;

import com.vangercat.data.Quaternion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.<p>
* <p/>
* Date: 14.11.2009<p>
* Time: 18:14:26<p>
*/
public class CyclicListOfQuaternions {
    private static Map<Quaternion, List<Quaternion>> quaterSequence = new HashMap<Quaternion, List<Quaternion>>();

    private Quaternion q;
    private int N;

    public CyclicListOfQuaternions(int N, Quaternion q) {
        this.q = q;
        this.N = N;
        
        List<Quaternion> list;
        list = quaterSequence.get(q);
        if (list == null || list.size() < N) {
            double sin = Math.sin((2 * Math.PI) / (double) N);
            Quaternion basic = new Quaternion(
                    Math.cos((2 * Math.PI) / (double) N),
                    sin * q.getQi(),
                    sin * q.getQj(),
                    0
            );

            list = new ArrayList<Quaternion>(N);
            Quaternion next = Quaternion.a;
            list.add(next);
            for (int i = 1; i < N; i++) {
                next = next.mul(basic);
                list.add(next);
            }

            quaterSequence.put(q, list);
        }
    }

    public Quaternion get(int n) {
        List<Quaternion> list = quaterSequence.get(q);
        int index = (n * (list.size() / N))  % list.size();
        return list.get(index);
    }

/*
    public static void main(String[] args) {
        CyclicListOfQuaternions tmp = new CyclicListOfQuaternions(4, new Quaternion(0, 1, 0, 0));
        for (int i = 0; i < 8; i++) {
            System.out.println(tmp.get(i));
        }
    }
*/
}
