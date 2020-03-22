package sample.lfsrStatePath;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import sample.enums.LfsrType;
import sample.toolkit.polynomial.FibonacciLfsr;
import sample.toolkit.polynomial.GaloisLfsr;
import sample.toolkit.polynomial.PolynomialState;
import sample.toolkit.polynomial.polynomial_processor.Lfsr;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static sample.main.CompareGaloisPolynomialStates.COMPLETE_CA;
import static sample.utils.Utils.tryParseInt;
import static sample.utils.Utils.tryParseIntArray;

public class LfsrStatePathController {

    private ThreadPoolExecutor executor =
            new ThreadPoolExecutor(4, 4, 1000, TimeUnit.MILLISECONDS,
                    new LinkedBlockingDeque<>(), runnable -> {
                final Thread thread = new Thread(runnable);
                thread.setDaemon(true);
                return thread;
            });

    @FXML
    TextArea feedbackPosTa;

    @FXML
    TextArea lengthTa;

    @FXML
    TextArea resultTa;

    @FXML
    TextArea sumPosTa;

    @FXML
    ChoiceBox<LfsrType> typeCb;

    // 3, 4, 5, 6, 9, 11, 13, 16, 19, 21, 24, 27
    // 2, 3, 4, 5, 8, 10, 12, 15, 18, 20, 23, 26

    public void initialize() {
        typeCb.getItems().addAll(LfsrType.values());
        typeCb.setValue(LfsrType.FIBONACCI);
    }

    public void generate(ActionEvent actionEvent) {
        int lfsrLength = tryParseInt(lengthTa.getText());
        int[] feedbackPositions = tryParseIntArray(feedbackPosTa.getText(), ",");
        int[] sumPositions = tryParseIntArray(sumPosTa.getText(), ",");
        LfsrType selectedValue = typeCb.getValue();
        resultTa.setText("");
        switch (selectedValue) {
            case FIBONACCI:
                executor.execute(() -> compareFibonacciPolynomials(feedbackPositions,
                        lfsrLength, sumPositions));
                break;
            case GALOIS:
                executor.execute(() -> compareGaloisPolynomials(feedbackPositions,
                        lfsrLength, sumPositions));
                break;
        }
    }

    // feedbackIndices - հետադարձ կապին մասնակցող թաբերի համարներ
    // lfsrLength - ռեգիստրի երկարություն
    // sumPositions - Գեներացիայի այն քայլերի համարները,
    // որոնց թաբերի գումարով ըստ մոդուլ երկուսի կատարվում է որոնում
    private void compareFibonacciPolynomials(
            int[] feedbackIndices,
            int lfsrLength,
            int[] sumPositions
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
        // էն ԳՀԿՏՌ-ի վիճակը
        for (int i = 0; i < COMPLETE_CA; i++) {
            lfsr.process();
            states.add(lfsr.captureState());
        }

        // Կատարվում է իտերացիա ըստ ԳՀԿՏՌ-ի գեներացիայի մեկ ցիկլի երկարության
        for (int i = 0; i < COMPLETE_CA - 1; i++) {

            print("step= " + i + "\t { " + states.get(i).toString() + " } ");

            // stateSummary փոփոխականում պահվում են sumPositions փոփոխականով
            // տրված գեներացիայի քայլերին համապատասխան թաբերի արժեքների գումարը
            // ըստ մոդուլ երկուսի
            PolynomialState stateSummary = null;


            // Իտերացիա ըստ ներմուծած sumPositions փոփոխականի
            for (int sumPosition : sumPositions) {

                int pos = i + sumPosition;
                if (pos >= COMPLETE_CA) {
                    break;
                }
                if (stateSummary == null) {
                    stateSummary = states.get(i);
                    print("\t s" + pos);
                } else {
                    // կատարվում է թաբերի արժեքների
                    // գումարում ըստ մոդուլ երկուսի
                    stateSummary = stateSummary.exOr(states.get(pos));
                    print(" + s" + pos);
                }
            }

            // Կատարվում է ստացված stateSummary փոփոխականի արժեքին համապատասխան
            // քայլի որոնում ընդհանուր գեներացիայի արժեքներից
            int similarStep = states.indexOf(stateSummary);

            print(" = s" + similarStep + " { " + stateSummary.toString() + " } " + "\n");
        }
    }

    private void compareGaloisPolynomials(
            int[] feedbackIndices,
            int lfsrLength,
            int[] sumPositions
    ) {

        Lfsr polynomial = new GaloisLfsr(feedbackIndices, lfsrLength);

        List<PolynomialState> states = new ArrayList<PolynomialState>();
        states.add(polynomial.captureState());

//        COMPLETE_CA = (int) Math.pow(2, polynomial.getRegisterSize()) - 1;

        for (int i = 0; i < COMPLETE_CA; i++) {
            polynomial.process();
            states.add(polynomial.captureState());
        }

        for (int i = 0; i < COMPLETE_CA - 1; i++) {
            print("step= \t" + i + "\t |" + states.get(i).toString() + " ");

            PolynomialState sum = null;
//                    states.get(i);
//
//            print("s" + i);

            for (int sumPosition : sumPositions) {
                int f = i + sumPosition;
                PolynomialState state = states.get(f);
                if (sum == null) {
                    sum = state;
                } else {
                    sum = sum.exOr(state);
                }

                print("s" + f + " + ");
            }

            int similarStep = states.indexOf(sum);

            print(" = s" + similarStep + " ( " + sum.toString() + " ) " + "\n");
        }
    }

    private void print(String text) {
        Platform.runLater(() -> resultTa.setText(resultTa.getText() + text));
    }

}
