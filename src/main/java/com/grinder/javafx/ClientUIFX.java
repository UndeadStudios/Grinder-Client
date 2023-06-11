package com.grinder.javafx;

import com.grinder.ui.ClientUI;
import com.grinder.GrinderScape;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 21/12/2019
 */
public class ClientUIFX extends ClientUI {

    @Override
    public void open(GrinderScape grinderScape) {
        super.open(grinderScape);
        FXUtil.createPanel();
    }


}
