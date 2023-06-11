package com.grinder.model;

import com.runescape.Client;
import com.runescape.cache.graphics.widget.Widget;

public class Spinner {

    public static boolean isSpinnerSpinning(boolean spinnerSpinning) {
        return spinnerSpinning;
    }

    public static void processSpinner(Client client) {
        if (!client.spinnerSpinning)
            return;

        Widget sprite = Widget.interfaceCache[34101];
        Widget container = Widget.interfaceCache[34200];
        if (sprite.horizontalOffset <= 0 && sprite.horizontalOffset >= -999) {
            sprite.horizontalOffset -= 25;
            container.horizontalOffset -= 25;
        } else if (sprite.horizontalOffset <= -1000 && sprite.horizontalOffset >= -1769) {
            sprite.horizontalOffset -= (25 / client.spinSpeed);
            container.horizontalOffset -= (25 / client.spinSpeed);
            client.spinSpeed = client.spinSpeed + 0.0627f;
        } else if (sprite.horizontalOffset <= -1770 && sprite.horizontalOffset >= -1926) {
            sprite.horizontalOffset -= (25 / client.spinSpeed);
            container.horizontalOffset -= (25 / client.spinSpeed);
            client.spinSpeed = client.spinSpeed + 0.14f;
        } else {
            client.spinnerSpinning = false;
        }
    }
}
