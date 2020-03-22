package sample.main;

import sample.toolkit.polynomial.FibonacciLfsr;
import sample.toolkit.polynomial.PolynomialState;
import sample.toolkit.polynomial.parallel_fibonacci_lfsr.FibonacciParallelLFSR;
import sample.toolkit.polynomial.phaseSelector.SatelliteOutputCollector;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static sample.main.CompareGaloisPolynomialStates.COMPLETE_CA;
import static sample.toolkit.satellites.SatellitesCaFactory.createSatelliteCollectors;

public class GlobalCaCodeParallelGeneration {

    public static void main(String[] args) {
        globalParallelGeneration();
        parallelGeneration();
    }

    private static void globalParallelGeneration() {

        final FibonacciParallelLFSR g1 = new FibonacciParallelLFSR(false);
        final FibonacciParallelLFSR g2 = new FibonacciParallelLFSR(false);

        List<SatelliteOutputCollector> phaseSelectors = createSatelliteCollectors(COMPLETE_CA);

        Consumer<int[]> onGenerationFinished = (ignored) -> {
            if (g2.isFinished() && g1.isFinished()) {
                print(phaseSelectors);
            }
        };

        g1.generate(new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, new int[]{3, 10}, new int[]{10}, 0,
                onGenerationFinished,
                (pos, value) -> {
                    for (SatelliteOutputCollector phaseSelector : phaseSelectors) {
                        phaseSelector.applyG1(pos, value);
                    }
                },
                (pos, state) -> {
                }
        );

        g2.generate(new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, new int[]{2, 3, 6, 8, 9, 10}, new int[]{10}, 0,
                onGenerationFinished,
                (pos, value) -> {
                },
                (pos, state) -> {
                    for (SatelliteOutputCollector phaseSelector : phaseSelectors) {
                        phaseSelector.applyG2(pos, state);
                    }
                });
    }

    private static void parallelGeneration() {
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
            for (SatelliteOutputCollector collector : phaseSelectors) {
                // applyG1 second polynomial states to them
                int p2OutPut = collector.applyG2(caIndex, p2State);
                collector.applyG1(caIndex, p1OutPut);
            }
            // shift the second polynomial
            p2.process();
        }
        print(phaseSelectors);
    }

    private static void print(List<SatelliteOutputCollector> phaseSelectors) {
        for (SatelliteOutputCollector phaseSelector : phaseSelectors) {
            System.out.println(Arrays.toString(phaseSelector.getValues())
                    .replaceAll("\\[", "")
                    .replaceAll("]", ""));
        }
    }
}
