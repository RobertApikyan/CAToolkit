package sample.controllers;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import sample.caGlobalGeneration.CaGlobalGenerationProgram;
import sample.caGenerator.CaProgram;
import sample.correlationProgram.CorrelationProgram;
import sample.gaGenerator.GaProgram;
import sample.lfsrFastCorrelation.LfsrFastCorrelation;
import sample.lfsrGenerator.LfsrGeneratorProgram;
import sample.lfsrStatePath.LfsrStatePathProgram;

public class LauncherController  {

    @FXML
    void caGlobalProgramBtn() {
        start(new CaGlobalGenerationProgram());
    }

    @FXML
    void caProgramBtn() {
        start(new CaProgram());
    }

    @FXML
    void correlationBtn() {
        start(new CorrelationProgram());
    }

    @FXML
    void gaProgramBtn() {
        start(new GaProgram());
    }

    @FXML
    void lfsrFastCorrelationBtn() {
        start(new LfsrFastCorrelation());
    }

    @FXML
    void lfsrGeneratorProgramBtn() {
        start(new LfsrGeneratorProgram());
    }

    @FXML
    void lfsrStatePathProgram() {
        start(new LfsrStatePathProgram());
    }

    private void start(Application application){
        try {
            application.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
