package com.runescape.audio;

import com.runescape.cache.AbstractIndexCache;
import com.runescape.cache.ByteArrayNode;
import com.runescape.collection.Node;
import com.runescape.collection.NodeHashTable;
import com.runescape.io.Buffer;

/**
 * @version 1.0
 * @since 13/02/2020
 */
public class MusicTrack extends Node {

    public static final int HEADER_CHUNK_TYPE = 1297377380;
    public static final int TRACK_CHUNK_TYPE = 1297379947;
    public static final int NOTE_ON_STATUS_BYTE = 0b1001_0000;
    public static final int NOTE_OFF_STATUS_BYTE = 0b1000_0000;
    public static final int CONTROLLER_CHANGE_STATUS_BYTE = 0b1011_0000;
    public static final int BANK_SELECT_CONTROLLER = 0;
    public static final int MODULATION_WHEEL = 1;
    public static final int LSB_MODULATION_WHEEL = 33;
    public static final int MAIN_VOLUME = 7;
    public static final int LSB_MAIN_VOLUME = 39;
    public static final int PAN = 10;
    public static final int LSB_PAN = 42;
    public static final int NON_REGISTERED_PARAM_NUMBER_LSB_2 = 99;
    public static final int NON_REGISTERED_PARAM_NUMBER_LSB_1 = 98;
    public static final int REGISTERED_PARAM_NUMBER_LSB_2 = 101;
    public static final int REGISTERED_PARAM_NUMBER_LSB_1 = 100;
    public static final int DAMPER_PEDAL = 64;
    public static final int PORTAMENTO = 65;
    public static final int UNDEFINED = 120;
    public static final int RESET_ALL_CONTROLLER = 121;
    public static final int ALL_NOTES_OFF = 123;
    public static final int PITCH_BEND_STATUS_BYTE = 0b1110_0000;
    public static final int CHANNEL_PRESSURE_STATUS_BYTE = 0b1101_0000;
    public static final int POLY_KEY_PRESSURE = 0b1010_0000;
    public static final int PROGRAM_CHANGE_STATUS_BYTE = 0b1100_0000;

    NodeHashTable channels;
    byte[] midi;

