package sample.controllers;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import sample.diffCodes.CodeIndividual;
import sample.diffCodes.CodesPopulation;
import sample.diffCodes.FileUtils;
import sample.diffCodes.GeneticAlgorithm;

import java.io.File;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class GaController {

    private ThreadPoolExecutor executor =
            new ThreadPoolExecutor(4, 4, 1000, TimeUnit.MILLISECONDS,
                    new PriorityBlockingQueue<>());

    @FXML
    TextField populationSizeTf;

    @FXML
    TextField mutationRateTf;

    @FXML
    TextField crossoverRateTf;

    @FXML
    TextField elitismCountTf;

    @FXML
    TextField singleCodeLengthTf;

    @FXML
    TextField codesCountTf;

    @FXML
    TextField effectiveGenerationOffsetTf;

    @FXML
    Label generationStepLb;

    @FXML
    TextArea outputTa;

    @FXML
    Label generationFitnessLb;

    @FXML
    TextField fileNameTf;

    @FXML
    Button generateButton;

    @FXML
    Button fileLocationBtn;

    @FXML
    Label fileLocationLb;

    private Stage stage;

    private File outputDirectory;

    @FXML
    private void onPickOutputFileLocation(ActionEvent _) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            outputDirectory = selectedDirectory;
            fileLocationLb.setText(outputDirectory.getAbsolutePath());
        }

        generateButton.setVisible(selectedDirectory != null);
    }

    @FXML
    private void onGenerateClick() {
        executor.execute(() -> {
            int populationSize = tryParseToInt(populationSizeTf.getText());

            double mutationRate = tryParseToDouble(mutationRateTf.getText());

            double crossoverRate = tryParseToDouble(crossoverRateTf.getText());

            int elitismCount = tryParseToInt(elitismCountTf.getText());
            int singleCodeLength = tryParseToInt(singleCodeLengthTf.getText());

            int codesCount = tryParseToInt(codesCountTf.getText());

            int effectiveGenerationOffset = tryParseToInt(effectiveGenerationOffsetTf.getText());

            int generation = 0;

            printGenerationNumber(generation);

            GeneticAlgorithm ga = new GeneticAlgorithm(populationSize, mutationRate, crossoverRate, elitismCount, effectiveGenerationOffset);

            CodesPopulation population = ga.initPopulation(singleCodeLength, codesCount);

            ga.evaluate(population);

            clearConsole();

            while (!ga.willTerminate(population)) {

                printGenerationNumber(generation);

                CodeIndividual fittest = population.getFittest(0);

                printFitnessValue(fittest.getFitness());

                population = ga.crossover(population);

                population = ga.mutate(population);

                ga.evaluate(population);

                generation++;
            }

            FileUtils.writeCodes(population.getFittest(0),fileLocationLb.getText(),fileNameTf.getText());

            printToConsole("Found solution in " + generation + " generationNumber");
            printToConsole("Best solution: " + population.getFittest(0).toString());
        });
    }

    private void printFitnessValue(double fitness) {
        Platform.runLater(() -> generationFitnessLb.setText("Fitness= " + fitness));
    }

    private void printGenerationNumber(int generationNumber) {
        Platform.runLater(() -> generationStepLb.setText("Generation= " + generationNumber));
    }

    private void printToConsole(String text) {
        Platform.runLater(() -> outputTa.setText(outputTa.getText() + "\n" + text));
    }

    private void clearConsole() {
        Platform.runLater(() -> outputTa.setText(""));
    }

    private int tryParseToInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ignored) {
            return 0;
        }
    }

    private double tryParseToDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception ignored) {
            return 0;
        }
    }

    public void setStage(Stage primaryStage) {
        this.stage = primaryStage;
    }
}
