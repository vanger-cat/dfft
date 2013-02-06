package com.vangercat.data.helpers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.<p>
 * <p/>
 * Date: 15.11.2009<p>
 * Time: 20:33:33<p>
 */
public class BinaryInverseOrder<T> {
    public List<T> makeBinaryInverseOrder(List<T> data) {
        List<T> res = new ArrayList<T>(data.size());

        int[] idxs = BinaryInverseOrder.generateFor(data.size());

        for (int i = 0; i < data.size(); i++) {
            res.add(data.get(idxs[i]));
        }

        return res;
    }

    public static int[] generateFor(final int N) {
        int binaryLength = getBinaryLength(N - 1);

        int[] res = new int[N];
        List<String> arr = new ArrayList<String>();
        for (int i = 0; i < N; i++) {
            int idx = 0;
            int tmp = i;
            int multiplier = N / 2;
            for (int j = 0; j < binaryLength; j++ ) {
                idx += (tmp % 2) * multiplier;
                multiplier = multiplier >> 1;
                tmp = tmp >> 1;
            }

            res[idx] = i;
        }

        return res;
    }

    private static int getBinaryLength(final int N_in) {
        int N = N_in;
        int binaryLength = 1;
        while (N > 1) {
            binaryLength++;
            N = N / 2;
        }
        return binaryLength;
    }

    public static void main(String[] args) {
        int N = 16;
        int[] orderForN = generateFor(N);
        for (int i = 0; i < N; i++ ) {
            System.out.print(orderForN[i] + "\t");
        }
    }
/**/
}
