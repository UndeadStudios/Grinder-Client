package com.grinder.javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 20/12/2019
 */
public class DevTool extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(createScene());
        primaryStage.show();
    }

    private static Scene createScene() {

        final StackPane root = new StackPane();
        final Scene scene = new Scene(root, 300, 800);
        scene.setFill(Color.BLACK);

        return scene;
    }
}
