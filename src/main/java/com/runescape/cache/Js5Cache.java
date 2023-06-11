package com.runescape.cache;

import com.grinder.Configuration;
import com.grinder.client.GameShell;
import com.grinder.client.util.Log;
import com.runescape.Client;
import com.runescape.clock.Time;
import com.runescape.io.Buffer;
import com.runescape.net.AbstractSocket;
import com.runescape.net.NetSocket;
import com.runescape.task.Task;
import com.runescape.task.TaskHandler;
import com.runescape.util.SocketUtil;

import java.io.IOException;
import java.net.Socket;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 14/02/2020
 */
public class Js5Cache {

    private static final int ARCHIVE_REQUEST_URGENT = 0;
    private static final int ARCHIVE_REQUEST_NEUTRAL = 1;
    private static final int CLIENT_INIT_GAME = 2;
    private static final int CLIENT_LOAD_SCREEN = 3;
    private static final long LOAD_INDICES_RESPONSE_KEY = 16711935L;
    private static final int INDICES_COUNT = 256;
    private static final int MAX_CHUNK_BYTES = 512;
    private static final int HANDSHAKE_OPCODE = 15;
    private static final int REVISION = 180;
    private static final int CLIENT_PING_REQUEST = 4;

    public static Buffer responseArchiveBuffer;
    public static NetFileRequest currentResponse;
    static AbstractSocket js5Socket;
    static int js5ConnectState;
    static long js5StartTimeMs;
    public static boolean priorityRequest;
    public static boolean useBufferedSocket;
    static int js5Errors;
    static int reconnectTimer;
    static Task js5SocketTask;
    static int port1 = Configuration.connected_world.getPort();
    static int port2 = Configuration.connected_world.getPort();
    static int port3 = Configuration.connected_world.getPort();
    public static String worldHost;

    static {
        useBufferedSocket = true;
    }

