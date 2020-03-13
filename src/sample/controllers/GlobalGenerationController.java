package sample.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import sample.enums.CaGenerationType;
import sample.toolkit.polynomial.FibonacciLfsr;
import sample.toolkit.polynomial.PolynomialState;
import sample.toolkit.polynomial.parallel_fibonacci_lfsr.FibonacciParallelLFSR;
import sample.toolkit.polynomial.phaseSelector.SatelliteOutputCollector;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static sample.main.CompareGaloisPolynomialStates.COMPLETE_CA;
import static sample.toolkit.satellites.SatellitesCaFactory.createSatelliteCollectors;


public class GlobalGenerationController {

    @FXML
    ChoiceBox<CaGenerationType> caGeneratorTypeCb;

    @FXML
    TextArea resultTa;

    public void initialize() {
        caGeneratorTypeCb.getItems().addAll(CaGenerationType.values());
        caGeneratorTypeCb.setValue(CaGenerationType.SEQUENTIAL);
    }

    @FXML
    private void generate() {
        clean();
        switch (caGeneratorTypeCb.getValue()) {
            case SEQUENTIAL:
                sequentialGeneration();
                break;
            case PARALLEL:
                parallelGeneration();
                break;
        }
    }

    private void parallelGeneration() {
        // սարգվում են առաջին G1 և երկրորդ G2 ռեգիստրների օբյեկտները
        final FibonacciParallelLFSR g1 = new FibonacciParallelLFSR();
        final FibonacciParallelLFSR g2 = new FibonacciParallelLFSR();
        // գեներացվում են արբանյակների ելքային արժեքների թաբերի կոլեկտորները
        List<SatelliteOutputCollector> outputCollectors = createSatelliteCollectors(COMPLETE_CA);
        // Հետադարձ կապ որը աշխատում է առաջին և երկրորդ ռեգիստրի աշխատանքի ավարտի պահին
        Consumer<int[]> onGenerationFinished = (ignored) -> {
            // Ստուգվում են G1 և G2 ռեգիստրների աշխատանքի ավարտի պայմանը
            if (g2.isFinished() && g1.isFinished()) {
                // տպում է ստացված ելքային արժեքները
                Platform.runLater(() -> printCa(outputCollectors));
            }
        };
        // G1 ռեգիստրը սկսում է զուգահեռ գեներացիան տրված նախնական թաբերի արժեքների
        // {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, հետադարձ կապին մասնակցող թաբերի համարների {3, 10}
        // և ելքային թաբի համարի {10}
        g1.generate(new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, new int[]{3, 10}, new int[]{10}, 0,
                onGenerationFinished,
                // Հետադարձ կապ, որը գեներացիայի յուրաքանչյուր քայլի ընթացքում վերադարձնում է ռեգիստրի
                // ելքային արժեքը
                (pos, value) -> {
                    // Ռեգիստրի ստացված ելքային արժեքը փոխանցվում է արբանյակների ելքային արժեքների
                    // կոլեկտորներին
                    for (SatelliteOutputCollector outputCollector : outputCollectors) {
                        outputCollector.applyG1(pos, value);
                    }
                },
                (pos, state) -> {
                }
        );
        // G2 ռեգիստրը սկսում է զուգահեռ գեներացիան տրված նախնական թաբերի արժեքների
        // {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, հետադարձ կապին մասնակցող թաբերի համարների {2, 3, 6, 8, 9, 10}
        // և ելքային թաբի համարի {10}, սակայն ելքային թաբի արժեքնը տվյալ դեպքում անտեսվում է, քանի որ գեներացիայի
        // ընթացքում արբանյակների ելքային արժեքների կոլեկտորներին, որոնցից յուրաքանչյուրը ընտրում է ելքային արժեքների
        // թաբերի համարները ըստ արբանյակի
        g2.generate(new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, new int[]{2, 3, 6, 8, 9, 10}, new int[]{10}, 0,
                onGenerationFinished,
                (pos, value) -> {
                },
                (pos, state) -> {
                    for (SatelliteOutputCollector phaseSelector : outputCollectors) {
                        phaseSelector.applyG2(pos, state);
                    }
                });
    }

    private void sequentialGeneration() {
        // գեներացվում են արբանյակների ելքային արժեքների թաբերի կոլեկտորները
        List<SatelliteOutputCollector> outputCollectors = createSatelliteCollectors(1024);
        // Սարգվում են առաջին G1 և երկրորդ G2 ռեգիստրների օբյեկտները, ըստ տրված հետադարձ կապերի
        // համարների և ռեգիստրի երկարության
        FibonacciLfsr g1 = new FibonacciLfsr(new int[]{3, 10}, 10);
        FibonacciLfsr g2 = new FibonacciLfsr(new int[]{2, 3, 6, 8, 9, 10}, 10);
        // Կատարվում է իտերացիայի ըստ մեկ կրկնման ցիկլի
        for (int caIndex = 0; caIndex < COMPLETE_CA; caIndex++) {
            // Գեներացվում է G1 ռեգիստրի ելքային բիտը
            int g1outPut = g1.process();
            // g2State փոփոխականի մեջ պահվում է երկրորդ ռեգիստրի թաբերի արժեքները
            PolynomialState g2State = g2.captureState();
            // Կատարվում է իտերացիա ըստ բոլոր թաբերի կոլեկտորների
            for (SatelliteOutputCollector collector : outputCollectors) {
                // Արբանյակի կոլեկտորին են փոխանցվում G1 ռեգիստրի ելքային բիտը
                // և G2 ռեգիստրի թաբերի արժեքները
                collector.applyG2(caIndex, g2State);
                collector.applyG1(caIndex, g1outPut);
            }
            // G2 ռեգիստրը իրականցնում է գեներացիայի մեկ քայլ
            g2.process();
        }
        // Իտերացիայից հետո տպվում են արբանյակների C/A կոդերի արժեքները
        printCa(outputCollectors);
    }

    private void printCa(List<SatelliteOutputCollector> phaseSelectors) {
        for (int i = 0; i < phaseSelectors.size(); i++) {
            SatelliteOutputCollector phaseSelector = phaseSelectors.get(i);
            String ca = Arrays.toString(phaseSelector.getValues())
                    .replaceAll("\\[", "")
                    .replaceAll("]", "");

            println("Sat" + i + "= " + ca);
        }
    }

    private void println(String text) {
        resultTa.setText(resultTa.getText() + "\n" + text);
    }

    private void clean() {
        resultTa.clear();
    }
}
