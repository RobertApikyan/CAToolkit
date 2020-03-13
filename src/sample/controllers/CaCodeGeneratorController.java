package sample.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sample.enums.GenerationType;
import sample.toolkit.generators.CaGenerator;
import sample.toolkit.generators.DataUtils;
import sample.toolkit.polynomial.parallel_fibonacci_lfsr.FibonacciParallelLFSR;
import sample.toolkit.satellites.SatellitesCaFactory;
import sample.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CaCodeGeneratorController {
    @FXML
    ChoiceBox<String> choiceBox;

    @FXML
    TextArea outputTa;

    @FXML
    TextField outputLengthTf;

    @FXML
    ChoiceBox<GenerationType> generationTypeCb;

    @FXML
    CheckBox partialCb;

    private List<String> satelliteNumbers;

    private List<int[]> goldNumbers;

    public void initialize() {
        generationTypeCb.getItems().addAll(GenerationType.values());
        generationTypeCb.setValue(GenerationType.SEQUENTIAL);

        goldNumbers = SatellitesCaFactory.getGoldNumbersintArrayList();
        satelliteNumbers = new ArrayList<>();
        for (int i = 0; i < goldNumbers.size(); i++) {
            satelliteNumbers.add("Satellite " + (i + 1));
        }
        partialCb.setVisible(false);
        generationTypeCb.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            partialCb.setVisible(newValue == GenerationType.PARALLEL);
        });
        choiceBox.getItems().addAll(satelliteNumbers);
        choiceBox.setValue(satelliteNumbers.get(0));
    }

    @FXML
    public void onGenerateButtonClick(ActionEvent event) {
        final GenerationType type = generationTypeCb.getValue();
        switch (type) {
            case SEQUENTIAL:
                generateSequential();
                break;
            case PARALLEL:
                generateParallel(partialCb.isSelected());
                break;
        }
    }

    private void generateParallel(boolean isPartial) {
        final int selectedSatelliteIndex = satelliteNumbers.indexOf(choiceBox.getValue());
        final int[] satelliteGoldCode = goldNumbers.get(selectedSatelliteIndex);
        // Ելքային արժեքների սինքրոնացման համար օգտագործվում է
        // syncArray զանգվածը, որը պահում է գեներացիայի քայլին համապատասխան
        // երկու ռեգիստրներից ստացած ելքային արժեքների գումարը ըստ մոդուլ երկուսի
        final double[] syncArray = new double[getOutputLength()];
        FibonacciParallelLFSR g1 = new FibonacciParallelLFSR(isPartial);
        FibonacciParallelLFSR g2 = new FibonacciParallelLFSR(isPartial);
        // Կանչվում է G1 և G2 ռեգիստրեների կողմից
        // գներացիան ավարետելուց հետո
        final Consumer<int[]> completeCallback = ignored -> {
            if (g1.isFinished() && g2.isFinished()) {
                outputTa.setText(Arrays.toString(syncArray)
                        .replace("]", "")
                        .replace("[", ""));
            }
        };
        // Կանչվում է G1 և G2 ռեգիստրեների կողմից
        // գներացիայի յուրաքանչյուր քայլում
        final BiConsumer<Integer, Integer> onEachGenerationCallback = (position, output) -> {
            int input = fromVoltage(syncArray[position]);
            int out = DataUtils.modAdd(input,output);
            syncArray[position] = toVoltage(out);
        };
        // G1 գեներատորին տրվում են նախնական պարամետրեր և սկսում գեներացիան
        g1.generate(new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, new int[]{3, 10}, new int[]{10}, 0,
                completeCallback,
                onEachGenerationCallback);
        // G2 գեներատորին տրվում են նախնական պարամետրեր և սկսում գեներացիան
        g2.generate(new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, new int[]{2, 3, 6, 8, 9, 10}, satelliteGoldCode, 0,
                completeCallback,
                onEachGenerationCallback);
    }

    private void generateSequential() {
        int selectionIndex = satelliteNumbers.indexOf(choiceBox.getValue());
        CaGenerator generator = new CaGenerator(goldNumbers.get(selectionIndex));
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < getOutputLength(); i++) {
            int output = generator.generate();
            builder.append(toVoltage(output))
                    .append(", ");
        }
        outputTa.setText(builder.toString());
    }

    private int getOutputLength() {
        String outputText = outputLengthTf.getText();
        int output = Utils.tryParseInt(outputText);
        if (output <= 0) {
            return 1024;
        }
        return output;
    }

    private double toVoltage(int output){
        return output == 0 ? -1.0 : 1.0;
    }

    private int fromVoltage(double output){
        return output == -1.0 ? 0 : 1;
    }
}
