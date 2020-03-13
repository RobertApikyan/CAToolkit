package sample.toolkit.polynomial.polynomial_processor;

import sample.toolkit.generators.DataUtils;
import sample.toolkit.polynomial.PolynomialState;

import java.util.Arrays;

/**
 * ԳՀԿՏՌ-ի աբստրակ դասի նկարագրություն,
 * Գալոայի կամ Ֆիբոնաչիի ԳՀԿՏՌ-ների դասերը
 * հանդիսանում են նշված դասի ժառանգներ
 */
public abstract class Lfsr {

    /**
     * Հետադարձ կապի համարներ
     */
    protected int[] feedbackIndices;
    /**
     * Ռեգիստրի բջիջների արժեքներ
     */
    protected int[] values;
    /**
     * Գեներացիայի քալի համար
     */
    protected int generationStep = 0;
    /**
     * Ռեգիստրի բջիջների քանակը
     */
    protected int registerSize;

    /**
     * Ռեգիստր հայտարարելու կոնստրուկտոր
     * @param feedbackIndices Հետադարձ կապի համարներ
     * @param registerSize Ռեգիստրի բջիջների քանակը
     */
    public Lfsr(int[] feedbackIndices, int registerSize) {
        this.feedbackIndices = feedbackIndices;
        this.registerSize = registerSize;
        // Գեներացվում են ռեգիստրի բջիջների նախնական արժեքները,
        // որոնք հավասար են մեկի
        values = DataUtils.createBytes(registerSize);
    }

    /**
     * Ռեգիստրը կատարում է գեներացի մեկ քայլ և
     * վերադարձնում է ելքային արժեքը
     */
    public int process() {
        generationStep++;
        return processNext();
    }

    /**
     * Վերադարձնում է ռեգիստրի բջիջների արժեքները
     */
    public int[] getCurrentBytes() {
        return values;
    }

    /**
     * Վերադարձնում է ռեգիստրի վիճակը բնութագրող
     * օբյեկտ
     */
    public PolynomialState captureState() {
        return new PolynomialState(values);
    }

    /**
     * Վերադարձնում է ռեգիստրի բջիջների քանակը
     */
    public int getRegisterSize() {
        return registerSize;
    }

    /**
     * Վերադարձնում է ռեգիստրի հետադարձ կապի
     * դիրքերի համարները
     */
    public int[] getFeedbackIndices() {
        return feedbackIndices;
    }

    /**
     * Բերում է ռեգիստրը նախնական վիճակի,
     * զրոյացնում է գեներացիայի քայլերի փոփոխականի արժեքը,
     * բջիջների արժեքները վերագրում է մեկի
     */
    public void resetRegister() {
        generationStep = 0;
        values = DataUtils.createBytes(registerSize);
    }

    /**
     * Տրվում է ռեգիստրի վիճակը բնութագրող օբյեկտ,
     * և ռեգիստրի բջիջների արժեքները վերագրվում են
     * տրված օբյեկիտի համապատասխան արժեքներին
     */
    public void setState(PolynomialState state) {
        int[] values = state.getValues();
        setValues(Arrays.copyOf(values, values.length));
    }

    /**
     * Կատարում է մեկ բիտով ռեգիստրի բջջիջների
     * աջակողմյան տեղաշարժ, և առաջին բջջին
     * վերագրվում է startValue-ի արժեքը
     */
    protected int shift(int startValue) {
        int lastItemIndex = values.length - 1;
        int outPut = values[lastItemIndex];
        System.arraycopy(values, 0, values, 1, lastItemIndex);
        values[0] = startValue;
        return outPut;
    }

    /**
     * Տրվում է ռեգիստրի բջիջների արժեքները
     */
    protected void setValues(int[] values) {
        if (this.values.length != values.length) {
            throw new IllegalArgumentException();
        }
        this.values = values;
    }

    /**
     * Lfsr դասի ժառանգ դասերը իրականացնում են
     * սեփական գեներացիայի մեկ քայլի համար
     * պահանջվող տրամաբանական քայլերի
     * հաջորդականությունը, օրինակ՝
     * Ֆիբոնաչիի կամ Գալոայի ԳՀԿՏՌ-ներ
     * իրականացնելիս
     */
    protected abstract int processNext();

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("step= ").append(generationStep).append(" | ");
        for (int b : values) {
            builder.append(b).append(" ");
        }
        return builder.toString();
    }
}
