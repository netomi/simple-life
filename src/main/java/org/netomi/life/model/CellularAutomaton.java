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
package org.netomi.life.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;

/**
 * The cellular automata model class.
 *
 * @author Thomas Neidhart
 */
public class CellularAutomaton implements Iterable<CellularAutomaton.Cell> {

    private final int rows;
    private final int cols;

    private final BitSet cellState;
    private final BitSet updatedCellState;

    private final int cellCount;
    private final Cell[] cells;

    private int generation;

    public CellularAutomaton(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;

        this.cellCount = rows * cols;

        cellState        = new BitSet(cellCount);
        updatedCellState = new BitSet(cellCount);

        cells = new Cell[cellCount];
        for (int i = 0; i < cellCount; i++) {
            cells[i] = new Cell(i);
        }

        generation = 0;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getGeneration() {
        return generation;
    }

    public Cell getCell(int row, int col) {
        int index = row * this.cols + col;
        return cells[index];
    }

    @Override
    public Iterator<Cell> iterator() {
        return Arrays.asList(cells).iterator();
    }

    public void evolve() {
        BitSet tmp = new BitSet(cellCount);
        for (int i = 0; i < cellCount; i++) {
            Cell cell = cells[i];

            boolean nextState = cell.evolve(tmp);
            updatedCellState.set(i, nextState);
        }

        generation++;

        cellState.clear();
        cellState.or(updatedCellState);

        for(int i = 0; i < cellCount; i++) {
            cells[i].setAlive(cellState.get(i));
        }
    }

    public void clear() {
        this.generation = 0;

        for (Cell cell : cells) {
            cell.setAlive(false);
        }

        updatedCellState.clear();
    }

    public class Cell {
        private final int    cellIndex;
        private final BitSet neighbors;

        private final BooleanProperty aliveProperty;

        Cell(int cellIndex) {
            this.cellIndex = cellIndex;
            this.neighbors = new BitSet(cellCount);

            int row = cellIndex / cols;
            int col = cellIndex % cols;

            addNeighbor(row - 1, col); // top
            addNeighbor(row + 1, col); // bottom
            addNeighbor(row, col - 1); // left
            addNeighbor(row, col + 1); // right
            addNeighbor(row - 1, col - 1); // top left
            addNeighbor(row - 1, col + 1); // top right
            addNeighbor(row + 1, col - 1); // bottom left
            addNeighbor(row + 1, col + 1); // bottom right

            aliveProperty = new SimpleBooleanProperty(false) {
                @Override
                public void set(boolean newValue) {
                    super.set(newValue);
                    cellState.set(cellIndex, newValue);
                }
            };
        }

        public int getRow() {
            return cellIndex / cols;
        }

        public int getColumn() {
            return cellIndex % cols;
        }

        private void addNeighbor(int row, int col) {
            if (row >= 0 && row < rows &&
                col >= 0 && col < cols) {

                int cellIndex = row * cols + col;
                neighbors.set(cellIndex);
            }
        }

        public void setAlive(boolean value) {
            aliveProperty.set(value);
        }

        public boolean isAlive() {
            return aliveProperty.get();
        }

        public BooleanProperty aliveProperty() {
            return aliveProperty;
        }

        public boolean evolve(BitSet working) {
            working.clear();
            working.or(cellState);
            working.and(neighbors);

            int aliveNeighbors = working.cardinality();

            if (isAlive()) {
                return aliveNeighbors == 2 || aliveNeighbors == 3;
            } else {
                return aliveNeighbors == 3;
            }
        }
    }
}
