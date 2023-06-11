package com.runescape.task;

import com.runescape.audio.PcmStreamMixer;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 11/12/2019
 */
public class TaskHandler implements Runnable{

    private final static int SOCKET_TASK_TYPE = 1;
    private final static int THREAD_TASK_TYPE = 2;
    private final static int STREAM_TASK_TYPE = 4;
    public final static int TASK_COMPLETE_STATUS = 1;
    public final static int TASK_FAILED_STATUS = 2;

    public static String javaVendor;
    public static String javaVersion;

    private Task current;
    private Task task0;
    private Thread thread;

    private boolean closed;

    public TaskHandler(){
        javaVendor = "Unknown";
        javaVersion = "1.6";

        try {
            javaVendor = System.getProperty("java.vendor");
            javaVersion = System.getProperty("java.version");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        closed = false;
        thread = new Thread(this);
        thread.setPriority(10);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        while (true){
            Task task;
            synchronized (this){
                while (true){
                    if(closed)
                        return;
                    if(current != null){
                        task = current;
                        current = current.next;
                        if(current == null)
                            task0 = null;
                        break;
                    }
                    try {
                        wait();
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
            try {
                int taskType = task.type;
                if(taskType == SOCKET_TASK_TYPE){
                    task.result = new Socket(InetAddress.getByName((String)task.objectArgument), task.intArgument);
                } else if(taskType == THREAD_TASK_TYPE){
                    final Thread thread = new Thread((Runnable)task.objectArgument);
                    thread.setDaemon(true);
                    thread.start();
                    thread.setPriority(task.intArgument);
                    task.result = thread;
                } else if(taskType == STREAM_TASK_TYPE)
                    task.result = new DataInputStream(((URL)task.objectArgument).openStream());

                task.status = TASK_COMPLETE_STATUS;
            } catch (IOException e) {
                e.printStackTrace();
                task.status = TASK_FAILED_STATUS;
            }
        }
    }

    public final void close(){
        synchronized (this){
            closed = true;
            notifyAll();
        }

        try {
            thread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public final Task newTask(int type, int arg0, Object arg1){
        final Task task = new Task();
        task.type = type;
        task.intArgument = arg0;
        task.objectArgument = arg1;
        synchronized (this){
            if(task0 != null){
                task0.next = task;
                task0 = task;
            } else
                task0 = current = task;
            notify();
            return task;
        }
    }
    public final Task newSocketTask(String address, int port){
        return newTask(SOCKET_TASK_TYPE, port, address);
    }

    public final Task newThreadTask(Runnable runnable, int priority){
        return newTask(THREAD_TASK_TYPE, priority, runnable);
    }
}
