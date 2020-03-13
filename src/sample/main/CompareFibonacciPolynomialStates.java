package sample.main;

import sample.toolkit.polynomial.FibonacciLfsr;
import sample.toolkit.polynomial.GaloisLfsr;
import sample.toolkit.polynomial.PolynomialState;
import sample.toolkit.polynomial.polynomial_processor.Lfsr;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CompareFibonacciPolynomialStates {

    public static int COMPLETE_CA = 1024;

    public static void main(String[] args) {
        compareFibonacciPolynomials(new int[]{3,10},10);
    }

    private static void compareFibonacciPolynomials(
            int[] feedbackIndices,
            int lfsrLength
    ) {
        // ԳՀԿՏՌ նշված երկարությամբ և հետադարծ կապերով
        Lfsr lfsr = new FibonacciLfsr(feedbackIndices, lfsrLength);

        // states փոփոխականի մեջ պահվում են ԳՀԿՏՌ-ի գեներացիայի
        // ընթացքում ստացված բոլոր փոփոխականները
        List<PolynomialState> states = new ArrayList<PolynomialState>();
        // Պահվում է առաջին վիճակը, որը հավասար է {1, 1,..., 1, 1}
        states.add(lfsr.captureState());

        // Կատարվում է գեներացիա, և յուրաքանչյուր
        // գեներացիայի քայլի ընթացքում states փոփոխականում պահվում
        // է ԳՀԿՏՌ-ի վիճակը
        for (int i = 0; i < COMPLETE_CA; i++) {
            lfsr.process();
            states.add(lfsr.captureState());
        }

        // Կատարվում է իտերացիա ըստ ԳՀԿՏՌ-ի գեներացիայի մեկ ցիկլի երկարության
        for (int i = 0; i < COMPLETE_CA - 1; i++) {

            System.out.print("step= \t" + i + "\t |" + states.get(i).toString() + " ");

            PolynomialState sum = states.get(i);
            int fl = lfsr.getFeedbackIndices()[lfsr.getFeedbackIndices().length - 1];

            System.out.print("s" + i);

            for (int j = lfsr.getFeedbackIndices().length - 2; j >= 0; j--) {
                int exOrIndex = lfsr.getFeedbackIndices()[j];
                int f = i + fl - exOrIndex;
                if (f >= COMPLETE_CA) {
                    break;
                }
                sum = sum.exOr(states.get(f));
                System.out.print(" + s" + f);
            }

            int similarStep = states.indexOf(sum);

            System.out.print(" = s" + similarStep + " ( " + sum.toString() + " ) " + "\n");
        }
    }

    private static void compareGaloisPolynomials() {

        Lfsr polynomial = new GaloisLfsr(new int[]{3, 4, 5, 6, 9, 11, 13, 16, 19, 21, 24, 27}, 27);

        List<PolynomialState> states = new ArrayList<PolynomialState>();
        states.add(polynomial.captureState());

//        COMPLETE_CA = (int) Math.pow(2, polynomial.getRegisterSize()) - 1;

        for (int i = 0; i < COMPLETE_CA; i++) {
            polynomial.process();
            states.add(polynomial.captureState());
        }

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new PrintWriter(new File("/Users/robert/Desktop/Project_Docs/tez/polynomialCompared.txt")));

            for (int i = 0; i < COMPLETE_CA - 1; i++) {
                System.out.print("step= \t" + i + "\t |" + states.get(i).toString() + " ");

                PolynomialState sum = states.get(i);

                System.out.print("s" + i);

                for (int j = 0; j < polynomial.getFeedbackIndices().length; j++) {
                    int f = i + polynomial.getFeedbackIndices()[j] - 1;
                    PolynomialState state = states.get(f);
                    if (sum == null) {
                        sum = state;
                    } else {
                        sum = sum.exOr(state);
                    }

                    System.out.print(" + s" + f);
                }

                int similarStep = states.indexOf(sum);

                System.out.print(" = s" + similarStep + " ( " + sum.toString() + " ) " + "\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