    public static void doCycleJs5() {
        if(Client.gameState != 1000) {
            long currentTimeMillis = Time.currentTimeMillis();
            int timeMillisPassed = (int)(currentTimeMillis - NetCache.lastTimeMillis);
            NetCache.lastTimeMillis = currentTimeMillis;
            if(timeMillisPassed > 200) {
                timeMillisPassed = 200;
            }

            NetCache.totalTimeMillisPassed += timeMillisPassed;
            boolean completedAllTasks;
            if(NetCache.pendingResponsesCount == 0 && NetCache.pendingPriorityResponsesCount == 0 && NetCache.pendingWritesCount == 0 && NetCache.pendingPriorityWritesCount == 0) {
                completedAllTasks = true;
            } else if(NetCache.socket == null) {
                completedAllTasks = false;
            } else {
                try {
                    reconnectBlock: {

                        if(NetCache.totalTimeMillisPassed > 30_000)
                            throw new IOException("Connection failing for 30 seconds! ");

                        while(NetCache.pendingPriorityResponsesCount < 200 && NetCache.pendingPriorityWritesCount > 0) {
                            final NetFileRequest fileRequest = (NetFileRequest)NetCache.pendingPriorityWrites.first();
                            final Buffer buffer = new Buffer(4);
                            buffer.writeByte(ARCHIVE_REQUEST_NEUTRAL);
                            buffer.writeMedium((int)fileRequest.key);
                            NetCache.socket.write(buffer.array, 0, 4);
                            NetCache.pendingPriorityResponses.put(fileRequest, fileRequest.key);
                            --NetCache.pendingPriorityWritesCount;
                            ++NetCache.pendingPriorityResponsesCount;
                        }

                        while(NetCache.pendingResponsesCount < 200 && NetCache.pendingWritesCount > 0) {
                            final NetFileRequest fileRequest = (NetFileRequest)NetCache.pendingWritesQueue.last();
                            final Buffer  buffer = new Buffer(4);
                            buffer.writeByte(ARCHIVE_REQUEST_URGENT);
                            buffer.writeMedium((int)fileRequest.key);
                            NetCache.socket.write(buffer.array, 0, 4);
                            fileRequest.removeDual();
                            NetCache.pendingResponses.put(fileRequest, fileRequest.key);
                            --NetCache.pendingWritesCount;
                            ++NetCache.pendingResponsesCount;
                        }

                        for(int j = 0; j < 100; ++j) {

                            int available = NetCache.socket.available();

                            if(available < 0)
                                throw new IOException("Negative bytes available in incoming stream?");

                            if(available == 0)
                                break;

                            NetCache.totalTimeMillisPassed = 0;
                            byte expectedBytes = 0;
                            if(currentResponse == null) {
                                expectedBytes = 8;
                            } else if(NetCache.responseExpectedBytes == 0) {
                                expectedBytes = 1;
                            }

                            int var8;
                            int var9;
                            int var10;
                            int var12;
                            if(expectedBytes > 0) {
                                int bytesToRead = expectedBytes - NetCache.responseHeaderBuffer.index;
                                if(bytesToRead > available) {
                                    bytesToRead = available;
                                }

                                NetCache.socket.read(NetCache.responseHeaderBuffer.array, NetCache.responseHeaderBuffer.index, bytesToRead);
                                if(NetCache.randomValue != 0) {
                                    for(int k = 0; k < bytesToRead; ++k) {
                                        NetCache.responseHeaderBuffer.array[NetCache.responseHeaderBuffer.index + k] ^= NetCache.randomValue;
                                    }
                                }

                                NetCache.responseHeaderBuffer.index += bytesToRead;

                                if(NetCache.responseHeaderBuffer.index < expectedBytes)
                                    break;

                                if(currentResponse == null) {
                                    NetCache.responseHeaderBuffer.index = 0;
                                    final int responseIndex = NetCache.responseHeaderBuffer.getUnsignedByte();
                                    final int responseArchive = NetCache.responseHeaderBuffer.getUnsignedLEShort();
                                    final int compressionType = NetCache.responseHeaderBuffer.getUnsignedByte();
                                    final int responseLength = NetCache.responseHeaderBuffer.readInt();
                                    final long responseId = responseArchive + (responseIndex << 16);

                                    NetFileRequest response = (NetFileRequest)NetCache.pendingPriorityResponses.get(responseId);

                                    priorityRequest = true;

                                    if(response == null) {
                                        response = (NetFileRequest)NetCache.pendingResponses.get(responseId);
                                        priorityRequest = false;
                                    }

                                    if(response == null) {
                                        throw new IOException("Response["+responseIndex+", "+responseArchive+", "+compressionType+", "+responseLength+", "+responseId+"] is null");
                                    }

                                    int extraBytes = compressionType == 0 ? 5 : 9;
                                    currentResponse = response;
                                    responseArchiveBuffer = new Buffer(responseLength + extraBytes + currentResponse.padding);
                                    responseArchiveBuffer.writeByte(compressionType);
                                    responseArchiveBuffer.writeInt(responseLength);
                                    NetCache.responseExpectedBytes = 8;
                                    NetCache.responseHeaderBuffer.index = 0;
                                } else if(NetCache.responseExpectedBytes == 0) {
                                    if(NetCache.responseHeaderBuffer.array[0] == -1) {
                                        NetCache.responseExpectedBytes = 1;
                                        NetCache.responseHeaderBuffer.index = 0;
                                    } else {
                                        currentResponse = null;
                                    }
                                }
                            } else {
                                int responseOnset = responseArchiveBuffer.array.length - currentResponse.padding;
                                int responseBytes = 512 - NetCache.responseExpectedBytes;
                                if(responseBytes > responseOnset - responseArchiveBuffer.index) {
                                    responseBytes = responseOnset - responseArchiveBuffer.index;
                                }

                                if(responseBytes > available) {
                                    responseBytes = available;
                                }

                                NetCache.socket.read(responseArchiveBuffer.array, responseArchiveBuffer.index, responseBytes);
                                if(NetCache.randomValue != 0) {
                                    for(int i = 0; i < responseBytes; ++i) {
                                        responseArchiveBuffer.array[responseArchiveBuffer.index + i] ^= NetCache.randomValue;
                                    }
                                }

                                responseArchiveBuffer.index += responseBytes;
                                NetCache.responseExpectedBytes += responseBytes;
                                if(responseOnset == responseArchiveBuffer.index) {
                                    if(LOAD_INDICES_RESPONSE_KEY == currentResponse.key) {
                                        OsCache.refrence = responseArchiveBuffer;

                                        for(int i = 0; i < INDICES_COUNT; ++i) {
                                            final IndexCache indexCache = NetCache.indexCaches[i];
                                            if(indexCache != null) {
                                                OsCache.refrence.index = i * 8 + 5;
                                                final int crc = OsCache.refrence.readInt();
                                                final int version = OsCache.refrence.readInt();
                                                indexCache.loadIndexReference(crc, version);
                                            }
                                        }
                                    } else {
                                        NetCache.crc.reset();
                                        NetCache.crc.update(responseArchiveBuffer.array, 0, responseOnset);
                                        final int crcValue = (int)NetCache.crc.getValue();
                                        if(crcValue != currentResponse.crc) {
                                            try {
                                                NetCache.socket.close();
                                            } catch (Exception e) {
                                                Log.error("CRC mismatch: "+crcValue+"!="+currentResponse.crc, e);
                                            }

                                            ++NetCache.crcMismatches;
                                            NetCache.socket = null;
                                            NetCache.randomValue = (byte)((int)(Math.random() * 255.0D + 1.0D));
                                            completedAllTasks = false;
                                            break reconnectBlock;
                                        }

                                        NetCache.crcMismatches = 0;
                                        NetCache.ioExceptions = 0;
                                        currentResponse.indexCache.write((int)(currentResponse.key & 65535L), responseArchiveBuffer.array, 16711680L == (currentResponse.key & 16711680L), priorityRequest);
                                    }

                                    currentResponse.remove();
                                    if(priorityRequest) {
                                        --NetCache.pendingPriorityResponsesCount;
                                    } else {
                                        --NetCache.pendingResponsesCount;
                                    }

                                    NetCache.responseExpectedBytes = 0;
                                    currentResponse = null;
                                    responseArchiveBuffer = null;
                                } else {

                                    if(NetCache.responseExpectedBytes != MAX_CHUNK_BYTES)
                                        break;

                                    NetCache.responseExpectedBytes = 0;
                                }
                            }
                        }

                        completedAllTasks = true;
                    }
                } catch (IOException e1) {
                    Log.error("Failed to reconnect with file server", e1);
                    try {
                        NetCache.socket.close();
                    } catch (Exception e2) {
                        Log.error("Failed to close socket after failing to reconnect, double trouble :C", e2);
                    }

                    ++NetCache.ioExceptions;
                    NetCache.socket = null;
                    completedAllTasks = false;
                }
            }

            if(!completedAllTasks) {
                doCycleJs5Connect();
            }

        }
    }
    public static void doCycleJs5Connect() {
        if(NetCache.crcMismatches >= 4) {
            Log.error("js5crc = "+NetCache.crcMismatches);
            Client.gameState = 1000;
        } else {
            if(NetCache.ioExceptions >= 4) {
                if(Client.gameState <= 5) {
                    Log.error("js5io");
                    Client.gameState = 1000;
                    return;
                } else {
                    Log.info("Reconnecting after timeout :)");
                }
                reconnectTimer = 10000;
                NetCache.ioExceptions = 3;
            }

            if(--reconnectTimer + 1 <= 0) {
                Log.info("Attempting to reconnect with server at "+worldHost+", "+port3);
                try {
                    if(js5ConnectState == 0) {
                        js5SocketTask = GameShell.taskHandler.newSocketTask(worldHost, port3);
                        ++js5ConnectState;
                    }

                    if(js5ConnectState == 1) {
                        if(js5SocketTask.status == TaskHandler.TASK_FAILED_STATUS) {
                            Log.info("Failed to reconnect with server!");
                            js5Error(-1);
                            return;
                        }

                        if(js5SocketTask.status == TaskHandler.TASK_COMPLETE_STATUS) {
                            ++js5ConnectState;
                        }
                    }

                    if(js5ConnectState == 2) {
                        if(useBufferedSocket) {
                            js5Socket = SocketUtil.createBufferedSocket((Socket)js5SocketTask.result, 40000, 5000);
                        } else {
                            js5Socket = new NetSocket((Socket)js5SocketTask.result, GameShell.taskHandler, 5000);
                        }
                        Log.info("Created file server socket (buffered="+useBufferedSocket+", rev="+REVISION+")");
                        Buffer buffer = new Buffer(5);
                        buffer.writeByte(HANDSHAKE_OPCODE);
                        buffer.writeInt(REVISION);
                        js5Socket.write(buffer.array, 0, 5);
                        ++js5ConnectState;
                        js5StartTimeMs = Time.currentTimeMillis();
                        Log.info("Sending handshake, state = "+js5ConnectState+", time = "+js5StartTimeMs);
                    }

                    if(js5ConnectState == 3) {
                        if(js5Socket.available() > 0 || !useBufferedSocket && Client.gameState <= 5) {
                            int responseCode = js5Socket.readUnsignedByte();
                            if(responseCode != 0) {
                                js5Error(responseCode);
                                return;
                            }

                            ++js5ConnectState;
                        } else if(Time.currentTimeMillis() - js5StartTimeMs > 30_000L) {
                            js5Error(-2);
                            return;
                        }
                    }

                    if(js5ConnectState == 4) {

                        final AbstractSocket fileServerSocket = js5Socket;
                        final boolean gameLoaded = Client.gameState > 20;

                        if(NetCache.socket != null) {
                            try {
                                NetCache.socket.close();
                            } catch (Exception e) {
                                Log.error("Client.gameSate="+Client.gameState+", loaded = "+gameLoaded+", closing socket failed!", e);
                            }

                            NetCache.socket = null;
                        }

                        NetCache.socket = fileServerSocket;
                        sendClientState(gameLoaded);
                        NetCache.responseHeaderBuffer.index = 0;
                        currentResponse = null;
                        responseArchiveBuffer = null;
                        NetCache.responseExpectedBytes = 0;

                        while(true) {
                            NetFileRequest request = (NetFileRequest)NetCache.pendingPriorityResponses.first();
                            if(request == null) {
                                while(true) {
                                    request = (NetFileRequest)NetCache.pendingResponses.first();
                                    if(request == null) {

                                        if(NetCache.randomValue != 0)
                                            sendPingRequest();

                                        NetCache.totalTimeMillisPassed = 0;
                                        NetCache.lastTimeMillis = Time.currentTimeMillis();
                                        js5SocketTask = null;
                                        js5Socket = null;
                                        js5ConnectState = 0;
                                        js5Errors = 0;
                                        return;
                                    }

                                    NetCache.pendingWritesQueue.addLast(request);
                                    NetCache.pendingWrites.put(request, request.key);
                                    ++NetCache.pendingWritesCount;
                                    --NetCache.pendingResponsesCount;
                                }
                            }

                            NetCache.pendingPriorityWrites.put(request, request.key);
                            ++NetCache.pendingPriorityWritesCount;
                            --NetCache.pendingPriorityResponsesCount;
                        }
                    }
                } catch (IOException e) {
                    js5Error(-3, e);
                }
            }
        }
    }

