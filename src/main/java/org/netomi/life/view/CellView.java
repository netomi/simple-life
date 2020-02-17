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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.Pane;
import org.netomi.life.model.CellularAutomaton;

/**
 * The view to display the state of an individual cell within a
 * cellular automata.
 *
 * @author Thomas Neidhart
 */
public class CellView extends Pane {

    private final CellularAutomaton.Cell cell;

    private final BooleanProperty aliveProperty = new SimpleBooleanProperty(false);

    public CellView(CellularAutomaton.Cell cell) {
        this.cell = cell;

        getStyleClass().add("cell");

        setMinSize(3, 3);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        aliveProperty.bindBidirectional(cell.aliveProperty());

        if (aliveProperty.get()) {
            getStyleClass().add("cell-alive");
        }

        aliveProperty.addListener((observable, oldValue, newValue) -> {
            getStyleClass().remove("cell-alive");
            if (newValue) {
                getStyleClass().add("cell-alive");
            }
        });
    }

    public int getRow() {
        return cell.getRow();
    }

    public int getColumn() {
        return cell.getColumn();
    }

    public boolean getAlive() {
        return aliveProperty.get();
    }

    public void setAlive(boolean alive) {
        aliveProperty.set(alive);
    }

}
