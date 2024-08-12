package com.keysync.keysync;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JTextArea;

class ClientServer implements Runnable {

    private final int port;
    private JTextArea logArea;

    public ClientServer(int port, JTextArea logArea) {
        this.port = port;
        this.logArea = logArea;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logArea.append("Client server listening on port: " + port + "\n");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logArea.append("event received \n");
                new Thread(new EventReceiver(clientSocket, logArea)).start();
            }

        } catch (IOException e) {
            logArea.append("Error: " + e.getMessage() + "\n");
        }
    }
}