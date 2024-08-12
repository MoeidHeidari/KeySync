package com.keysync.keysync;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;


public class ServerGUI extends JFrame implements NativeKeyListener, NativeMouseInputListener {
    private JButton startButton;
    private JButton stopButton;
    private TextArea logArea;
    private Server server;
    private boolean isServerRunning = false;

    public ServerGUI() {
        setTitle("Server Application");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");
        stopButton.setEnabled(false);

        panel.add(startButton);
        panel.add(stopButton);

        logArea = new TextArea();
        logArea.setEditable(false);

        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        startButton.addActionListener(e -> startServer());
        stopButton.addActionListener(e -> stopServer());

        setVisible(true);
    }

    private void startServer() {
        if (!isServerRunning) {
            server = new Server(8080, logArea);
            new Thread(() -> server.start()).start();
            logArea.append("Server started on port 8080\n");

            try {
                GlobalScreen.registerNativeHook();
            } catch (NativeHookException e) {
                e.printStackTrace();
                return;
            }

            GlobalScreen.addNativeKeyListener(this);
            GlobalScreen.addNativeMouseListener(this);
            GlobalScreen.addNativeMouseMotionListener(this);

            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            isServerRunning = true;
        }
    }

    private void stopServer() {
        if (isServerRunning) {
            server.stop();
            logArea.append("Server stopped.\n");

            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException e) {
                e.printStackTrace();
            }

            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            isServerRunning = false;
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        server.sendEvent(e);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        server.sendEvent(e);
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        server.sendEvent(e);
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent e) {
        server.sendEvent(e);
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent e) {
        server.sendEvent(e);
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {
        server.sendEvent(e);
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent e) {
        server.sendEvent(e);
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent e) {
        server.sendEvent(e);
    }
}