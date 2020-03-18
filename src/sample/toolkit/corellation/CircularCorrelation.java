package sample.toolkit.corellation;

import static sample.toolkit.corellation.Correlation.arrayShift;
import static sample.toolkit.corellation.Correlation.value;

public class CircularCorrelation {

    public static double[] crossValues(
            double[] sample,
            double[] source,
            double emptyValue,
            double corrCoefficient
    ) {
        // Առաջին քայլով հավասարեցվում են տրված երկու զանգվածների երկարությունները

        // Զանգված, որի երկարությունը հավասար է source զանգվածի երկարությանը,
        // և պարունակելու է sample փոփոխականի արժեքները
        double[] equalizedSample;

        if (sample.length < source.length) {

            equalizedSample = new double[source.length];

            // equalizedSample փոփոխականը ստանում է
            // sample փոփոխականի արժեքները,
            // իսկ equalizedSample դատարկ դիրքերը լրացվում են
            // emptyValue փոփոխականի արժեքներով
            for (int i = 0; i < equalizedSample.length; i++) {
                equalizedSample[i] = emptyValue;
                if (sample.length > i) {
                    equalizedSample[i] = sample[i];
                }
            }
        } else {
            equalizedSample = sample;
        }

        // Փոփոխական որի մեջ պահվելու են կորելյացիաի արժեքները
        double[] corrValues = new double[sample.length + 2 * source.length];

        int corrIndex = 0;
        // Կատարվում է իտերացիա, որի յուրաքանչյուր քայլում կատարվում է
        // առաջին sample զանգվածի տեղաշարժ երկրորդի նկատմամբ
        for (int i = 1 - sample.length; i < sample.length + source.length + 1; i++) {
            // Կատարվում է sample զանգվածի տեղաշարժ
            double[] shiftedSample = arrayShiftCircular(equalizedSample, i, true);

            // հաշվարկվում է կորելյացիայի արժեքը տեղաշարժած shiftedSample զանգվածի
            // և source զանգվածի միջև
            // իտերացիայի քայլի համար
            corrValues[corrIndex] =
                    value(0, Math.max(shiftedSample.length, source.length) - 1, shiftedSample, source) / corrCoefficient;

            corrIndex++;
        }

        // Վերադարձնում է կորելյացիայի արժեքները
        return corrValues;
    }


    public static double[] arrayShiftCircular(double[] array, int shift, boolean copyArray) {
        return arrayShift(array, shift, true, copyArray, 0.0);
    }

}
