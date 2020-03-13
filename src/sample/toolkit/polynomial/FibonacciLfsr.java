package sample.toolkit.polynomial;

import sample.toolkit.generators.DataUtils;
import sample.toolkit.polynomial.polynomial_processor.Lfsr;

// ԳՀԿՏՌ-ի դասի իրականացումը Ֆիբոնաչիի ռեգիստրի համար
public class FibonacciLfsr extends Lfsr {
    // Ելքային թաբերի դիրքեր
    private int[] outputRegisters;

    public FibonacciLfsr(int[] exOrIndexes,
                         int registersCount) {
        this(exOrIndexes, registersCount, new int[]{registersCount});
    }

    public FibonacciLfsr(int[] exOrIndexes,
                         int registersCount,
                         int[] outputRegisters) {
        super(exOrIndexes, registersCount);
        this.outputRegisters = outputRegisters;
    }

    // Հաշվարկում է ելքային արժեքը և կատարում է ռեգիստրի
    // թաբերի արժեքների դեպի աջ տեղաշարժ
    @Override
    protected int processNext() {
        // հաշվարկվում է ռեգիստրի ելքային արժեքը
        final int output =  DataUtils.calculateExOrIndexes(values, outputRegisters);
        // Հաշվարկվում է հետադարձ կապերի գումարը ըստ մոդուլ երկուսի
        final int lastIndexValue = DataUtils.calculateExOrIndexes(values, getFeedbackIndices());
        // Կատարվում է ռեգիստրի արժեքների դեպի աջ տեղաշարժ
        shift(lastIndexValue);
        return output;
    }
}
