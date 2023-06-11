package com.runescape.cache.graphics.widget;

import com.grinder.ui.ClientUI;
import com.runescape.Client;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-27
 */
public enum WidgetAlignment {
    TOP_RIGHT{
        @Override
        int getXPosition(int width) {
            return ClientUI.screenAreaWidth - width;
        }

        @Override
        int getYPosition(int height) {
            return 0;
        }
    },
    TOP_LEFT {
        @Override
        int getXPosition(int width) {
            return 0;
        }

        @Override
        int getYPosition(int height) {
            return 0;
        }
    },
    BOTTOM_RIGHT {
        @Override
        int getXPosition(int width) {
            return ClientUI.screenAreaWidth - width;
        }

        @Override
        int getYPosition(int height) {
            return ClientUI.screenAreaHeight - getChatboxHeight() - height;
        }
    },
    BOTTOM_LEFT {
        @Override
        int getXPosition(int width) {
            return 0;
        }

        @Override
        int getYPosition(int height) {
            return ClientUI.screenAreaHeight - getChatboxHeight() - height;
        }
    };

    abstract int getXPosition(int width);
    abstract int getYPosition(int height);

    public int getChatboxHeight(){
        return Client.instance.resizeChatOffset();
    }
}
