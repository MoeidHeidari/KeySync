package com.keysync.keysync;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {
    private ServerSocket serverSocket;
    private Robot robot;
    private ClientGUI clientGUI;

    public Client(ClientGUI clientGUI, int port) {
        this.clientGUI = clientGUI;
        try {
            serverSocket = new ServerSocket(port);
            robot = new Robot();
            clientGUI.updateLog("Server started on port: " + port + "\n");
        } catch (Exception e) {
            clientGUI.updateLog("Error starting server: " + e.getMessage() + "\n");
        }
    }

    public void start() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientGUI.updateLog("Client connected: " + clientSocket.getInetAddress() + "\n");
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (Exception e) {
            clientGUI.updateLog("Error accepting client connection: " + e.getMessage() + "\n");
        }
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                processEvent(line);
            }
        } catch (Exception e) {
            clientGUI.updateLog("Connection lost: " + e.getMessage() + "\n");
        }
    }

    private void processEvent(String eventMessage) {
        try {
            if (eventMessage.startsWith("KeyEvent:")) {
                processKeyEvent(eventMessage);
            } else if (eventMessage.startsWith("MouseEvent:")) {
                processMouseEvent(eventMessage);
            } else {
                clientGUI.updateLog("Unknown event type: " + eventMessage + "\n");
            }
        } catch (Exception e) {
            clientGUI.updateLog("Error processing event: " + e.getMessage() + "\n");
        }
    }

    private void processKeyEvent(String eventMessage) {
        String[] parts = eventMessage.split(":");
        if (parts.length >= 2) {
            try {
                int keyCode = Integer.parseInt(parts[1].trim());
                robot.keyPress(keyCode);
                robot.keyRelease(keyCode);
                clientGUI.updateLog("Key event processed: " + keyCode + "\n");
            } catch (NumberFormatException e) {
                clientGUI.updateLog("Invalid KeyEvent code: " + parts[1] + "\n");
            }
        } else {
            clientGUI.updateLog("Invalid KeyEvent format: " + eventMessage + "\n");
        }
    }

    private void processMouseEvent(String eventMessage) {
        String regex = "MouseEvent:\\s*(\\d+)\\s*at\\s*\\(([-\\d]+),\\s*([-\\d]+)\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(eventMessage);

        if (matcher.find()) {
            try {
                int button = Integer.parseInt(matcher.group(1));
                int x = Integer.parseInt(matcher.group(2));
                int y = Integer.parseInt(matcher.group(3));

                if (x >= 0 && y >= 0) {
                    robot.mouseMove(x, y);
                    clientGUI.updateLog("Mouse moved to: (" + x + ", " + y + ")\n");

                    int mask = 0;
                    if (button == 1) {
                        mask = InputEvent.BUTTON1_DOWN_MASK;
                    } else if (button == 2) {
                        mask = InputEvent.BUTTON2_DOWN_MASK;
                    } else if (button == 3) {
                        mask = InputEvent.BUTTON3_DOWN_MASK;
                    } else {
                        clientGUI.updateLog("Unknown mouse button: " + button + "\n");
                        return;
                    }
                    robot.mousePress(mask);
                    robot.mouseRelease(mask);
                } else {
                    clientGUI.updateLog("Ignored invalid MouseEvent coordinates: (" + x + ", " + y + ")\n");
                }
            } catch (NumberFormatException e) {
                clientGUI.updateLog("Invalid MouseEvent number format: " + eventMessage + "\n");
            }
        } else {
            clientGUI.updateLog("Invalid MouseEvent format: " + eventMessage + "\n");
        }
    }
}