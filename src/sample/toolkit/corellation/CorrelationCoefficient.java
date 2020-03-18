package sample.toolkit.corellation;

public class CorrelationCoefficient {
    public static double coefficient(double[] first, double[] second) {
        // firstSum-ին է վերագրվում առաջին ազդանշանի դիսկրետ
        // արժեքների քառակուսիների գումարը
        double firstSum = 0.0;
        // secondSum-ին է վերագրվում երկրորդ ազդանշանի դիսկրետ
        // արժեքների քառակուսիների գումարը
        double secondSum = 0.0;

        // կատարվում է իտերացիա ըստ տրված ազդանշանների
        // դիսկրետ արժեքների քանակի և հաշվարկվում է տրված
        // ազդանշանների դիսկրետ արժեքների քառակուսիների գումարը
        for (int i = 0; i < Math.max(first.length, second.length); i++) {
            if (i < first.length) {
                firstSum += Math.pow(first[i], 2.0);
            }
            if (i < second.length) {
                secondSum += Math.pow(second[i], 2.0);
            }
        }

        if (firstSum == 0) {
            firstSum = 1;
        }

        if (secondSum == 0) {
            secondSum = 1;
        }

        // Վերադարձնում է ազդանշանների դիսկրետ արժեքների
        // քառակուսային գումարի արտադրյալի քառակուսի արմատաը
        return Math.sqrt(firstSum * secondSum);
    }
}
