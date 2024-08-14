package com.keysync.keysync;

import java.awt.TextArea;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;

public class Server {
    private int port;
    private TextArea logArea;
    private ServerSocket serverSocket;
    private List<Socket> clientSockets;

    public Server(int port, TextArea logArea) {
        this.port = port;
        this.logArea = logArea;
        this.clientSockets = new ArrayList<>();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            logArea.append("Server started on port " + port + "\n");

            // Accept client connections in a separate thread
            new Thread(() -> {
                while (true) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        synchronized (clientSockets) {
                            clientSockets.add(clientSocket);
                        }
                        logArea.append("Client connected: " + clientSocket.getInetAddress() + "\n");
                    } catch (Exception e) {
                        logArea.append("Error accepting client: " + e.getMessage() + "\n");
                    }
                }
            }).start();

        } catch (Exception e) {
            logArea.append("Error: " + e.getMessage() + "\n");
        }
    }

    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            logArea.append("Server stopped\n");
        } catch (Exception e) {
            logArea.append("Error stopping server\n");
        }
    }

    public void sendEvent(NativeKeyEvent e) {
        String eventMessage = "KeyEvent: " + e.getKeyCode() + "\n";
        sendMessageToClients(eventMessage);
    }

    public void sendEvent(NativeMouseEvent e) {
        String eventMessage = "MouseEvent: " + e.getButton() + " at (" + e.getX() + ", " + e.getY() + ")\n";
        sendMessageToClients(eventMessage);
    }

    private void sendMessageToClients(String message) {
        synchronized (clientSockets) {
            List<Socket> closedSockets = new ArrayList<>();
            for (Socket clientSocket : clientSockets) {
                try {
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
                    out.println(message);
                } catch (Exception e) {
                    logArea.append("Error sending message to client: " + e.getMessage() + "\n");
                    closedSockets.add(clientSocket);
                }
            }
            // Remove any closed sockets
            clientSockets.removeAll(closedSockets);
        }
    }
}