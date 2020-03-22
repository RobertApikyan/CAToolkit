package sample.lfsrFastCorrelation;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import sample.diffCodes.Correlation;
import sample.enums.LfsrType;
import sample.toolkit.generators.DataUtils;
import sample.toolkit.polynomial.FibonacciLfsr;
import sample.toolkit.polynomial.GaloisLfsr;
import sample.toolkit.polynomial.base_paralle.ParallelLfsr;
import sample.toolkit.polynomial.parallel_fibonacci_lfsr.FibonacciParallelLFSR;
import sample.toolkit.polynomial.parallel_galois_lfsr.GaloisParallelLFSR;
import sample.toolkit.polynomial.polynomial_processor.Lfsr;

import static jdk.nashorn.internal.objects.NativeMath.max;
import static sample.utils.Utils.*;

public class LfsrFastCorrelationController {

    @FXML
    TextArea feedbackPosTa;

    @FXML
    TextArea lengthTa;

    @FXML
    ChoiceBox<LfsrType> lfsrTypeCb;

    @FXML
    TextArea resultTa;

    @FXML
    LineChart corrLc;

    public void initialize() {
        lfsrTypeCb.getItems().addAll(LfsrType.values());
        lfsrTypeCb.setValue(LfsrType.FIBONACCI);
    }

    public void generate() {
        int lfsrLength = tryParseInt(lengthTa.getText());
        int[] feedbackPositions = tryParseIntArray(feedbackPosTa.getText(), ",");
        LfsrType selectedLfsrType = lfsrTypeCb.getValue();

        resultTa.setText("");

        switch (selectedLfsrType) {
            case FIBONACCI:
                fastCorrelation(new FibonacciLfsr(feedbackPositions,lfsrLength),new FibonacciParallelLFSR(true),lfsrLength,feedbackPositions);
                break;
            case GALOIS:
                fastCorrelation(new GaloisLfsr(feedbackPositions,lfsrLength),new GaloisParallelLFSR(true),lfsrLength,feedbackPositions);
                break;
        }
    }

    private void fastCorrelation(Lfsr lfsr, ParallelLfsr parallelLfsr,int lfsrLength,int[] feedbackPositions) {

        int cycleLength = (int) (Math.pow(2, lfsrLength) - 1);

        double[] linearOutput = new double[cycleLength];

        for (int i = 0; i < cycleLength; i++) {
            linearOutput[i] = (double) lfsr.process();
        }

        int[] initialState = new int[lfsrLength];

        for (int i = 0; i < lfsrLength; i++) {
            initialState[i] = 1;
        }

        double[] parallelOutput = new double[cycleLength];

        parallelLfsr.generate(initialState,feedbackPositions,new int[]{lfsrLength},0,(values)->{
            double coefficient = Correlation.coefficient(linearOutput, parallelOutput);
            double[] crossValues = Correlation.crossValues(linearOutput, parallelOutput, 0, coefficient);

            double max = DataUtils.max(crossValues);

            resultTa.setText(String.valueOf(max));

            drawOnChart(corrLc,crossValues);
        },(step,value)-> parallelOutput[step] = value);
    }
}
