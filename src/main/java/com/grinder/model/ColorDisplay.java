package com.grinder.model;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 20/12/2019
 */
public class ColorDisplay extends Application {

    public static void main(String[] args) {


        launch(ColorDisplay.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        int[] u_colors = new int[] {
                2631712, 12047514, 11116512, 16533930, 6307904, 3153952, 9463856, 3684400, 8026240, 2629656, 5261376, 4735032, 8417352, 5785640, 3684392, 8555423, 14392, 7509687, 3159333, 7899240, 8400992, 8862056, 6312104, 2631752, 1585176, 11571288, 1644825, 6297728, 2105368, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3502602, 5793803, 7890955, 7121936, 8492337, 5591562, 1202241, 7763574, 5066061, 3026478, 15658734, 13752039, 13756374, 11639357, 13680756, 4008715, 6573598, 6638859, 6697728, 6702097, 13351542, 6573598, 6638859, 6697728, 197379, 5601177, 10048870, 5601177, 0, 0, 14269331, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6050884, 3692079, 4603956, 4090261, 4931092, 6050876, 3091231, 2583847, 3760661, 0, 0, 1121818, 0, 0, 0, 0, 0, 0, 0, 6710886, 3355443, 2829869, 6601589, 3362846, 7411036, 0, 7676450, 0, 0, 6303784, 3145728, 0, 12103832, 10854025, 9726723, 0, 0, 8550724, 0, 0, 0, 0, 0, 0, 0, 0, 12615752, 0, 11051160, 3942435, 14526173, 5284024, 22632, 4210752, 3684408, 3158064, 2631720, 2105376, 1579032
        };
        int[] o_colors = new int[] {
                11184810, 4473924, 6710886, 8947848, 0, 0, 0, 5987163, 16777205, 5263440, 2236962, 0, 10040115, 4008715, 4008715, 6697728, 6697728, 204, 0, 0, 10053120, 7166763, 7166763, 0, 13351542, 8550724, 0, 0, 3502602, 15658734, 6638859, 0, 13229559, 5263440, 0, 6702097, 0, 5601177, 0, 4671907, 4153729, 16711935, 16711935, 5263440, 0, 0, 0, 2631712, 2631712, 3153952, 16711935, 0, 16711935, 16711935, 16711935, 6316120, 16711935, 4008715, 16711935, 8544278, 16711935, 16711935, 14392, 16711935, 16711935, 7899240, 16711935, 6037591, 16711935, 8862056, 16711935, 6684672, 16711935, 3846161, 16711935, 12889166, 12031847, 4206608, 9930575, 8215339, 5259296, 6050884, 4603956, 0, 3159333, 2949120, 4341619, 5524791, 6178332, 3025184, 5259296, 16711935, 3160120, 4801090, 51344, 5795952, 65280, 16711935, 2631720, 16711935, 5521943, 3159333, 5249088, 8635338, 6710886, 6707515, 3820341, 4206608, 13229561, 3684384, 16711935, 16711935, 16711935, 4208656, 1579024, 0, 5197647, 0, 6050876, 3091231, 0, 0, 12103832, 0, 0, 0, 4210744, 7841669, 0, 8570040, 2565904, 3617321, 3160104, 4210756, 16711935, 16711935, 7629129, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7509687, 16711935, 16711935, 16711935, 16711935, 4491374, 16430903, 4293295, 3942435, 14526173, 16711935, 4412737, 7224879, 5263472, 0, 16448069, 10930884, 13269961, 8697796, 0, 7893088, 7891794, 7891016, 0
        };
        final HBox splitBox = new HBox();
        final VBox leftBox = new VBox();
        final VBox rightBox = new VBox();
        final ScrollPane pane = new ScrollPane();
        pane.setContent(splitBox);
        splitBox.getChildren().add(leftBox);
        splitBox.getChildren().add(rightBox);

        final Scene scene = new Scene(pane, 40, 600);
        primaryStage.setScene(scene);
        for (int i = 0; i < u_colors.length; i++) {
            int color = u_colors[i];

            double r = (color >> 16 & 0xff) / 256.0;
            double g = (color >> 8 & 0xff) / 256.0;
            double b = (color & 0xff) / 256.0;

            Color col = Color.color(r, g, b);

            final Rectangle rectangle = new Rectangle(20, 20);
            rectangle.setFill(col);
            final Label label = new Label(""+i);
            final HBox vBox = new HBox();
            vBox.getChildren().addAll(rectangle, label);

            leftBox.getChildren().add(vBox);
        }
        for (int i = 0; i < o_colors.length; i++) {
            int color = o_colors[i];

            double r = (color >> 16 & 0xff) / 256.0;
            double g = (color >> 8 & 0xff) / 256.0;
            double b = (color & 0xff) / 256.0;

            Color col = Color.color(r, g, b);

            final Rectangle rectangle = new Rectangle(20, 20);
            rectangle.setFill(col);
            final Label label = new Label(""+i);
            final HBox vBox = new HBox();
            vBox.getChildren().addAll(rectangle, label);

            rightBox.getChildren().add(vBox);
        }
        primaryStage.show();
    }

    public static int[] getRGB(final String rgb)
    {
        final int[] ret = new int[3];
        for (int i = 0; i < 3; i++)
        {
            ret[i] = Integer.parseInt(rgb.substring(i * 2, i * 2 + 2), 16);
        }
        return ret;
    }
}
