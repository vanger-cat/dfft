package com.vangercat.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.<p>
 * <p/>
 * Date: 05.11.2009<p>
 * Time: 0:41:10<p>
 */

// this class simple generates some dumb matrixes of necessary size
public class SimpleMatrixFileGenerator {
    static void generate(int n, String fileName) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fileName)));
        long full_size = n * n;
        try {
            bw.write(n + "\t");

            long counter = 0;
            for (int i = 0; i < n; i++) {
                bw.newLine();
                for (int j = 0; j < n; j++) {
                    bw.write(((i*n + j) % 100 ) + "\t");
                    counter++;
                }
                if (i % 10 == 0)
                    System.out.print("Completed " + counter + " of " + full_size + "\n");
            }
        } finally {
            bw.flush();
            bw.close();
        }
    }

    public static void main(String[] args) throws IOException {
        SimpleMatrixFileGenerator.generate(32, "1.txt");
    }
}
