package sample.toolkit.generators;

import sample.toolkit.polynomial.PolynomialOne;
import sample.toolkit.polynomial.PolynomialTwo;

/**
 * Created by Robert on 30.09.2017.
 */
public class CaGeneratorLegacy implements Generator<Integer> {
    private PolynomialOne polynomialOne;
    private PolynomialTwo polynomialTwo;

    public CaGeneratorLegacy(PolynomialOne polynomialOne) {
        this(polynomialOne, null);
    }

    public CaGeneratorLegacy(PolynomialOne polynomialOne, PolynomialTwo polynomialTwo) {
        this.polynomialOne = polynomialOne;
        this.polynomialTwo = polynomialTwo;
    }

    @Override
    public Integer generate() {
        return DataUtils.modAdd(polynomialOne.process(), polynomialTwo.process());
    }

    public void setPolynomialTwo(PolynomialTwo polynomialTwo) {
        this.polynomialTwo = polynomialTwo;
    }
}
