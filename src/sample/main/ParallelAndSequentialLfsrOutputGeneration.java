package sample.main;

import sample.toolkit.polynomial.FibonacciLfsr;
import sample.toolkit.polynomial.parallel_fibonacci_lfsr.FibonacciParallelLFSR;
import sample.utils.ElapsedTimeCounter;

import java.util.Arrays;

public class ParallelAndSequentialLfsrOutputGeneration {

    public static void main(String[] args) {
        timeMeasure();
  }

    public static void timeMeasure(){
        final FibonacciParallelLFSR lfsr = new FibonacciParallelLFSR(false);
        // Ռեգիստրի սկզբնական թաբերի արժեքներ
        final int[] initialState = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        // Հետադարձ կապին մասնակցող թաբերի համարներ
        final int[] exOrIndexes = new int[]{2, 3, 6, 8, 9, initialState.length - 1};
        // Կրկնման մեկ ցիկլի երկարությունը
        final int maxRun = (int) Math.pow(2, initialState.length) - 1;

        // Զուգահեռ գեներացիա
        final ElapsedTimeCounter parGenerationTimeWatcher = new ElapsedTimeCounter();
        // Տևողության հաշվարկի սկիզբ
        parGenerationTimeWatcher.start();
        lfsr.generate(initialState, exOrIndexes, 0, outputBits -> {
            // Տևողության հաշվարկի ավարտ
            // Տևողության արժեքը վերագրվում է timeElapseInParallel փոփոխականին
            final long timeElapseInParallel = parGenerationTimeWatcher.stop();
            // Տպվում է տևողության արժեքը
            System.out.println("Parallel\t" + timeElapseInParallel + "\t" + Arrays.toString(outputBits));
        });

        // Հաջորդական գեներացիա
        final int[] linearOutputBits = new int[maxRun];
        // Կառուցվում է նույն պարամետրերով Ֆիբոնաչիի ԳՀԿՏՌ հաջորդական գեներացիայի
        // ժամանակի չափման համար
        final FibonacciLfsr polynomial = new FibonacciLfsr(exOrIndexes, initialState.length);

        final ElapsedTimeCounter seqGenerationTimeWatcher = new ElapsedTimeCounter();
        // Տևողության հաշվարկի սկիզբ
        seqGenerationTimeWatcher.start();

        for (int i = 0; i < maxRun; i++) {
            linearOutputBits[i] = polynomial.process();
        }

        // Տևողության հաշվարկի ավարտ
        // Տևողության արժեքը վերագրվում է timeElapseInSequential փոփոխականին
        final long timeElapseInSequential = seqGenerationTimeWatcher.stop();
        // Տպվում է տևողության արժեքը
        System.out.println("Sequential\t" + timeElapseInSequential + "\t" + Arrays.toString(linearOutputBits));
    }
}