    MusicTrack(Buffer trackBuffer) {
        trackBuffer.index = trackBuffer.array.length - 3;
        int tracks = trackBuffer.readUnsignedByte();
        int division = trackBuffer.getUnsignedLEShort();
        int midiBytes = tracks * 10 + 14;
        trackBuffer.index = 0;
        int var5 = 0;
        int var6 = 0;
        int var7 = 0;
        int var8 = 0;
        int var9 = 0;
        int var10 = 0;
        int var11 = 0;
        int var12 = 0;

        int trackChunkOnset;
        int var14;
        int var15;
        for(trackChunkOnset = 0; trackChunkOnset < tracks; ++trackChunkOnset) {
            var14 = -1;

            while(true) {
                var15 = trackBuffer.readUnsignedByte();
                if(var15 != var14) {
                    ++midiBytes;
                }

                var14 = var15 & 15;
                if(var15 == 7) {
                    break;
                }

                if(var15 == 23) {
                    ++var5;
                } else if(var14 == 0) {
                    ++var7;
                } else if(var14 == 1) {
                    ++var8;
                } else if(var14 == 2) {
                    ++var6;
                } else if(var14 == 3) {
                    ++var9;
                } else if(var14 == 4) {
                    ++var10;
                } else if(var14 == 5) {
                    ++var11;
                } else {
                    if(var14 != 6) {
                        throw new RuntimeException();
                    }

                    ++var12;
                }
            }
        }

        midiBytes += var5 * 5;
        midiBytes += (var7 + var8 + var6 + var9 + var11) * 2;
        midiBytes = midiBytes + var10 + var12;
        trackChunkOnset = trackBuffer.index;
        var14 = tracks + var5 + var6 + var7 + var8 + var9 + var10 + var11 + var12;

        for(var15 = 0; var15 < var14; ++var15) {
            trackBuffer.readVariableLength();
        }

        midiBytes += trackBuffer.index - trackChunkOnset;
        var15 = trackBuffer.index;
        int var16 = 0;
        int var17 = 0;
        int var18 = 0;
        int var19 = 0;
        int var20 = 0;
        int var21 = 0;
        int var22 = 0;
        int var23 = 0;
        int var24 = 0;
        int var25 = 0;
        int var26 = 0;
        int var27 = 0;
        int controllerNumber = 0;

        int messageTypeIndex;
        for(messageTypeIndex = 0; messageTypeIndex < var6; ++messageTypeIndex) {
            controllerNumber = controllerNumber + trackBuffer.readUnsignedByte() & 127;
            if(controllerNumber != 0 && controllerNumber != 32) {
                if(controllerNumber == 1) {
                    ++var16;
                } else if(controllerNumber == 33) {
                    ++var17;
                } else if(controllerNumber == 7) {
                    ++var18;
                } else if(controllerNumber == 39) {
                    ++var19;
                } else if(controllerNumber == 10) {
                    ++var20;
                } else if(controllerNumber == 42) {
                    ++var21;
                } else if(controllerNumber == 99) {
                    ++var22;
                } else if(controllerNumber == 98) {
                    ++var23;
                } else if(controllerNumber == 101) {
                    ++var24;
                } else if(controllerNumber == 100) {
                    ++var25;
                } else if(controllerNumber != 64 && controllerNumber != 65 && controllerNumber != 120 && controllerNumber != 121 && controllerNumber != 123) {
                    ++var27;
                } else {
                    ++var26;
                }
            } else {
                ++var12;
            }
        }

        messageTypeIndex = 0;
        int var30 = trackBuffer.index;
        trackBuffer.index += var26;
        int polyKeyPressureIndex = trackBuffer.index;
        trackBuffer.index += var11;
        int pressureIndex = trackBuffer.index;
        trackBuffer.index += var10;
        int fineIndex = trackBuffer.index;
        trackBuffer.index += var9;
        int modulationWheelIndex = trackBuffer.index;
        trackBuffer.index += var16;
        int mainVolumeIndex = trackBuffer.index;
        trackBuffer.index += var18;
        int panIndex = trackBuffer.index;
        trackBuffer.index += var20;
        int keyIndex = trackBuffer.index;
        trackBuffer.index += var7 + var8 + var11;
        int noteOnVelocityIndex = trackBuffer.index;
        trackBuffer.index += var7;
        int var39 = trackBuffer.index;
        trackBuffer.index += var27;
        int noteOffVelocityIndex = trackBuffer.index;
        trackBuffer.index += var8;
        int lsbModulationWheelIndex = trackBuffer.index;
        trackBuffer.index += var17;
        int lsbMainVolumeIndex = trackBuffer.index;
        trackBuffer.index += var19;
        int lsbPanIndex = trackBuffer.index;
        trackBuffer.index += var21;
        int presetNumberIndex = trackBuffer.index;
        trackBuffer.index += var12;
        int coarseIndex = trackBuffer.index;
        trackBuffer.index += var9;
        int nonRegisteredParamNumberLsb2Index = trackBuffer.index;
        trackBuffer.index += var22;
        int nonRegisteredParamNumberLsb1Index = trackBuffer.index;
        trackBuffer.index += var23;
        int registeredParamNumberLsb2Index = trackBuffer.index;
        trackBuffer.index += var24;
        int registeredParamNumberLsb1Index = trackBuffer.index;
        trackBuffer.index += var25;
        int var50 = trackBuffer.index;
        trackBuffer.index += var5 * 3;
        this.midi = new byte[midiBytes];
        Buffer midiBuffer = new Buffer(this.midi);
        // The Chunk Type
        midiBuffer.writeInt(HEADER_CHUNK_TYPE);
        // length in bytes of the chunk data part.
        midiBuffer.writeInt(6);
        // The MIDI file format.
        midiBuffer.writeShort(tracks > 1?1:0);
        // The number of track chunks contained in this MIDI file.
        midiBuffer.writeShort(tracks);
        // This defines the default unit of delta-time for this MIDI file.
        midiBuffer.writeShort(division);
        trackBuffer.index = trackChunkOnset;
        int var52 = 0;
        int key = 0;
        int noteOnVelocity = 0;
        int noteOffVelocity = 0;
        int pitchBend = 0;
        int channelPressure = 0;
        int polyKeyPressure = 0;
        int[] var59 = new int[128];
        controllerNumber = 0;

        tracksLoop:
        for(int trackId = 0; trackId < tracks; ++trackId) {

            midiBuffer.writeInt(TRACK_CHUNK_TYPE);
            midiBuffer.index += 4;
            int chunkDataOnset = midiBuffer.index;
            int lastChannelVoiceMessageType = -1;

            while(true) {
                while(true) {
                    int deltaTime = trackBuffer.readVariableLength();
                    midiBuffer.writeSmartValue(deltaTime);
                    int messageType = trackBuffer.array[messageTypeIndex++] & 255;
                    boolean writeStatusByte = messageType != lastChannelVoiceMessageType;
                    lastChannelVoiceMessageType = messageType & 0xf;
                    if(messageType == 7) {
                        if(writeStatusByte) {
                            midiBuffer.writeByte(255);
                        }

                        midiBuffer.writeByte(47);
                        midiBuffer.writeByte(0);
                        midiBuffer.writeVariableLength(midiBuffer.index - chunkDataOnset);
                        continue tracksLoop;
                    }

                    if(messageType == 23) {
                        if(writeStatusByte) {
                            midiBuffer.writeByte(255);
                        }

                        midiBuffer.writeByte(81);
                        midiBuffer.writeByte(3);
                        midiBuffer.writeByte(trackBuffer.array[var50++]);
                        midiBuffer.writeByte(trackBuffer.array[var50++]);
                        midiBuffer.writeByte(trackBuffer.array[var50++]);
                    } else {
                        var52 ^= messageType >> 4;
                        if(lastChannelVoiceMessageType == 0) {
                            if(writeStatusByte) {
                                midiBuffer.writeByte(var52 + NOTE_ON_STATUS_BYTE);
                            }

                            key += trackBuffer.array[keyIndex++];
                            noteOnVelocity += trackBuffer.array[noteOnVelocityIndex++];
                            midiBuffer.writeByte(key & 127);
                            midiBuffer.writeByte(noteOnVelocity & 127);
                        } else if(lastChannelVoiceMessageType == 1) {
                            if(writeStatusByte) {
                                midiBuffer.writeByte(var52 + NOTE_OFF_STATUS_BYTE);
                            }

                            key += trackBuffer.array[keyIndex++];
                            noteOffVelocity += trackBuffer.array[noteOffVelocityIndex++];
                            midiBuffer.writeByte(key & 127);
                            midiBuffer.writeByte(noteOffVelocity & 127);
                        } else if(lastChannelVoiceMessageType == 2) {
                            if(writeStatusByte) {
                                midiBuffer.writeByte(var52 + CONTROLLER_CHANGE_STATUS_BYTE);
                            }

                            controllerNumber = controllerNumber + trackBuffer.array[var15++] & 127;
                            midiBuffer.writeByte(controllerNumber);
                            byte controllerValue;
                            if(controllerNumber != 0 && controllerNumber != 32) {
                                if(controllerNumber == MODULATION_WHEEL) {
                                    controllerValue = trackBuffer.array[modulationWheelIndex++];
                                } else if(controllerNumber == LSB_MODULATION_WHEEL) {
                                    controllerValue = trackBuffer.array[lsbModulationWheelIndex++];
                                } else if(controllerNumber == MAIN_VOLUME) {
                                    controllerValue = trackBuffer.array[mainVolumeIndex++];
                                } else if(controllerNumber == LSB_MAIN_VOLUME) {
                                    controllerValue = trackBuffer.array[lsbMainVolumeIndex++];
                                } else if(controllerNumber == PAN) {
                                    controllerValue = trackBuffer.array[panIndex++];
                                } else if(controllerNumber == LSB_PAN) {
                                    controllerValue = trackBuffer.array[lsbPanIndex++];
                                } else if(controllerNumber == NON_REGISTERED_PARAM_NUMBER_LSB_2) {
                                    controllerValue = trackBuffer.array[nonRegisteredParamNumberLsb2Index++];
                                } else if(controllerNumber == NON_REGISTERED_PARAM_NUMBER_LSB_1) {
                                    controllerValue = trackBuffer.array[nonRegisteredParamNumberLsb1Index++];
                                } else if(controllerNumber == REGISTERED_PARAM_NUMBER_LSB_2) {
                                    controllerValue = trackBuffer.array[registeredParamNumberLsb2Index++];
                                } else if(controllerNumber == REGISTERED_PARAM_NUMBER_LSB_1) {
                                    controllerValue = trackBuffer.array[registeredParamNumberLsb1Index++];
                                } else if(controllerNumber != DAMPER_PEDAL
                                        && controllerNumber != PORTAMENTO
                                        && controllerNumber != UNDEFINED
                                        && controllerNumber != RESET_ALL_CONTROLLER
                                        && controllerNumber != ALL_NOTES_OFF) {
                                    controllerValue = trackBuffer.array[var39++];
                                } else {
                                    controllerValue = trackBuffer.array[var30++];
                                }
                            } else {
                                controllerValue = trackBuffer.array[presetNumberIndex++];
                            }

                            int var67 = controllerValue + var59[controllerNumber];
                            var59[controllerNumber] = var67;
                            midiBuffer.writeByte(var67 & 127);
                        } else if(lastChannelVoiceMessageType == 3) {
                            if(writeStatusByte) {
                                midiBuffer.writeByte(var52 + PITCH_BEND_STATUS_BYTE);
                            }

                            pitchBend += trackBuffer.array[coarseIndex++];
                            pitchBend += trackBuffer.array[fineIndex++] << 7;
                            midiBuffer.writeByte(pitchBend & 127);
                            midiBuffer.writeByte(pitchBend >> 7 & 127);
                        } else if(lastChannelVoiceMessageType == 4) {
                            if(writeStatusByte) {
                                midiBuffer.writeByte(var52 + CHANNEL_PRESSURE_STATUS_BYTE);
                            }

                            channelPressure += trackBuffer.array[pressureIndex++];
                            midiBuffer.writeByte(channelPressure & 127);
                        } else if(lastChannelVoiceMessageType == 5) {
                            if(writeStatusByte) {
                                midiBuffer.writeByte(var52 + POLY_KEY_PRESSURE);
                            }

                            key += trackBuffer.array[keyIndex++];
                            polyKeyPressure += trackBuffer.array[polyKeyPressureIndex++];
                            midiBuffer.writeByte(key & 127);
                            midiBuffer.writeByte(polyKeyPressure & 127);
                        } else {
                            if(lastChannelVoiceMessageType != 6) {
                                throw new RuntimeException();
                            }

                            if(writeStatusByte) {
                                midiBuffer.writeByte(var52 + PROGRAM_CHANGE_STATUS_BYTE);
                            }

                            midiBuffer.writeByte(trackBuffer.array[presetNumberIndex++]);
                        }
                    }
                }
            }
        }
    }

