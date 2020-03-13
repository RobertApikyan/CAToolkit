package sample.toolkit.polynomial.parallelCaGenerator;

import sample.toolkit.generators.CaGeneratorLegacy;
import sample.toolkit.generators.DataUtils;
import sample.toolkit.polynomial.PolynomialOne;
import sample.toolkit.polynomial.PolynomialState;
import sample.toolkit.polynomial.PolynomialTwo;
import sample.toolkit.polynomial.parallel_fibonacci_lfsr.FibonacciParallelLFSR;
import sample.utils.ElapsedTimeCounter;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static sample.toolkit.satellites.SatellitesCaFactory.SAT_14;
import static sample.utils.Utils.timeWatch;

public class ParallelCa {

    public static void main(String[] args) {

        int[] satellite = SAT_14;

        CaGeneratorLegacy caGenerator = new CaGeneratorLegacy(new PolynomialOne(), new PolynomialTwo(satellite));
        int[] sequentialValues = new int[1024];

        long sequentialTime = timeWatch(() -> {
            for (int i = 0; i < 1024; i++) {
                sequentialValues[i] = caGenerator.generate();
            }
        });

        System.out.println("sequential time =\t" + sequentialTime);
        System.out.println("Sequential output" + Arrays.toString(sequentialValues));

        ElapsedTimeCounter parallelTimeCounter = new ElapsedTimeCounter();
        parallelTimeCounter.start();
        generate(satellite, output -> {
                    long parallelTime = parallelTimeCounter.stop();
                    System.out.println("parallel time =\t" + parallelTime);
                    System.out.println("Parallel output=" + Arrays.toString(output));
                }
        );
    }

    public static void generate(int[] goldNumber,
                                Consumer<int[]> onComplete) {
        generate(goldNumber, onComplete, null);
    }

    public static void generate(int[] goldNumber,
                                Consumer<int[]> onComplete,
                                BiConsumer<Integer, PolynomialState> onEachProcessStep) {

        final int[] output = new int[1024];
        final FibonacciParallelLFSR firstParallelLfsr = new FibonacciParallelLFSR();
        final FibonacciParallelLFSR secondParallelLfsr = new FibonacciParallelLFSR();

        firstParallelLfsr.generate(new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, new int[]{3, 10}, new int[]{10}, 0, ignored -> {
                    if (secondParallelLfsr.isFinished()) {
                        onComplete.accept(output);
                    }
                },
                (pos, value) -> output[pos] = DataUtils.modAdd(output[pos], value),
                onEachProcessStep);

        secondParallelLfsr.generate(new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, new int[]{2, 3, 6, 8, 9, 10}, goldNumber, 0, ignored -> {
                    if (firstParallelLfsr.isFinished()) {
                        onComplete.accept(output);
                    }
                },
                (pos, value) -> output[pos] = DataUtils.modAdd(output[pos], value),
                onEachProcessStep);
    }


}
