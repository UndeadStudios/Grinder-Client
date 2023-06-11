package com.runescape.input;

import com.runescape.Client;
import com.runescape.clock.Time;
import com.runescape.io.packets.outgoing.PacketBuilder;

public class KeyRecorder {

    public static KeyRecorder instance;

    private static final boolean CAPTURE_KEYS = false;

    private int cyclesTillNextArrowKeyPressRecording;
    private long lastRecordedKeyTime;
    private boolean pressedArrowKey;

    public void onLogin(){
        lastRecordedKeyTime = 0L;
    }

    public void onCycle(Client client){

        if(!CAPTURE_KEYS)
            return;

        if(KeyHandler.__an_cl > 0){

            final PacketBuilder packet = new PacketBuilder(42);
            final long time = Time.currentTimeMillis();

            for(int i = 0; i < KeyHandler.__an_cl; i++){
                final long timePassed = Math.min(16777215L, time - lastRecordedKeyTime);
                lastRecordedKeyTime = time;
                packet.putReverseTriByte((int) timePassed);
                packet.putSignedBytePlus(KeyHandler.__an_cp[i]);
            }
        }

        if (cyclesTillNextArrowKeyPressRecording > 0)
            cyclesTillNextArrowKeyPressRecording--;

        if (KeyHandler.pressedKeys[96] || KeyHandler.pressedKeys[97] || KeyHandler.pressedKeys[98] || KeyHandler.pressedKeys[99])
            pressedArrowKey = true;

        if (pressedArrowKey && cyclesTillNextArrowKeyPressRecording <= 0) {
            cyclesTillNextArrowKeyPressRecording = 20;
            pressedArrowKey = false;
            final PacketBuilder packet = new PacketBuilder(100);
            packet.putUnsignedWordBigEndian(client.cameraPitchTarget);
            packet.putUnsignedWordA(client.minimapOrientation);
            client.sendPacket(packet);
        }
    }
}