    public MusicTrack(byte[] midi) {
        this.midi = midi;
    }

    void load() {
        if(this.channels == null) {
            this.channels = new NodeHashTable(16);
            int[] controllers = new int[16];
            int[] programs = new int[16];
            programs[9] = 128;
            controllers[9] = 128;
            MidiFileReader midiFileReader = new MidiFileReader(this.midi);
            int trackCount = midiFileReader.trackCount();

            int trackId;
            for(trackId = 0; trackId < trackCount; ++trackId) {
                midiFileReader.gotoTrack(trackId);
                midiFileReader.readTrackLength(trackId);
                midiFileReader.markTrackPosition(trackId);
            }

            label53:
            do {
                while(true) {
                    trackId = midiFileReader.getPrioritizedTrack();
                    int trackLength = midiFileReader.trackLengths[trackId];

                    while(trackLength == midiFileReader.trackLengths[trackId]) {
                        midiFileReader.gotoTrack(trackId);
                        int response = midiFileReader.readMessage(trackId);
                        if(response == 1) {
                            midiFileReader.setTrackDone();
                            midiFileReader.markTrackPosition(trackId);
                            continue label53;
                        }

                        int statusByte = response & 0b1111_0000;
                        int channel;

                        /*
                          Controller change

                          Sent when a change is made in a footswitch, expression pedal, slider, or other controller.
                         */
                        if(statusByte == CONTROLLER_CHANGE_STATUS_BYTE) {
                            channel = response & 15;
                            int controllerNumber = response >> 8 & 127;
                            int controllerValue = response >> 16 & 127;
                            if(controllerNumber == 0) {
                                controllers[channel] = (controllerValue << 14) + (controllers[channel] & 0b11111111111000000011111111111111);
                            }

                            if(controllerNumber == 32) {
                                controllers[channel] = (controllers[channel] & -16257) + (controllerValue << 7);
                            }
                        }

                        /*
                          Program change

                          Used to change the instrument (or sound) to be played when a note-on message is received.
                          This is usually not retro-active, and only applies to subsequent note-on messages
                          This message may have a completely different interpretation depending on the type of device. For example, it could change the current rhythm on a drum-machine.
                         */
                        if(statusByte == PROGRAM_CHANGE_STATUS_BYTE) {
                            channel = response & 15;
                            int newProgramNumber = response >> 8 & 127;
                            programs[channel] = newProgramNumber + controllers[channel];
                        }

                        /*
                          Note on

                          Normally sent when a key (on a synthesizer) is pressed
                          A corresponding note-off message must be sent for each and every note-on message
                         */
                        if(statusByte == NOTE_ON_STATUS_BYTE) {
                            channel = response & 15;
                            int pressedKey = response >> 8 & 127;
                            int pressVelocity = response >> 16 & 127;
                            if(pressVelocity > 0) {
                                int program = programs[channel];
                                ByteArrayNode arrayNode = (ByteArrayNode)this.channels.get(program);
                                if(arrayNode == null) {
                                    arrayNode = new ByteArrayNode(new byte[128]);
                                    this.channels.put(arrayNode, program);
                                }
                                arrayNode.byteArray[pressedKey] = 1;
                            }
                        }

                        midiFileReader.readTrackLength(trackId);
                        midiFileReader.markTrackPosition(trackId);
                    }
                }
            } while(!midiFileReader.isDone());
        }
    }

    void clear() {
        this.channels = null;
    }

    public static MusicTrack readTrack(AbstractIndexCache trackCache, int fileId, int archiveId) {
        byte[] buffer = trackCache.takeRecord(fileId, archiveId);
        return buffer == null?null:new MusicTrack(new Buffer(buffer));
    }
}
