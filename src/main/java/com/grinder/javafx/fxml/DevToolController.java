package com.grinder.javafx.fxml;

import com.grinder.model.Snow;
import com.runescape.Client;
import com.runescape.audio.Audio;
import com.runescape.cache.def.FloorDefinition;
import com.runescape.entity.model.Model;
import com.runescape.scene.SceneGraph;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 20/12/2019
 */
public class DevToolController implements Initializable {

    public ListView<FloorListItem> uFloorList;
    public ListView<FloorListItem> oFloorList;
    public Slider widthSlider;
    public Slider heightSlider;
    public Slider offXSlider;
    public Slider offYSlider;
    public Slider zoomSlider;
    public Slider rot1Slider;
    public Slider rot2Slider;
    public ToggleButton toggleCutScene;
    public Slider xCoordSlider, yCoordSlider, hSlider, camSpeedSlider, camAngleSlider, camXSlider, camYSlider, camZSlider;
    public TextArea codeArea;
    public TextArea infoTextArea;
    public Slider tileDistanceSlider;
    public Slider modelDistanceSlider;
    public TextField soundIdField;
    public ListView areaSoundWrappers;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        toggleCutScene.selectedProperty().addListener((observable, oldValue, newValue) -> {
            SwingUtilities.invokeLater(() -> Client.instance.isCameraLocked = newValue);
            updateCodeArea();
        });
        xCoordSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            SwingUtilities.invokeLater(() -> Client.instance.x = newValue.intValue());
            updateCodeArea();
        });
        yCoordSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            SwingUtilities.invokeLater(() -> Client.instance.y = newValue.intValue());
            updateCodeArea();
        });
        hSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            SwingUtilities.invokeLater(() -> Client.instance.height = newValue.intValue());
            updateCodeArea();
        });
        camSpeedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            SwingUtilities.invokeLater(() -> Client.instance.speed = newValue.intValue());
            updateCodeArea();
        });
        camAngleSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            SwingUtilities.invokeLater(() -> Client.instance.angle = newValue.intValue());
            updateCodeArea();
        });
        camXSlider.setValue(Client.instance.cameraX);
        camXSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            SwingUtilities.invokeLater(() -> Client.instance.cameraX = newValue.intValue());
            updateCodeArea();
        });
        camYSlider.setValue(Client.instance.cameraZ);
        camYSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            SwingUtilities.invokeLater(() -> Client.instance.cameraZ = newValue.intValue());
            updateCodeArea();
        });
        camZSlider.setValue(Client.instance.cameraY);
        camZSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            SwingUtilities.invokeLater(() -> Client.instance.cameraY = newValue.intValue());
            updateCodeArea();
        });
        tileDistanceSlider.setValue(SceneGraph.TILE_DRAW_DISTANCE);
        tileDistanceSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            SwingUtilities.invokeLater(() -> {
                SceneGraph.TILE_DRAW_DISTANCE = newValue.intValue();
                SceneGraph.rebuildVisibilityMap();
            });
        });
        modelDistanceSlider.setValue(Model.MODEL_DRAW_DISTANCE);
        modelDistanceSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            SwingUtilities.invokeLater(() -> {
                Model.MODEL_DRAW_DISTANCE = newValue.intValue();
                System.out.println("Model.MODEL_DRAW_DISTANCE = "+newValue.intValue());
            });
        });
    }


    private void updateCodeArea(){
        codeArea.setText(
                "player.packetSender.sendCameraPos("
                        + (int) camXSlider.getValue()+", "
                        + (int) camYSlider.getValue()+", "
                        + (int) camZSlider.getValue()
                        + ")"
                        + "\n" +
                "player.packetSender.sendCameraAngle("
                        + (int) xCoordSlider.getValue()+", "
                        + (int) yCoordSlider.getValue()+", "
                        + (int) hSlider.getValue()+", "
                        + (int) camSpeedSlider.getValue()+", "
                        + (int) camAngleSlider.getValue()
                        + ")");
    }


    public void populate(FloorDefinition[] overlays, FloorDefinition[] underlays){

        final ObservableList<Color> oColorPalette = FXCollections.observableArrayList();
        for(FloorDefinition overlay : overlays){
            int color = overlay.rgb;

            double r = (color >> 16 & 0xff) / 256.0;
            double g = (color >> 8 & 0xff) / 256.0;
            double b = (color & 0xff) / 256.0;
            Color col = Color.color(r, g, b);
            oColorPalette.add(col);
        }

        for (int i = 0; i < overlays.length; i++) {
            oFloorList.getItems().add(new FloorListItem(i, overlays[i], oColorPalette));
        }

        final ObservableList<Color> uColorPalette = FXCollections.observableArrayList();
        for(FloorDefinition underlay : underlays){
            int color = underlay.rgb;

            double r = (color >> 16 & 0xff) / 256.0;
            double g = (color >> 8 & 0xff) / 256.0;
            double b = (color & 0xff) / 256.0;
            Color col = Color.color(r, g, b);
            uColorPalette.add(col);
        }
        for (int i = 0; i < underlays.length; i++) {
            uFloorList.getItems().add(new FloorListItem(i, underlays[i], uColorPalette));
        }

        widthSlider.setValue(Snow.interfaceWidth);
        widthSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            SwingUtilities.invokeLater(() -> {
                Snow.interfaceWidth = newValue.intValue();
            });
        });

        heightSlider.setValue(Snow.interfaceHeight);
        heightSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            SwingUtilities.invokeLater(() -> {
                Snow.interfaceHeight = newValue.intValue();
            });
        });

        offXSlider.setValue(Snow.childWith);
        offXSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            SwingUtilities.invokeLater(() -> {
                Snow.childWith = newValue.intValue();
            });
        });
        offYSlider.setValue(Snow.childHeight);
        offYSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            SwingUtilities.invokeLater(() -> {
                Snow.childHeight = newValue.intValue();
            });
        });

        zoomSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            SwingUtilities.invokeLater(() -> {
                Snow.modelZoom = newValue.intValue();
            });
        });

        rot1Slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            SwingUtilities.invokeLater(() -> {
                Snow.modelRotation1 = newValue.intValue();
            });
        });
        rot2Slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            SwingUtilities.invokeLater(() -> {
                Snow.modelRotation2 = newValue.intValue();
            });
        });
    }

    public void playSound(ActionEvent event) {
        final int soundId = Integer.parseInt(soundIdField.getText().trim());
        SwingUtilities.invokeLater(() -> {
            Audio.queueSoundEffect(soundId, 100, 0);
        });
    }

    class FloorListItem extends HBox {
        final int index;
        final FloorDefinition floorDefinition;

        public FloorListItem(int index, FloorDefinition floorDefinition, ObservableList<Color> colorPalette) {
            this.index = index;
            this.floorDefinition = floorDefinition;
            final ColorPicker colorPicker = new ColorPicker();

            int color = floorDefinition.rgb;

            double r = (color >> 16 & 0xff) / 256.0;
            double g = (color >> 8 & 0xff) / 256.0;
            double b = (color & 0xff) / 256.0;
            Color col = Color.color(r, g, b);
            colorPicker.setValue(col);
            colorPicker.getCustomColors().setAll(colorPalette);
            colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
                SwingUtilities.invokeLater(() -> {
                    floorDefinition.rgbToHsl(newValue.getRed(), newValue.getBlue(), newValue.getGreen());
                    Client.instance.reloadRegion = true;
                });
            });
            final Button resetButton = new Button("Reset");
            getChildren().add(new Label(""+index));
            getChildren().add(colorPicker);
            getChildren().add(resetButton);
            resetButton.setOnAction(event -> {
                colorPicker.valueProperty().set(Color.color(r, g, b));
            });
        }
    }
}
