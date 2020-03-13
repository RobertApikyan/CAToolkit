package sample.toolkit.polynomial.phaseSelector;

import sample.toolkit.polynomial.PolynomialState;

public interface PhaseSelector {
    int applyG2(int step, PolynomialState state);
    int applyG1(int step, int value);
}
