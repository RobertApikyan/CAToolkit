package sample.toolkit.polynomial.parallel_fibonacci_lfsr;

import sample.toolkit.polynomial.FibonacciLfsr;
import sample.toolkit.polynomial.PolynomialState;
import sample.toolkit.polynomial.base_paralle.ParallelLfsr;
import sample.toolkit.polynomial.polynomial_processor.Lfsr;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FibonacciParallelLFSR extends ParallelLfsr {

    public FibonacciParallelLFSR() {
    }

    public FibonacciParallelLFSR(boolean skipParallelBits) {
        super(skipParallelBits);
    }

    @Override
    protected Lfsr createLfsr(int[] exOrIndexes, int[] outputRegisters, int registersCount) {
        return new FibonacciLfsr(exOrIndexes, registersCount, outputRegisters);
    }

    @Override
    protected Map<Integer, PolynomialState> calculateParallelSteps(int[] feedbackPositions, Integer tabsCount, int step) {
        Map<Integer,PolynomialState> states = new HashMap<Integer, PolynomialState>();
        // Որոշվում են գեներացիայի այն քայլերի համարները որոնք օգտագրոծվում են
        // զւոգահեռացման բանաձևում
        for (int feedbackPosition : feedbackPositions) {
            states.put(tabsCount - feedbackPosition + step, null);
        }
        return states;
    }

    public int[] getOutput() {
        return output;
    }

    public boolean isFinished() {
        return isFinished;
    }
}