    public static void js5Error(int responseCode) {
        js5Error(responseCode, new Exception("error: "+responseCode));
    }

    public static void js5Error(int responseCode, Throwable throwable) {
        js5SocketTask = null;
        js5Socket = null;
        js5ConnectState = 0;
        if(port1 == port3)
            port3 = port2;
        else
            port3 = port1;

        ++js5Errors;

        final String prefix = Client.gameState+" - "+" - "+responseCode;

        if(js5Errors >= 2 && (responseCode == 7 || responseCode == 9)) {
            if(Client.gameState <= 5) {
                Log.error(prefix+" - js5connect_full", throwable);
                Client.gameState = 1000;
            } else {
                Log.info(prefix+" - Reconnecting with file server");
                reconnectTimer = 10000;
            }
        } else if(js5Errors >= 2 && responseCode == 6) {
            Log.error(prefix+" - js5connect_outofdate", throwable);
            Client.gameState = 1000;
        } else if(js5Errors >= 4) {
            if(Client.gameState <= 5) {
                Log.error(prefix+" - js5connect", throwable);
                Client.gameState = 1000;
            } else {
                Log.info(prefix+" - Reconnecting with file server");
                reconnectTimer = 10000;
            }
        } else {
            Log.info(prefix+" - "+js5Errors+" Reconnecting with file server");
        }
    }

