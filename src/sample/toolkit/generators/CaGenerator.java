package sample.toolkit.generators;

import sample.toolkit.polynomial.FibonacciLfsr;

// C/A կոդի գեներատորի դաս
public class CaGenerator implements Generator<Integer> {
    private FibonacciLfsr g1;
    private FibonacciLfsr g2;

    // Կոնստրուկտոր, որին փոխանցվում է արբանյակի համարին
    // համապատասխան ելքային թաբերիի դիրքերի համարները
    public CaGenerator(int[] goldNumbers){
        g1 = new FibonacciLfsr(new int[]{3,10},10);
        g2 = new FibonacciLfsr(new int[]{2, 3, 6, 8, 9, 10},10,goldNumbers);
    }

    // Իրականացնում է G1 և G2 ռեգիստրների
    // գեներացիայի մեկ քայլ
    @Override
    public Integer generate() {
        return DataUtils.modAdd(g1.process(), g2.process());
    }
}
