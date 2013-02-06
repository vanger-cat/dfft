package com.vangercat.io;

import com.vangercat.data.DataMatrix;
import static com.vangercat.data.DataMatrixSubSquare.isSizeValid;
import com.vangercat.data.Quaternion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.<p>
 * <p/>
 * Date: 04.11.2009<p>
 * Time: 22:19:27<p>
 */
public class DataLoader {
    private String fileName;

    public DataLoader(String fileName) {
        this.fileName = fileName;
    }

    public DataMatrix<Quaternion> getFullDataTableFromFile() throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
        try {
            String str;
            StringTokenizer st;

            str = reader.readLine();
            st = new StringTokenizer(str);

            int N = Integer.parseInt(st.nextToken());

            List<List<Quaternion>> arr = new ArrayList<List<Quaternion>>(N);

            isSizeValid(N);

            int i = 0;
            while (reader.ready()) {
                str = reader.readLine();
                st = new StringTokenizer(str);

                int j = 0;
                while (st.hasMoreTokens()) {
                    if (i >= N) {
                        throw new Exception("To many rows in file");
                    }
                    if (j == 0)
                        arr.add(new ArrayList<Quaternion>(N));

                    if (j >= N) {
                        throw new Exception("To many elements in row " + i);
                    }
                    arr.get(i).add(new Quaternion(Double.parseDouble(st.nextToken()), 0, 0, 0));
                    j++;
                }
                if (j != N && (i != N)) {
                    throw new Exception("Not enough elements in row " + i);
                }

                i++;
            }

            if (i < N) {
                throw new Exception("Not enough row, found only " + i + " rows");
            }

            return new DataMatrix<Quaternion>(arr, N);
        } finally {
            reader.close();
        }
    }
}
