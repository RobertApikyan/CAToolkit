package sample.toolkit.detectors;

import sample.toolkit.generators.CaGeneratorLegacy;
import sample.toolkit.generators.DataUtils;
import sample.toolkit.polynomial.PolynomialTwo;
import sample.toolkit.satellites.SatellitesCaFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static sample.toolkit.satellites.SatellitesCaFactory.COMPLETE_CA;

/**
 * Created by Robert on 02.10.2017.
 */
public class GoldNumberDetector implements Detector<int[], List<Integer>> {
    private CaGeneratorLegacy caGenerator;

    public GoldNumberDetector(CaGeneratorLegacy caGenerator) {
        this.caGenerator = caGenerator;
    }

    @Override
    public int[] detect(List<Integer> bytes) {

        List<int[]> goldNumbers = SatellitesCaFactory.getGoldNumbersintArrayList();
        List<Integer> ca = new ArrayList<Integer>();
        List<Integer> caReverse = new ArrayList<Integer>();
        List<Integer> matches = new ArrayList<Integer>();

        for (int[] goldNumber : goldNumbers) {
            PolynomialTwo polynomialTwo = SatellitesCaFactory.createFor(goldNumber);

            caGenerator.setPolynomialTwo(polynomialTwo);

            for (int i = 0; i < COMPLETE_CA; i++) {
                int caCode = caGenerator.generate();
                ca.add(caCode);
                caReverse.add(DataUtils.reverseBite(caCode));
            }

            String bytesString = bytes.toString().replace("[", "").replace("]", "").replace(",","");
            String caString = ca.toString().replace("[", "").replace("]", "").replace(",","");
            String caReverseString = caReverse.toString().replace("[", "").replace("]", "").replace(",","");

            if (bytesString.contains(caString) || bytesString.contains(caReverseString)) {
                int caCount = DataUtils.stringCount(bytesString, caString);
                int caRevCount = DataUtils.stringCount(bytesString, caString);
                matches.add(caCount + caRevCount);
            } else {
                matches.add(0);
            }

            ca.clear();
        }

        int[] maxAndIndexValues = DataUtils.listMaxAndIndexValues(matches);
        System.out.println(matches.toString());
        System.out.println("Gold Number Detector: goldNumber= " + Arrays.toString(goldNumbers.get(maxAndIndexValues[1])) + ", " + "matches= " + maxAndIndexValues[0]);
        return goldNumbers.get(maxAndIndexValues[1]);
    }
}
