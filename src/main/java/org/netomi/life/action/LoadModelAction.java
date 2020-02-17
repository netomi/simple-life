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
package org.netomi.life.action;

import org.netomi.life.model.CellularAutomaton;

import java.io.*;

/**
 * Loads a cellular automata model from the specified file.
 *
 * @author Thomas Neidhart
 */
public class LoadModelAction {

    private File file;

    public void setFile(File inputFile) {
        this.file = inputFile;
    }

    public File getFile() {
        return file;
    }

    public CellularAutomaton execute() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(getFile()))) {

            int rows = Integer.parseInt(reader.readLine());
            int cols = Integer.parseInt(reader.readLine());

            CellularAutomaton model = new CellularAutomaton(rows, cols);

            String state = reader.readLine();

            int idx = 0;
            for (CellularAutomaton.Cell cell : model) {
                boolean alive = state.charAt(idx++) == '1';
                cell.setAlive(alive);
            }

            return model;
        }
    }
}
