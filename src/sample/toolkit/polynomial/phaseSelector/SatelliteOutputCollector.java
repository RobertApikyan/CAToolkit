package sample.toolkit.polynomial.phaseSelector;

import sample.toolkit.generators.DataUtils;
import sample.toolkit.polynomial.PolynomialState;

import java.util.Arrays;

public class SatelliteOutputCollector implements PhaseSelector {
    // Արբանյակի ելքային թաբերի ցուցիչները
    private final int[] indexes;
    // Ելքային C/A կոդի զանգված
    private int[] output;

    public SatelliteOutputCollector(int[] outputIndexes, int runLength) {
        this.indexes = outputIndexes;
        this.output = new int[runLength];
    }
    // Մեթոդին որպես արգումենտ գեներացիայի քայլի համարը և քայլին համապատասխան
    // երկրորդ G2 ռեգիստրի թաբերի արժեքները
    @Override
    public final int applyG2(int step, PolynomialState state) {
        int value = DataUtils.calculateExOrIndexes(state.getValues(), indexes);
        output[step] = DataUtils.modAdd(output[step], value);
        return value;
    }
    // Մեթոդին որպես արգումենտ գեներացիայի քայլի համարը և քայլին համապատասխան
    // առաջին G1 ռեգիստրի ելքային արժեքը
    public final int applyG1(int step, int value) {
        output[step] = DataUtils.modAdd(output[step], value);
        return value;
    }

    public int[] getValues() {
        return output;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SatelliteOutputCollector that = (SatelliteOutputCollector) o;
        return Arrays.equals(indexes, that.indexes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(indexes);
    }
}
