package com.runescape.net;

import com.grinder.client.util.Log;
import com.runescape.sign.SignLink;
import com.runescape.clock.Time;
import com.runescape.task.Task;
import com.runescape.task.TaskHandler;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 11/12/2019
 */
public class NetSocket extends AbstractSocket implements Runnable {

    private InputStream inputStream;
    private OutputStream outputStream;
    private Socket socket;

    private boolean closed;
    private boolean exceptionWriting;
    private TaskHandler taskHandler;
    private Task task;
    private byte[] writeBuffer;

    private int writeOffset;
    private int writeIndex;
    private final int writeBufferSize;
    private final int __k;

    public NetSocket(Socket socket, TaskHandler taskHandler, int writeBufferSize) throws IOException {
        this.socket = socket;
        this.taskHandler = taskHandler;
        this.writeBufferSize = writeBufferSize;
        writeOffset = 0;
        writeIndex = 0;
        __k = writeBufferSize - 100;
        closed = false;
        exceptionWriting = false;
        socket.setSoTimeout(30_000);
        socket.setTcpNoDelay(true);
        socket.setReceiveBufferSize(65536);
        socket.setSendBufferSize(65536);
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }

    @Override
    public boolean isAvailable(int var1) throws IOException {
        return !closed && inputStream.available() >= var1;
    }

    @Override
    public int available() throws IOException {
        return closed?0:inputStream.available();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return closed?0:inputStream.read();
    }

    @Override
    public int read(byte[] bytes, int offset, int length) throws IOException {
        if(closed)
            return 0;
        else {
            int i;
            int b;
            for(i = length; length > 0; length -= b) {
                b = inputStream.read(bytes, offset, length);
                if(b <= 0)
                    throw new EOFException();
                offset += b;
            }
            return i;
        }
    }

    @Override
    public void write(byte[] bytes, int offset, int length) throws IOException {
        if(!closed){
            if(exceptionWriting){
                exceptionWriting = false;
                throw new IOException();
            } else {
                if(writeBuffer == null)
                    writeBuffer = new byte[writeBufferSize];

                synchronized (this){
                    for(int i = 0; i < length; ++i) {
                        this.writeBuffer[writeIndex] = bytes[i + offset];
                        this.writeIndex = (writeIndex + 1) % writeBufferSize;

                        if((__k + writeOffset) % writeBufferSize == this.writeIndex)
                            throw new IOException();
                    }

                    if(task == null)
                        task = taskHandler.newThreadTask(this, 3);

                    notifyAll();
                }
            }
        }
    }

    @Override
    public void close() {
        if(!closed){
            synchronized (this){
                closed = true;
                notifyAll();
            }
            if(task != null){
                while (task.status == 0){
                    Time.sleep(1L);
                }
                if(task.status == 1){
                    try {
                        ((Thread)task.result).join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            task = null;
        }
    }

    @Override
    public void run() {
        try {
            while (true){
                label84: {
                    int length;
                    int offset;
                    synchronized(this) {
                        if(writeIndex == writeOffset) {

                            if(closed)
                                break label84;

                            try {
                                this.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        offset = writeOffset;
                        if(writeIndex >= writeOffset)
                            length = writeIndex - writeOffset;
                        else
                            length = writeBufferSize - writeOffset;
                    }

                    if(length <= 0)
                        continue;

                    try {
                        outputStream.write(writeBuffer, offset, length);
                    } catch (IOException e) {
                        exceptionWriting = true;
                    }

                    writeOffset = (length + writeOffset) % writeBufferSize;

                    try {
                        if(writeIndex == writeOffset)
                            outputStream.flush();

                    } catch (IOException e) {
                        exceptionWriting = true;
                        e.printStackTrace();
                    }
                    continue;
                }

                try {
                    if(inputStream != null)
                        inputStream.close();

                    if(outputStream != null)
                        outputStream.close();

                    if(socket != null)
                        socket.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                writeBuffer = null;
                break;
            }
        } catch (Exception e) {
            Log.error("Socket thread failed",e);
        }
    }
}
