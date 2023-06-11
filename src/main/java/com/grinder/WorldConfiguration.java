package com.grinder;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since 20/01/2021
 */
public final class WorldConfiguration {

    private final int port;
    private final String address;
    private final String name;

    public WorldConfiguration(String address, String name, int port) {
        this.port = port;
        this.address = address;
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }
}
