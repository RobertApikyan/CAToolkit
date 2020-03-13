package sample.diffCodes;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class FileUtils {

    public static void writeCodes(CodeIndividual individual,String path,String fileName) {
        try (PrintWriter writer = new PrintWriter(new File(path,fileName))) {
            for (int codeNumber = 0; codeNumber < individual.getCodesCount(); codeNumber++) {
                double[] codes = individual.getSingleCodeChunk(codeNumber);

                writer.print(codeNumber+1+". ");
                for (int i = 0; i < codes.length; i++) {
                    writer.print(codes[i]);

                    if (i != codes.length - 1) {
                        writer.print(", ");
                    }
                }
                writer.println();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
