package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import sample.enums.CorrType;

import java.util.Arrays;

import static sample.diffCodes.Correlation.*;

import static sample.enums.CorrType.CIRCULAR;
import static sample.utils.Utils.drawOnChart;
import static sample.utils.Utils.toDoubleArray;

public class CorrelationController {

    @FXML
    TextArea sourceTa;

    @FXML
    TextArea sampleTa;

    @FXML
    Label resultLb;

    @FXML
    ChoiceBox<CorrType> corrTypeCb;

    @FXML
    LineChart corrLc;

    public void init(){
        corrTypeCb.getItems().addAll(CorrType.values());
        corrTypeCb.setValue(CIRCULAR);
    }

    @FXML
    public void correlationButton(){
        double[] source = toDoubleArray(sourceTa.getText());
        double[] sample = toDoubleArray(sampleTa.getText());
        double[] results = new double[]{};
        CorrType value = corrTypeCb.getValue();
        switch (value){
            case CIRCULAR:
                results = circularValues(source, sample, 0.0, coefficient(source, sample));
                break;
            case CROSS:
                results = crossValues(source, sample, 0.0, coefficient(source, sample));
                break;
        }

        drawOnChart(corrLc,results);

        Arrays.sort(results);
        resultLb.setText("Correlation= "+ results[results.length - 1]);
    }

}
