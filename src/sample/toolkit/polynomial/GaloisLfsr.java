package sample.toolkit.polynomial;

import sample.toolkit.generators.DataUtils;
import sample.toolkit.polynomial.polynomial_processor.Lfsr;

import java.util.Arrays;

// ԳՀԿՏՌ-ի դասի իրականացումը Գալոաի ռեգիստրի համար
public class GaloisLfsr extends Lfsr {
    // Ելքային թաբերի դիրքեր
    private int[] outputRegisters;

    public GaloisLfsr(int[] exOrIndexes,
                      int registersCount) {
        this(exOrIndexes, registersCount, new int[]{registersCount});
    }

    public GaloisLfsr(int[] exOrIndexes,
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
        final int output = DataUtils.calculateExOrIndexes(values,outputRegisters);
        // Հաշվարկվում է ռեգիստրի թաբերի բիտերի հաշվարկ ըստ
        // Գալոաի ռեգիստրի հետադարձ կապերի
        final int lastIndexValue = DataUtils.calculateGaloisFeedback(values, getFeedbackIndices());
        // Կատարվում է ռեգիստրի արժեքների դեպի աջ տեղաշարժ
        shift(lastIndexValue);
        return output;
    }

    public void setState(PolynomialState state) {
        int[] values = state.getValues();
        setValues(Arrays.copyOf(values, values.length));
    }
}
