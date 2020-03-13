package sample.toolkit.polynomial.parallel_galois_lfsr;

import sample.toolkit.polynomial.GaloisLfsr;
import sample.toolkit.polynomial.PolynomialState;
import sample.toolkit.polynomial.base_paralle.ParallelLfsr;
import sample.toolkit.polynomial.polynomial_processor.Lfsr;

import java.util.HashMap;
import java.util.Map;

public class GaloisParallelLFSR extends ParallelLfsr {

    public GaloisParallelLFSR() {
    }

    public GaloisParallelLFSR(boolean skipParallelBits) {
        super(skipParallelBits);
    }

    @Override
    protected Map<Integer, PolynomialState> calculateParallelSteps(int[] feedbackPositions, Integer tabsCount, int step) {
        Map<Integer, PolynomialState> states = new HashMap<Integer, PolynomialState>();

        // Put the So state
        states.put(step, null);
        // Define the required states
        for (int feedbackPosition : feedbackPositions) {
            states.put(step + feedbackPosition - 1, null);
        }

        return states;
    }

    @Override
    protected Lfsr createLfsr(int[] exOrIndexes, int[] outputRegisters, int registersCount) {
        return new GaloisLfsr(exOrIndexes, registersCount, outputRegisters);
    }
}
