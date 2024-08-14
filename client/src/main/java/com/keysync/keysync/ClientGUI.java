package com.keysync.keysync;

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
    }

    private void connectToServer() {
        String serverIp = serverIpField.getText();
        int serverPort = Integer.parseInt(serverPortField.getText());

        try {
            Socket socket = new Socket(serverIp, serverPort);
            updateLog("Connected to server at " + serverIp + ":" + serverPort + "\n");

            // Start a thread to listen for logs from the server
            new Thread(() -> listenForLogs(socket)).start();

        } catch (Exception e) {
            updateLog("Error: " + e.getMessage() + "\n");
        }
    }

    private void listenForLogs(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                updateLog(line);
            }
        } catch (Exception e) {
            updateLog("Connection lost: " + e.getMessage() + "\n");
        }
    }

    // Method to update the log area
    public void updateLog(String message) {
        logArea.append(message);
    }

    public static void main(String[] args) {
        new ClientGUI();
        new Client(new ClientGUI(), 8080).start();
    }
}