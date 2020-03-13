package sample.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import sample.enums.GenerationType;
import sample.enums.LfsrType;
import sample.toolkit.polynomial.FibonacciLfsr;
import sample.toolkit.polynomial.GaloisLfsr;
import sample.toolkit.polynomial.parallel_fibonacci_lfsr.FibonacciParallelLFSR;
import sample.toolkit.polynomial.parallel_galois_lfsr.GaloisParallelLFSR;
import sample.toolkit.polynomial.polynomial_processor.Lfsr;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static sample.toolkit.satellites.SatellitesCaFactory.COMPLETE_CA;
import static sample.utils.Utils.tryParseInt;
import static sample.utils.Utils.tryParseIntArray;

public class LfsrGeneratorController {


    private ThreadPoolExecutor executor =
            new ThreadPoolExecutor(4, 4, 1000, TimeUnit.MILLISECONDS,
                    new LinkedBlockingDeque<>());

    @FXML
    TextArea feedbackPosTa;

    @FXML
    TextArea lengthTa;

    @FXML
    TextArea resultTa;

    @FXML
    ChoiceBox<LfsrType> lfsrTypeCb;

    @FXML
    ChoiceBox<GenerationType> generationTypeCb;

    // 3, 4, 5, 6, 9, 11, 13, 16, 19, 21, 24, 27
    // 2, 3, 4, 5, 8, 10, 12, 15, 18, 20, 23, 26

    public void initialize() {
        lfsrTypeCb.getItems().addAll(LfsrType.values());
        lfsrTypeCb.setValue(LfsrType.FIBONACCI);
        generationTypeCb.getItems().addAll(GenerationType.values());
        generationTypeCb.setValue(GenerationType.SEQUENTIAL);
    }

    public void generate(ActionEvent actionEvent) {
        int lfsrLength = tryParseInt(lengthTa.getText());
        int[] feedbackPositions = tryParseIntArray(feedbackPosTa.getText(), ",");
        resultTa.setText("");

        LfsrType lfsrType = lfsrTypeCb.getValue();

        GenerationType generationType = generationTypeCb.getValue();

        print(lfsrType+"\n"+generationType+"\n");

        switch (lfsrType) {
            case FIBONACCI:
                switch (generationType) {
                    case SEQUENTIAL:
                        generateFibonacciSequential(feedbackPositions,lfsrLength);
                        break;
                    case PARALLEL:
                        generateFibonacciParallel(feedbackPositions,lfsrLength);
                        break;
                }
                break;
            case GALOIS:
                switch (generationType) {
                    case SEQUENTIAL:
                        generateGaloisSequential(feedbackPositions,lfsrLength);
                        break;
                    case PARALLEL:
                        generateGaloisParallel(feedbackPositions,lfsrLength);
                        break;
                }
                break;
        }
    }

    private void generateFibonacciSequential(int[] feedbackPositions, int lfsrLength) {
        Lfsr lfsr = new FibonacciLfsr(feedbackPositions, lfsrLength);
        for (int i = 0; i < COMPLETE_CA; i++) {
            print(lfsr.process() +", ");
        }
    }

    private void generateGaloisSequential(int[] feedbackPositions, int lfsrLength) {
        Lfsr lfsr = new GaloisLfsr(feedbackPositions, lfsrLength);
        for (int i = 0; i < COMPLETE_CA; i++) {
            print(lfsr.process() +", ");
        }
    }

    private void generateFibonacciParallel(int[] feedbackPositions, int lfsrLength) {
        FibonacciParallelLFSR lfsr = new FibonacciParallelLFSR();

        int[] initialStates = generateInitialState(lfsrLength);

        lfsr.generate(initialStates, feedbackPositions, 0, (output) -> {
            String outputData = Arrays.toString(output)
                    .replaceAll("\\[", "")
                    .replaceAll("]", "");

            print(outputData);
        });
    }

    private void generateGaloisParallel(int[] feedbackPositions, int lfsrLength) {
        GaloisParallelLFSR lfsr = new GaloisParallelLFSR();

        int[] initialStates = generateInitialState(lfsrLength);

        lfsr.generate(initialStates, feedbackPositions, 0, (output) -> {
            String outputData = Arrays.toString(output)
                    .replaceAll("\\[", "")
                    .replaceAll("]", "");

            print(outputData);
        });
    }

    private int[] generateInitialState(int length) {
        int[] array = new int[length];
        for (int i = 0; i < array.length; i++) {
            array[i] = 1;
        }
        return array;
    }

    private void print(String text) {
        Platform.runLater(() -> resultTa.setText(resultTa.getText() + text));
    }

}
