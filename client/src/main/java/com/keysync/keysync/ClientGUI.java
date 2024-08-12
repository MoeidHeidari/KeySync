package com.keysync.keysync;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

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

        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        connectButton.addActionListener((ActionEvent e) -> {
            connectToServer();
        });

        setVisible(true);
    }

    private void connectToServer() {
        String serverIp = serverIpField.getText();
        int serverPort = Integer.parseInt(serverPortField.getText());

        try {
            Socket socket = new Socket(serverIp, serverPort);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            out.println(socket.getLocalAddress().getHostAddress());
            out.println("9090"); // The client's listening port

            logArea.append("Connected to server at " + serverIp + ":" + serverPort + "\n");

            new Thread(new ClientServer(9090, logArea)).start();

        } catch (IOException e) {
            logArea.append("Error: " + e.getMessage() + "\n");
        }
    }
}