package com.grinder.javafx;

import com.grinder.javafx.fxml.DevToolController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javax.swing.*;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 21/12/2019
 */
public class DevToolFX {

    public static DevToolController devToolController;

    public static void launchDevTool(JFrame window){
        Platform.runLater(() -> {

            try {
                FXMLLoader loader = new FXMLLoader(DevTool.class.getResource("fxml/DevView.fxml"));
                Parent root = loader.load();
                FXUtil.fxPanel.setScene(new Scene(root, 300, 765));
                // Give the controller access to the main app
                DevToolController controller = loader.getController();

                SwingUtilities.invokeLater(() -> {
                    devToolController = controller;
                    window.add(FXUtil.fxPanel);
                    window.pack();
                    window.setVisible(true);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
