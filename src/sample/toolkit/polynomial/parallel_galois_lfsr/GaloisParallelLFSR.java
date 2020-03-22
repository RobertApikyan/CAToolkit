package sample.toolkit.polynomial.parallel_galois_lfsr;

import sample.toolkit.polynomial.GaloisLfsr;
import sample.toolkit.polynomial.PolynomialState;
import sample.toolkit.polynomial.base_paralle.ParallelLfsr;
import sample.toolkit.polynomial.polynomial_processor.Lfsr;

import java.util.HashMap;
import java.util.Map;

public class GaloisParallelLFSR extends ParallelLfsr {

    public GaloisParallelLFSR(){
        this(false);
    }

    public GaloisParallelLFSR(boolean skipParallelBits) {
        super(skipParallelBits);
    }

    @Override
    protected Lfsr createLfsr(int[] feedbackPositions, int[] outputRegisters, int tabsCount) {
        // Վերադարձնում է նոր Ֆիբոնաչիի ռեգիստրի դասի օբյեկտ համապատասխան տրված
        // պարամետրերի
        return new GaloisLfsr(feedbackPositions, tabsCount, outputRegisters);
    }

    @Override
    protected Map<Integer, PolynomialState> calculateParallelSteps(int[] feedbackPositions, Integer tabsCount, int step) {
        Map<Integer, PolynomialState> states = new HashMap<Integer, PolynomialState>();
        // Որոշվում են գեներացիայի այն քայլերի համարները որոնք անհրաժեշտ են
        // զւոգահեռացման բանաձևի իրականացման համար
        states.put(step, null);
        for (int feedbackPosition : feedbackPositions) {
            states.put(step + feedbackPosition - 1, null);
        }
        return states;
    }
}
