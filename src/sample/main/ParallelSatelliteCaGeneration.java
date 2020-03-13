package sample.main;

import sample.generators.DataUtils;
import sample.toolkit.generators.CaGenerator;
import sample.toolkit.polynomial.FibonacciLfsr;
import sample.toolkit.polynomial.PolynomialState;
import sample.toolkit.polynomial.phaseSelector.SatelliteOutputCollector;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static sample.main.CompareGaloisPolynomialStates.COMPLETE_CA;
import static sample.toolkit.generators.DataUtils.printSatelliteCaSequences;
import static sample.toolkit.satellites.SatellitesCaFactory.*;

public class ParallelSatelliteCaGeneration {

    public static final String SEQUENTIAL_PATH = "/Users/robert/Desktop/Project_docs/tez/sequential.txt";
    public static final String PARALLEL_PATH = "/Users/robert/Desktop/Project_docs/tez/parallel.txt";

    public static void main(String[] args) {
        runParallel(PARALLEL_PATH);
        runSequential(SEQUENTIAL_PATH);
    }

    /**
     * This method will generate C/A codes in sequential manner, and write them in to .txt file
     *
     * @param filePath, the final complete directory of file, example
     */
    private static void runSequential(String filePath) {
        BufferedOutputStream bos = null;
        try {
            // Generate C/A codes
            int[][] codes = sequentialGeneration();
            bos = new BufferedOutputStream(
                    new FileOutputStream(filePath));
            // write C/A codes on file
            printSatelliteCaSequences(bos, "Parallel Generation", codes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.flush();
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This method will generate C/A codes in parallel manner, and write them in to .txt file
     *
     * @param filePath, the final complete directory of file, example
     */
    private static void runParallel(String filePath) {
        BufferedOutputStream bos = null;
        try {
            // Generate C/A codes
            int[][] codes = parallelGeneration();
            bos = new BufferedOutputStream(
                    new FileOutputStream(filePath));
            // write C/A codes on file
            printSatelliteCaSequences(bos, "Parallel Generation", codes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.flush();
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Generates C/A codes using parallel generation method
     *
     * @return The result is two dimensional array, where first dimension is the satellite number and second one
     * is appropriate C/A codes
     */
    private static int[][] parallelGeneration() {
        // initialize two dimensional empty array for results
        int[][] values = new int[SAT_COUNT][COMPLETE_CA];
        // initialize phase selectors for all satellites
        List<SatelliteOutputCollector> phaseSelectors = createSatelliteCollectors(1024);
        // initialize first polynomial
        FibonacciLfsr p1 = new FibonacciLfsr(new int[]{3, 10}, 10);
        // initialize second polynomial
        FibonacciLfsr p2 = new FibonacciLfsr(new int[]{2, 3, 6, 8, 9, 10}, 10);
        // start the iteration for 1024 C/A code cycle
        for (int caIndex = 0; caIndex < COMPLETE_CA; caIndex++) {
            // generate output bit for first polynomial
            int p1OutPut = p1.process();
            // take the second polynomial state (the registers values)
            PolynomialState p2State = p2.captureState();
            // iterate through all phase selectors
            for (int collectorIndex = 0; collectorIndex < phaseSelectors.size(); collectorIndex++) {
                // applyG1 second polynomial states to them
                SatelliteOutputCollector collector = phaseSelectors.get(collectorIndex);
                int p2OutPut = collector.applyG2(caIndex,p2State);
                // perform modulo addition between satellite output bit from second polynomial
                // and first register output bit
                values[collectorIndex][caIndex] = DataUtils.exOr(p1OutPut, p2OutPut);
            }
            // shift the second polynomial
            p2.process();
        }

        return values;
    }

    /**
     * Generates C/A codes using sequential generation method
     *
     * @return The result is two dimensional array, where first dimension is the satellite number and second one
     * is appropriate C/A codes
     */
    private static int[][] sequentialGeneration() {
        // initialize two dimensional empty array for results
        int[][] values = new int[SAT_COUNT][COMPLETE_CA];
        // initialize phase selectors for all satellites
        List<int[]> goldNumbers = getGoldNumbersintArrayList();

        for (int satelliteIndex = 0; satelliteIndex < SAT_COUNT; satelliteIndex++) {
            // initialize C/A code generator for iteration satellite
            CaGenerator generator = new CaGenerator(goldNumbers.get(satelliteIndex));
            // Generate all C/A codes for iteration satellite
            for (int caIndex = 0; caIndex < COMPLETE_CA; caIndex++) {
                int chip = generator.generate();
                values[satelliteIndex][caIndex] = chip;
            }
            // process to next
        }

        return values;
    }
}
