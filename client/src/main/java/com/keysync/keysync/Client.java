package com.keysync.keysync;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JTextArea;

public class Client implements Runnable {

    private final String serverAddress;
    private final int port;
    private JTextArea logArea;
    private Socket clientSocket;
    
    public Client(String serverAddress, int port, JTextArea logArea) {
        this.serverAddress = serverAddress;
        this.port = port;
        this.logArea = logArea;
    }

    @Override
    public void run() {
        try {
            clientSocket = new Socket(serverAddress, port);
            logArea.append("Connected to server at " + serverAddress + ":" + port + "\n");

            // Start a thread to receive messages from the server
            new Thread(new EventReceiver(clientSocket, logArea)).start();
        } catch (IOException e) {
            logArea.append("Error connecting to server: " + e.getMessage() + "\n");
        }
    }
    
    // Method to close the connection
    public void stop() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                logArea.append("Disconnected from server\n");
            }
        } catch (IOException e) {
            logArea.append("Error disconnecting from server: " + e.getMessage() + "\n");
        }
    }
    
    // Inner class to handle incoming messages
    private class EventReceiver implements Runnable {
        private Socket socket;
        private JTextArea logArea;
        
        public EventReceiver(Socket socket, JTextArea logArea) {
            this.socket = socket;
            this.logArea = logArea;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String message;
                while ((message = reader.readLine()) != null) {
                    logArea.append("Received from server: " + message + "\n");
                }
            } catch (IOException e) {
                logArea.append("Error reading from server: " + e.getMessage() + "\n");
            }
        }
    }
}