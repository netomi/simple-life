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
package org.netomi.life.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import org.netomi.life.model.CellularAutomaton;

import java.util.ArrayList;
import java.util.List;

/**
 * The main grid view to visualize the state of a cellular automata.
 *
 * @author Thomas Neidhart
 */
public class AutomatonView extends GridPane {

    private ObjectProperty<CellularAutomaton> modelProperty = new SimpleObjectProperty<>();

    private       CellView[][]  cellGrid;
    private final MouseGestures mouseGestures = new MouseGestures();

    public AutomatonView() {
        getStyleClass().add("grid");

        setPadding(new Insets(2, 2, 2, 2));

//        setHgap(2);
//        setVgap(2);

        modelProperty.addListener((observable, oldValue, newValue) -> updateModel());

        mouseGestures.setupGestures();
    }

    public ObjectProperty<CellularAutomaton> modelProperty() {
        return modelProperty;
    }

    public CellularAutomaton getModel() {
        return modelProperty.get();
    }

    public void resetGrid() {
        getModel().clear();
    }

    private void updateModel() {
        getChildren().clear();
        getRowConstraints().clear();
        getColumnConstraints().clear();

        CellularAutomaton model = getModel();

        cellGrid = new CellView[model.getRows()][model.getCols()];

        for (CellularAutomaton.Cell cell : model) {
            CellView cellView = new CellView(cell);
            add(cellView, cell.getColumn(), cell.getRow());

            cellGrid[cell.getRow()][cell.getColumn()] = cellView;
        }

        for (int i = 0; i < model.getRows(); i++) {
            RowConstraints row = new RowConstraints(3, 100, Double.MAX_VALUE);
            row.setVgrow(Priority.ALWAYS);
            getRowConstraints().add(row);
        }

        for (int i = 0; i < model.getCols(); i++) {
            ColumnConstraints col = new ColumnConstraints(3, 100, Double.MAX_VALUE);
            col.setHgrow(Priority.ALWAYS);
            getColumnConstraints().add(col);
        }
    }

    private class MouseGestures {

        private MouseEvent lastMouseEventWhenDragging  = null;
        private CellView   lastEnteredCellWhenDragging = null;

        private void setupGestures() {

            // Set alive status for each cell depending on the clicked button.
            setOnMousePressed(event -> {
                Node node = event.getPickResult().getIntersectedNode();
                if (node instanceof  CellView) {
                    updateCell((CellView) node, event);
                }
            });

            setOnDragDetected(event      -> AutomatonView.this.startFullDrag());
            setOnMouseDragReleased(event -> resetDragState());
            setOnMouseDragExited(event   -> resetDragState());

            setOnMouseDragged(event -> {
                if (!event.isPrimaryButtonDown() && !event.isSecondaryButtonDown()) {
                    return;
                }

                Node node = event.getPickResult().getIntersectedNode();
                if (node instanceof CellView) {
                    CellView cellView = (CellView) node;

                    updateNonEnteredCells(cellView, event);
                    updateCell(cellView, event);

                    lastMouseEventWhenDragging  = event;
                    lastEnteredCellWhenDragging = cellView;
                } else {
                    resetDragState();
                }
            });
        }

        private void resetDragState() {
            lastMouseEventWhenDragging  = null;
            lastEnteredCellWhenDragging = null;
        }

        private void updateCell(CellView cellView, MouseEvent mouseEvent) {
            if (mouseEvent.isPrimaryButtonDown()) {
                cellView.setAlive(true);
            } else if (mouseEvent.isSecondaryButtonDown()) {
                cellView.setAlive(false);
            }
        }

        private void updateNonEnteredCells(CellView currentCell, MouseEvent currentEvent) {
            if (lastMouseEventWhenDragging  == null ||
                lastEnteredCellWhenDragging == null) {
                return;
            }

            Point2D lastPoint =
                    new Point2D(lastMouseEventWhenDragging.getSceneX(),
                                lastMouseEventWhenDragging.getSceneY());

            Point2D currPoint =
                    new Point2D(currentEvent.getSceneX(),
                                currentEvent.getSceneY());

            List<CellView> cellsInBoundingBox = new ArrayList<>();

            int minRow = Math.min(lastEnteredCellWhenDragging.getRow(), currentCell.getRow());
            int minCol = Math.min(lastEnteredCellWhenDragging.getColumn(), currentCell.getColumn());
            int maxRow = Math.max(lastEnteredCellWhenDragging.getRow(), currentCell.getRow());
            int maxCol = Math.max(lastEnteredCellWhenDragging.getColumn(), currentCell.getColumn());

            for (int i = minRow; i <= maxRow; i++) {
                for (int j = minCol; j <= maxCol; j++) {
                    cellsInBoundingBox.add(cellGrid[i][j]);
                }
            }

            for (CellView cellView : cellsInBoundingBox) {
                Point2D lastPointLocal = cellView.sceneToLocal(lastPoint);
                Point2D currPointLocal = cellView.sceneToLocal(currPoint);

                if (lineRectIntersect(lastPointLocal, currPointLocal, cellView.getBoundsInLocal())) {
                    updateCell(cellView, currentEvent);
                }
            }
        }

        private boolean lineRectIntersect(Point2D startPoint, Point2D endPoint, Bounds rect) {
            // check if the line has hit any of the rectangle's sides
            // uses the Line/Line function below
            double x1 = startPoint.getX();
            double y1 = startPoint.getY();

            double x2 = endPoint.getX();
            double y2 = endPoint.getY();

            double rx = rect.getMinX();
            double ry = rect.getMinY();

            double rh = rect.getHeight();
            double rw = rect.getWidth();

            boolean left   = lineLineIntersect(x1,y1,x2,y2, rx,ry,rx, ry+rh);
            boolean right  = lineLineIntersect(x1,y1,x2,y2, rx+rw,ry, rx+rw,ry+rh);
            boolean top    = lineLineIntersect(x1,y1,x2,y2, rx,ry, rx+rw,ry);
            boolean bottom = lineLineIntersect(x1,y1,x2,y2, rx,ry+rh, rx+rw,ry+rh);

            // if ANY of the above are true, the line
            // has hit the rectangle
            if (left || right || top || bottom) {
                return true;
            }
            return false;
        }

        boolean lineLineIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
            // calculate the direction of the lines
            double delta = (y4-y3)*(x2-x1) - (x4-x3)*(y2-y1);

            double uA = ((x4-x3)*(y1-y3) - (y4-y3)*(x1-x3)) / delta;
            double uB = ((x2-x1)*(y1-y3) - (y2-y1)*(x1-x3)) / delta;

            // if uA and uB are between 0-1, lines are colliding
            if (uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1) {
                return true;
            } else {
                return false;
            }
        }

    }
}
