package sample.toolkit.corellation;

import java.util.Arrays;

public class Correlation {

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

    public static double[] arrayShift(
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
}
