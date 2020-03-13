package sample.toolkit.polynomial;

import sample.toolkit.generators.DataUtils;
import sample.toolkit.polynomial.polynomial_processor.Lfsr;

/**
 * Created by Robert on 30.09.2017.
 */
public class PolynomialTwo extends Lfsr {
    // 1+x2+x3+x6+x8+x9+x10 algorithm
    private static final int[] EX_OR_INDEXES = new int[]{2, 3, 6, 8, 9, 10};
    private int[] goldNumbers;

    public PolynomialTwo(int[] goldNumbers) {
        super(EX_OR_INDEXES,10);
        this.goldNumbers = goldNumbers;
    }

    @Override
    public int processNext() { ;
        byte outPut =  DataUtils.calculateExOrIndexes(values, goldNumbers);
        shift( DataUtils.calculateExOrIndexes(values, getFeedbackIndices()));
        return outPut;
    }
}
