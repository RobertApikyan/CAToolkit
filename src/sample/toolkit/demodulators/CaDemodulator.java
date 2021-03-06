package sample.toolkit.demodulators;

import sample.toolkit.generators.CaGeneratorLegacy;
import sample.toolkit.generators.DataGenerator;
import sample.toolkit.generators.DataUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robert on 01.10.2017.
 */
public class CaDemodulator implements Demodulator<List<Integer>, List<Integer>> {
    private CaGeneratorLegacy caGenerator;
    private List<Integer> ca;// Ca code

    public CaDemodulator(CaGeneratorLegacy caGenerator) {
        this.caGenerator = caGenerator;
    }

    @Override
    public List<Integer> demodulate(List<Integer> data) {
        initCaCodes();
        List<Integer> outData = new ArrayList<Integer>();

        for (int i = 0; i < data.size(); i += DataGenerator.DATA_STEP) {
            boolean isReverse = DataUtils.isReverse(data.get(i), ca);
            int dataBit = (isReverse ? 0 : 1);
            outData.add(dataBit);
        }

        return outData;
    }

    private void initCaCodes() {
        ca = new ArrayList<Integer>();
        for (int i = 0; i < DataGenerator.DATA_STEP; i++) {
            int caBit = caGenerator.generate();
            ca.add(caBit);
        }
    }
}
