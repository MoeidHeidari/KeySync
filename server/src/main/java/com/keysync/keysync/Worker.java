package com.keysync.keysync;

import java.awt.TextArea;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class Worker implements Runnable {
    private Socket clientSocket;
    private ObjectOutputStream out;
    private TextArea logArea;
    private boolean running;

    public Worker(Socket clientSocket, TextArea logArea) {
        this.clientSocket = clientSocket;
        this.logArea = logArea;
        this.running = true;

        try {
            this.out = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (running) {
                // Here you could implement some handling if needed.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendEvent(Object event) {
        try {
            if (out != null) {
                out.writeObject(event);
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        running = false;
        try {
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}