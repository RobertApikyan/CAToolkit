package sample.main;

import sample.toolkit.polynomial.GaloisLfsr;
import sample.toolkit.polynomial.PolynomialState;
import sample.toolkit.polynomial.polynomial_processor.Lfsr;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CompareGaloisPolynomialStates {

    public static int COMPLETE_CA =1024;

    public static void main(String[] args) {
        compareGaloisPolynomials();
    }

    private static void compareFibonacciPolynomials() {

        Lfsr polynomial = new GaloisLfsr(new int[]{ 2, 3, 6, 8, 9, 17 }, 17);

        List<PolynomialState> states = new ArrayList<PolynomialState>();
        states.add(polynomial.captureState());

//        COMPLETE_CA = (int) Math.pow(2,polynomial.getRegisterSize())-1;

        for (int i = 0; i < COMPLETE_CA; i++) {
            polynomial.process();
            states.add(polynomial.captureState());
        }

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new PrintWriter(new File("/Users/robert/Desktop/Project_Docs/tez/polynomialCompared.txt")));

            for (int i = 0; i < COMPLETE_CA - 1; i++) {
                System.out.print("step= \t" + i + "\t |"+ states.get(i).toString()+" ");

                PolynomialState sum = states.get(i);
                int fl = polynomial.getFeedbackIndices()[polynomial.getFeedbackIndices().length  -1];

                System.out.print("s" + i);

                for (int j = polynomial.getFeedbackIndices().length-1; j >= 0 ; j--) {
                    int exOrIndex = polynomial.getFeedbackIndices()[j];
                    int f = i + exOrIndex -1;
                    if (f >= COMPLETE_CA){
                        break;
                    }
                    sum = sum.exOr(states.get(f));
                    System.out.print(" + s"+f);
                }

                int similarStep = states.indexOf(sum);

                System.out.print(" = s"+similarStep + " ( " + sum.toString() + " ) " + "\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }  finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void compareGaloisPolynomials() {

        Lfsr polynomial = new GaloisLfsr(new int[]{3, 4,5,6,9,11,13,16,19,21,24,27}, 27);

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
                    int f = i + polynomial.getFeedbackIndices()[j]-1;
                    PolynomialState state = states.get(f);
                    if (sum == null){
                        sum = state;
                    }else {
                        sum = sum.exOr(state);
                    }

                    System.out.print(" + s" + f);
                }

//                for (int j = polynomial.getExOrIndexes().length - 2; j >= 0; j--) {
//                    int exOrIndex = polynomial.getExOrIndexes()[j];
//                    int f = i + exOrIndex;
//                    if (f >= COMPLETE_CA) {
//                        break;
//                    }
//                    sum = sum.exOr(states.get(f));
//                    System.out.print(" + s" + f);
//                }

//                sum = new PolynomialState(DataUtils.reverse(sum.getValues()));

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