    private static void sendPingRequest() {
        try {
            Buffer buffer = new Buffer(4);
            buffer.writeByte(CLIENT_PING_REQUEST);
            buffer.writeByte(NetCache.randomValue);
            buffer.writeShort(0);
            NetCache.socket.write(buffer.array, 0, 4);
            Log.info("Send ping, rando == "+NetCache.randomValue);
        } catch (IOException e1) {
            Log.error("Failed to send ping request", e1);
            try {
                NetCache.socket.close();
            } catch (Exception e2) {
                Log.error("Failed to close socket after failing to send ping request", e2);
            }

            ++NetCache.ioExceptions;
            NetCache.socket = null;
        }
    }

    public static void sendClientState(boolean startingUp) {
        if(NetCache.socket != null) {
            try {
                Buffer buffer = new Buffer(4);
                buffer.writeByte(startingUp ? CLIENT_INIT_GAME : CLIENT_LOAD_SCREEN);
                buffer.writeMedium(0);
                NetCache.socket.write(buffer.array, 0, 4);
                Log.info("Send handshake, starting up == "+startingUp);
            } catch (IOException e1) {
                Log.error("Failed to send handshake, attempting to close socket...", e1);
                try {
                    NetCache.socket.close();
                } catch (Exception e2) {
                    Log.error("Failed to close socket, not good", e2);
                }
                ++NetCache.ioExceptions;
                NetCache.socket = null;
            }
        }
    }

    static int getLoadPercent(int index, int archiveId) {
       final long key = (index << 16) + archiveId;
       final boolean isCurrentResponse = currentResponse != null && key == currentResponse.key;
       return isCurrentResponse
               ? responseArchiveBuffer.index * 99 / (responseArchiveBuffer.array.length - currentResponse.padding) + 1
               : 0;
    }
}
