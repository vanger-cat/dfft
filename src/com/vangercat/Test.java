package com.vangercat;

import com.vangercat.data.DataMatrix;
import com.vangercat.data.Quaternion;
import com.vangercat.dfft.QuaternionFourierExecutor22;
import com.vangercat.dfft.QuaternionFourierExecutorFinalInversedCyclic;
import com.vangercat.dfft.QuaternionFourierExecutorFinalInversedCyclicWithSuperposition;
import com.vangercat.io.DataLoader;

import java.io.*;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.<p>
 * <p/>
 * Date: 04.11.2009<p>
 * Time: 23:49:08<p>
 */
// At the current moment this is main class. It is used for testing.
public class Test {
    public static void main(String[] args) throws Exception {
        //Loading initial matrix
        DataMatrix<Quaternion> dm_in = new DataLoader("1.txt").getFullDataTableFromFile();

        Writer writer = new OutputStreamWriter(System.out);
//        dm_in.write(writer);

        if (args.length == 0 || args[0].equals("compare")) {
            System.out.println("Exectuing compare:");
            DataMatrix<Quaternion> standartResult = simpleFourie(dm_in, writer);
            DataMatrix<Quaternion> result = fastFourie(dm_in, writer);

            System.out.println("Compare result: " + isMatrixEqual(result, standartResult));
            writeToFiles(result);
            return;
        }

        if (args[0].equals("simple"))
        {
            writeToFiles(simpleFourie(dm_in, writer));
            return;
        }

        if (args[0].equals("fast"))
        {
            writeToFiles(fastFourie(dm_in, writer));
        }

    }

    private static void writeToFiles(DataMatrix<Quaternion> result) throws IOException {
        writeToFile(
                new BufferedWriter(new FileWriter(new File("output.txt"))),
                result
        );

        writeSeparateComplexNumber(
                result,
                new BufferedWriter(new FileWriter(new File("output_real.txt"))),
                new BufferedWriter(new FileWriter(new File("output_complex.txt")))
        );
    }

    private static DataMatrix<Quaternion> simpleFourie(DataMatrix<Quaternion> dm_in, Writer writer) throws Exception {
        long initialTime;
        DataMatrix<Quaternion> result;
        initialTime = Calendar.getInstance().getTimeInMillis();
        result = QuaternionFourierExecutor22.fourier(dm_in);

        System.out.println("Simple dfft (" + ((Calendar.getInstance().getTimeInMillis() - initialTime)/1000d) + " seconds):");
//        result.write(writer);
        return result;
    }

    private static DataMatrix<Quaternion> fastFourie(DataMatrix<Quaternion> dm_in, Writer writer) throws Exception {
        long initialTime;
        DataMatrix<Quaternion> result;
        initialTime = Calendar.getInstance().getTimeInMillis();
        result = QuaternionFourierExecutorFinalInversedCyclicWithSuperposition.fourier(dm_in);
        System.out.println("Complex(inversed cyclic with superposition) dfft (" + ((Calendar.getInstance().getTimeInMillis() - initialTime)/1000d) + " seconds):");
//        result.write(writer);
        result = QuaternionFourierExecutorFinalInversedCyclic.fourier(dm_in);
        return result;
    }

    private static void writeToFile(BufferedWriter bw, DataMatrix<Quaternion> dm) throws IOException {
        dm.write(bw);
        bw.flush();
        bw.close();
    }

    private static Boolean isMatrixEqual(DataMatrix<Quaternion> matrix1, DataMatrix<Quaternion> matrix2) {
        if (matrix1.getSize() != matrix2.getSize()) {
            return false;
        }
        for (int i = 0; i < matrix1.getSize(); i++)
            for (int j = 0; j < matrix1.getSize(); j++)
                if (!matrix1.getValue(i,j).toString().equals(matrix2.getValue(i, j).toString()))
                    return false;

        return true;
    }

    private static void writeSeparateComplexNumber(DataMatrix<Quaternion> dm, Writer realWriter, Writer complexWriter) throws IOException {
        int size = dm.getSize();
        realWriter.write(size + "\n");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                realWriter.append(dm.getValue(i, j).realPart()).append("\t");
            }
            realWriter.append("\n");
        }
        realWriter.flush();
        realWriter.close();

        complexWriter.write(size + "\n");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                complexWriter.append(dm.getValue(i, j).complexPart()).append("\t");
            }
            complexWriter.append("\n");
        }
        complexWriter.flush();
        complexWriter.close();


    }
}
