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
 * Saves the current state of the given cellular automata to a file.
 *
 * @author Thomas Neidhart
 */
public class SaveModelAction {

    private File file;

    public void setFile(File outputFile) {
        this.file = outputFile;
    }

    public File getFile() {
        return file;
    }

    public void execute(CellularAutomaton model) throws IOException {
        if (model == null) {
            throw new IllegalArgumentException("model is null.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getFile()))) {

            writer.write(String.format("%d\n", model.getRows()));
            writer.write(String.format("%d\n", model.getCols()));

            for (CellularAutomaton.Cell cell : model) {
                writer.write(cell.isAlive() ? "1" : "0");
            }
        }
    }
}
