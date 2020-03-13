package sample.toolkit.corellation;

import java.util.Arrays;

public class Correlation {

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
            double[] shiftedSample = arrayShiftLinear(equalizedSample, i, true);

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

    // Բազմապատկում է տրված first և second
    // զանգվածների արժեքները տրված start և end
    // միջակայքերի համար
    public static double value(int start,
                               int end,
                               double[] first,
                               double[] second) {
        // i ցուցչի արժեքը ցույց է տալիս առաջին
        // ազդանշանի միջակայքի առաջին տարրի դիրքիը
        int i = start;
        // j ցուցչի արժեքը ցույց է տալիս երկրորդ
        // ազդանշանի միջակայքի առաջին տարրի դիրքիը
        int j = Math.max(first.length, second.length) - (end - start) - 1;

        // Փոփոխական որը պահում է կորելյացիայի արժեքը
        double sum = 0.0;

        // կատարվում է իտերացիա 0-ից մինչև միջակայքի
        // երկարության չափով
        for (int n = 0; n <= end - start; n++) {

            // Փոփոխական, որի մեջ պահվում է առաջին ազդանշանի
            // միջակայքից n-րդ դիրքին համապատասխան արժեքը X(n)
            double firstValue = 0.0;
            if (first.length > i) {
                firstValue = first[i];
            }

            // Փոփոխական, որի մեջ պահվում է երկրորդ ազդանշանի
            // միջակայքից n-րդ դիրքին համապատասխան արժեքը Y(n)
            double secondValue = 0.0;
            if (second.length > j) {
                secondValue = second[j];
            }

            // հաշվարկվում է կորելյացիայի արժեքը X(n) * Y(n)
            sum += firstValue * secondValue;

            i++;
            j++;
        }

        return sum;
    }

    public static double[] correlation(
            int start, // առաջին ցուցիչ
            int end, // երկրորդ ցուցիչ
            double[] first, // X(n) ազդանշան
            double[] second, // Y(n) ազդանշան
            double[] result, // Կորելյացիայի արժեքներ
            double coefficient // Կորելյացիայի գործակից
    ) {
        // Հաշվարկվում է կորելյացիայի քայլի համարը
        int step = start + end + 1;
        // Ընտրվում է տրված ազդանշանների առավելագույնի
        // երկարությունը
        int length = Math.max(first.length, second.length);


        int ds = start;
        int de = end;

        // հաշվարկվում է հաշորդ քայլի համար առաջին ds և
        // երկրորդ de ցուցիչների դիրքերը
        if ((length - 1 - step) >= 0) {
            // Մեկ միավորով տեղաշարժում ենք de ցուցիչը
            // քանի որ ds-ը հասել է առավելագույն արժեքին,
            // այսինքն գտնվում է ազդանշանի վերջում
            de++;
        } else if ((length - step) <= 0) {
            // Տեղաշարժում ենք ds առաջին ցուցիչը,
            // Քանի դեռ այն չի հասել ազդանշանի վերջը
            ds++;
        }
        // Եթե քայլերի քանակը հավասար է 2n, որտեղ
        // n-ը առավելագույն ազդանշանի երկարությունն է,
        // ապա ֆունկցիան դադարեցնում է կորելյացիայի հաշվարկը
        // և վերադարձնում հաշվարկած արժեքները
        if (step == 2 * length) {
            return result;
        } else {
            // Հակառակ դեպքում հաշվարկվում է նշված
            // start և end միջակայքերում կորելյացիայի արժեքը
            double sum = value(start, end, first, second);
            // Ստացված կորելացիայի արժեքը բաժանվում է
            // միջինացման գործակցի վրա, բերելով այն [0:1] միջակայքի
            result[step - 1] = sum / coefficient;

            // Ռեկուրսիվ կանչվում է կորելացիոն ֆունկցիան
            // փոխանցելով
            return correlation(ds, de, first, second, result, coefficient);
        }
    }

    private static double[] arrayShift(
            double[] array,
            int shift,
            boolean circular,
            boolean copyArray,
            double emptyValue
    ) {
        if (array.length <= 1) {
            return array;
        }

        boolean toRight = shift > 0;

        double[] shiftedArray = array;

        if (copyArray) {
            shiftedArray = Arrays.copyOf(array, array.length);
        }

        for (int step = 0; step < Math.abs(shift); step++) {

            double tail = emptyValue;

            if (circular) {
                tail = toRight ? shiftedArray[array.length - 1] : shiftedArray[0];
            }

            if (toRight) {
                for (int i = array.length - 1; i >= 0; i--) {
                    if (i - 1 >= 0) {
                        shiftedArray[i] = shiftedArray[i - 1];
                    } else {
                        shiftedArray[i] = tail;
                    }
                }
            } else {
                for (int i = 0; i < array.length; i++) {
                    if (i + 1 < array.length) {
                        shiftedArray[i] = shiftedArray[i + 1];
                    } else {
                        shiftedArray[i] = tail;
                    }
                }
            }
        }

        return shiftedArray;
    }

    public static double[] arrayShiftCircular(double[] array, int shift, boolean copyArray) {
        return arrayShift(array, shift, true, copyArray, 0.0);
    }

    public static double[] arrayShiftLinear(double[] array, int shift, boolean copyArray) {
        return arrayShift(array, shift, false, copyArray, 0.0);
    }
}
