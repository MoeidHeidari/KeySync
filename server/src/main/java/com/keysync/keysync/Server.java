package com.keysync.keysync;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JTextArea;


import java.awt.TextArea;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private int port;
    private TextArea logArea;
    private ServerSocket serverSocket;
    private List<Worker> workers;

    public Server(int port, TextArea logArea) {
        this.port = port;
        this.logArea = logArea;
        this.workers = new ArrayList<>();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            logArea.append("Server started on port " + port + "\n");

            while (true) {
                logArea.append("Waiting for client connection...\n");
                Socket clientSocket = serverSocket.accept();
                logArea.append("Client connected: " + clientSocket.getInetAddress() + "\n");

                Worker worker = new Worker(clientSocket, logArea);
                workers.add(worker);
                new Thread(worker).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            for (Worker worker : workers) {
                worker.stop();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendEvent(Object event) {
        for (Worker worker : workers) {
            worker.sendEvent(event);
        }
    }
}