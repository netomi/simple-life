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
package org.netomi.life;

import com.gluonhq.ignite.guice.GuiceContext;
import com.google.inject.AbstractModule;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.netomi.life.service.ModelService;
import org.netomi.life.service.ModelServiceImpl;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * A simple Conway's Game of Life application.
 * <p>
 * It allows to visualize and simulate a cellular automata.
 *
 * @author Thomas Neidhart
 */
public class LifeApplication extends Application {

    private GuiceContext context = new GuiceContext(this, () -> Arrays.asList(new AppModule()));

    @Inject private FXMLLoader fxmlLoader;

    @Override
    public void start(Stage primaryStage) throws Exception {
        context.init();

        Parent root = fxmlLoader.load(getClass().getResourceAsStream("/main.fxml"));

        Scene scene = new Scene(root, 800, 600);

        scene.getStylesheets().add(getClass().getResource("/app.css").toExternalForm());

        primaryStage.setTitle("Simple Life");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static class AppModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(ModelService.class).to(ModelServiceImpl.class);
        }

    }

}
