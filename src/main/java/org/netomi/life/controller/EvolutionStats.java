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

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

/**
 * A simple POJO class to collect statistics about the performed evolutions.
 *
 * @author Thomas Neidhart
 */
public class EvolutionStats {
    private long   evolutionCount;
    private double meanEvolutionInterval;

    private final  ReadOnlyStringWrapper text = new ReadOnlyStringWrapper(this, "text", toString());

    public long getEvolutionCount() {
        return evolutionCount;
    }

    public double getMeanEvolutionInterval() {
        return meanEvolutionInterval;
    }

    public void addEvolution(long evolutionDurationNanos) {
        meanEvolutionInterval = (meanEvolutionInterval * evolutionCount + evolutionDurationNanos / 1_000_000.0) / (evolutionCount + 1) ;
        evolutionCount++ ;
        text.set(toString());
    }

    public void reset() {
        evolutionCount = 0;
        meanEvolutionInterval = 0;
        text.set(toString());
    }

    public String getText() {
        return text.get();
    }

    public ReadOnlyStringProperty textProperty() {
        return text.getReadOnlyProperty() ;
    }

    @Override
    public String toString() {
        String interval = getMeanEvolutionInterval() == 0.0 ? "N/A" : String.format("%.3f milliseconds", getMeanEvolutionInterval());
        return String.format("Evolution: %d, average evolution interval: %s", getEvolutionCount(), interval);
    }
}
