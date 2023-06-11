package com.grinder.client.util;

import net.runelite.client.rs.RSAppletStub;

import java.applet.Applet;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 11/12/2019
 */
public class ClientLoader {

    private static Applet loadFromClass(final Class<?> clientClass) throws IllegalAccessException, InstantiationException
    {
        final Applet rs = (Applet) clientClass.newInstance();
        rs.setStub(new RSAppletStub());
        return rs;
    }

}
