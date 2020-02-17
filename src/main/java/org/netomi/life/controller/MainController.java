/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.netomi.life.controller;

import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.netomi.life.action.LoadModelAction;
import org.netomi.life.action.SaveModelAction;
import org.netomi.life.model.CellularAutomaton;
import org.netomi.life.service.ModelService;
import org.netomi.life.view.AutomatonView;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * The controller for the simple life app.
 *
 * @author Thomas Neidhart
 */
public class MainController implements Initializable {

    @FXML
    private Button loadButton;

    @FXML
    private Button saveButton;

    @FXML
    private ToggleButton controlToggleButton;

    @FXML
    private ToggleButton toggleSimulationButton;

    @FXML
    private Slider rowSlider;

    @FXML
    private Slider colSlider;

    @FXML
    private Slider speedSlider;

    @FXML
    private GridPane divider;

    @FXML
    private AutomatonView modelGrid;

    @FXML
    private Label statusLabel;

    @Inject
    private ModelService modelService;

    private AnimationTimer simulationTimer;
    private EvolutionStats evolutionStats;

    private final DoubleProperty speedProperty = new SimpleDoubleProperty(1.0);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        simulationTimer = createSimulationTimer();

        rowSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                updateModel();
            }
        });

        colSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                updateModel();
            }
        });

        modelGrid.modelProperty().bind(modelService.modelProperty());
        updateModel();

        evolutionStats = new EvolutionStats();

        statusLabel.textProperty().bind(evolutionStats.textProperty());
        speedProperty.bind(speedSlider.valueProperty());
    }

    public void toggleControls(ActionEvent actionEvent) {
        if (controlToggleButton.isSelected()) {
            divider.getColumnConstraints().get(1).setPrefWidth(Control.USE_COMPUTED_SIZE);
        }
        else {
            divider.getColumnConstraints().get(1).setPrefWidth(0.0);
        }
    }

    public void toggleSimulation(ActionEvent actionEvent) {
        if (toggleSimulationButton.isSelected()) {
            toggleSimulationButton.setText("Stop evolution");
            simulationTimer.start();
        } else {
            toggleSimulationButton.setText("Start evolution");
            simulationTimer.stop();
        }
    }

    public void resetGrid(ActionEvent actionEvent) {
        modelGrid.resetGrid();
        evolutionStats.reset();
    }

    private void updateModel() {
        modelService.initModel((int) rowSlider.getValue(), (int) colSlider.getValue());
    }

    private AnimationTimer createSimulationTimer() {
        final LongProperty lastUpdateTime = new SimpleLongProperty(0);
        final AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long timestamp) {
                if (lastUpdateTime.get() > 0) {
                    long elapsedTime = timestamp - lastUpdateTime.get();
                    double ems = elapsedTime / 1e6;
                    double val = 1000.0 / speedProperty.doubleValue();
                    if (ems > val) {
                        evolveAutomaton();
                        lastUpdateTime.set(timestamp);
                        evolutionStats.addEvolution(elapsedTime);
                    }
                } else {
                    lastUpdateTime.set(timestamp);
                }
            }

        };
        return timer;
    }

    private void evolveAutomaton() {
        modelService.getModel().evolve();
    }

    public void saveModel(ActionEvent actionEvent) {
        try {
            FileChooser chooser = new FileChooser();

            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("GOL files (*.gol)", "*.gol");
            chooser.getExtensionFilters().add(extFilter);

            chooser.setTitle("Save Model");
            File file = chooser.showSaveDialog(saveButton.getScene().getWindow());

            if (file != null) {
                SaveModelAction action = new SaveModelAction();
                action.setFile(file);
                action.execute(modelService.getModel());
            }
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Saving model to file failed!", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void loadModel(ActionEvent actionEvent) {
        try {
            FileChooser chooser = new FileChooser();

            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("GOL files (*.gol)", "*.gol");
            chooser.getExtensionFilters().add(extFilter);

            chooser.setTitle("Load Model");
            File file = chooser.showOpenDialog(loadButton.getScene().getWindow());

            if (file != null) {
                LoadModelAction action = new LoadModelAction();
                action.setFile(file);
                CellularAutomaton model = action.execute();
                modelService.setModel(model);

                rowSlider.setValue(model.getRows());
                colSlider.setValue(model.getCols());
            }
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Loading model from file failed!", ButtonType.OK);
            alert.showAndWait();
        }
    }
}
