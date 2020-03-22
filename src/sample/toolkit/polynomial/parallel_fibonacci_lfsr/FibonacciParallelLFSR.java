package sample.toolkit.polynomial.parallel_fibonacci_lfsr;

import sample.toolkit.polynomial.FibonacciLfsr;
import sample.toolkit.polynomial.PolynomialState;
import sample.toolkit.polynomial.base_paralle.ParallelLfsr;
import sample.toolkit.polynomial.polynomial_processor.Lfsr;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FibonacciParallelLFSR extends ParallelLfsr {

    public FibonacciParallelLFSR(){
        this(false);
    }

    public FibonacciParallelLFSR(boolean skipParallelBits) {
        super(skipParallelBits);
    }

    @Override
    protected Lfsr createLfsr(int[] feedbackPositions, int[] outputRegisters, int tabsCount) {
        // Վերադարձնում է նոր Ֆիբոնաչիի ռեգիստրի դասի օբյեկտ համապատասխան տրված
        // պարամետրերի
        return new FibonacciLfsr(feedbackPositions, tabsCount, outputRegisters);
    }

    @Override
    protected Map<Integer, PolynomialState> calculateParallelSteps(int[] feedbackPositions, Integer tabsCount, int step) {
        Map<Integer,PolynomialState> states = new HashMap<Integer, PolynomialState>();
        // Որոշվում են գեներացիայի այն քայլերի համարները որոնք անհրաժեշտ են
        // զւոգահեռացման բանաձևի իրականացման համար
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
