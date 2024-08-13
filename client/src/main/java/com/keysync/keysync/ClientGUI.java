package com.keysync.keysync;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientGUI extends JFrame {
    private JTextField serverIpField;
    private JTextField serverPortField;
    private JButton connectButton;
    private JTextArea logArea;
    private Robot robot;

    public ClientGUI() {
        setTitle("Client Application");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new java.awt.GridLayout(3, 2));

        panel.add(new JLabel("Server IP:"));
        serverIpField = new JTextField("127.0.0.1");
        panel.add(serverIpField);

        panel.add(new JLabel("Server Port:"));
        serverPortField = new JTextField("8080");
        panel.add(serverPortField);

        connectButton = new JButton("Connect");
        panel.add(connectButton);

        logArea = new JTextArea();
        logArea.setEditable(false);

        add(panel, java.awt.BorderLayout.NORTH);
        add(new JScrollPane(logArea), java.awt.BorderLayout.CENTER);

        connectButton.addActionListener(e -> connectToServer());

        setVisible(true);

        try {
            robot = new Robot();
        } catch (Exception e) {
            logArea.append("Error initializing Robot: " + e.getMessage() + "\n");
        }
    }

    private void connectToServer() {
        String serverIp = serverIpField.getText();
        int serverPort = Integer.parseInt(serverPortField.getText());

        try {
            Socket socket = new Socket(serverIp, serverPort);
            logArea.append("Connected to server at " + serverIp + ":" + serverPort + "\n");

            // Start a thread to listen for events from the server
            new Thread(() -> listenForEvents(socket)).start();

        } catch (Exception e) {
            logArea.append("Error: " + e.getMessage() + "\n");
        }
    }

    private void listenForEvents(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                logArea.append("Received: " + line + "\n");
                processEvent(line);
            }
        } catch (Exception e) {
            logArea.append("Connection lost: " + e.getMessage() + "\n");
        }
    }

    private void processEvent(String eventMessage) {
        try {
            if (eventMessage.startsWith("KeyEvent:")) {
                // Example format: "KeyEvent: 65"
                String[] parts = eventMessage.split(":");
                if (parts.length >= 2) {
                    int keyCode = Integer.parseInt(parts[1].trim());
                    
                    if (keyCode >= KeyEvent.VK_0 && keyCode <= KeyEvent.VK_Z) {
                        robot.keyPress(keyCode);
                        robot.keyRelease(keyCode);
                    } else {
                        logArea.append("Invalid KeyEvent code: " + keyCode + "\n");
                    }
                } else {
                    logArea.append("Invalid KeyEvent format: " + eventMessage + "\n");
                }
            } else if (eventMessage.startsWith("MouseEvent:")) {
                // Example format: "MouseEvent: 1 at (100, 200)"
                String[] parts = eventMessage.split(" ");
                if (parts.length >= 4) {
                    int button = Integer.parseInt(parts[1].trim());
                    
                    String[] coords = parts[3].substring(1, parts[3].length() - 1).split(",");
                    if (coords.length == 2) {
                        int x = Integer.parseInt(coords[0].trim());
                        int y = Integer.parseInt(coords[1].trim());
    
                        robot.mouseMove(x, y);
                        
                        if (button == 1) {
                            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                        } else if (button == 2) {
                            robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
                            robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
                        } else if (button == 3) {
                            robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                            robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                        } else {
                            logArea.append("Unknown mouse button: " + button + "\n");
                        }
                    } else {
                        logArea.append("Invalid MouseEvent coordinates: " + eventMessage + "\n");
                    }
                } else {
                    logArea.append("Invalid MouseEvent format: " + eventMessage + "\n");
                }
            } else {
                logArea.append("Unknown event type: " + eventMessage + "\n");
            }
        } catch (Exception e) {
            logArea.append("Error processing event: " + e.getMessage() + "\n");
        }
    }
}