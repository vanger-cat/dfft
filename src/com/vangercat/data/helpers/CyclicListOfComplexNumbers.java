package com.vangercat.data.helpers;

import com.vangercat.data.ComplexNumber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.<p>
* <p/>
* Date: 14.11.2009<p>
* Time: 18:14:26<p>
*/
public class CyclicListOfComplexNumbers {
    private int N;

    private List<ComplexNumber> list = new ArrayList<ComplexNumber>();

    public CyclicListOfComplexNumbers(int N, ComplexNumber q) {
        this.N = N;

        double sin = Math.sin((2 * Math.PI) / (double) N);
        ComplexNumber basic = new ComplexNumber(
                Math.cos((2 * Math.PI) / (double) N),
                sin * q.getB()
        );

        list = new ArrayList<ComplexNumber>(N);
        ComplexNumber next = ComplexNumber.a0;
        list.add(next);
        for (int i = 1; i < N; i++) {
            next = next.mul(basic);
            list.add(next);
        }
    }

    public ComplexNumber get(int n) {
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