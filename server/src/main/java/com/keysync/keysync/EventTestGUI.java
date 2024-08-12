package com.keysync.keysync;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;

public class EventTestGUI extends JFrame implements NativeKeyListener, NativeMouseInputListener {

    private JTextArea logArea;

    public EventTestGUI() {
        // Set up the frame
        setTitle("Event Test");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set up the log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane, BorderLayout.CENTER);

        // Set up the panel for interaction
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);

        // Label instructions
        JLabel instructions = new JLabel("Global events are being logged:");
        panel.add(instructions, BorderLayout.NORTH);

        // Register native hook
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        // Add key and mouse listeners
        GlobalScreen.addNativeKeyListener(this);
        GlobalScreen.addNativeMouseListener(this);
        GlobalScreen.addNativeMouseMotionListener(this);

        setVisible(true);
    }

    // Implement NativeKeyListener methods
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        logArea.append("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()) + " Code: " + e.getKeyCode() + "\n");
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        logArea.append("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()) + " Code: " + e.getKeyCode() + "\n");
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // Optional: handle key typed if necessary
    }

    // Implement NativeMouseInputListener methods
    @Override
    public void nativeMouseClicked(NativeMouseEvent e) {
        logArea.append("Mouse Clicked: " + e.getX() + ", " + e.getY() + " Button: " + e.getButton() + "\n");
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent e) {
        logArea.append("Mouse Pressed: " + e.getX() + ", " + e.getY() + " Button: " + e.getButton() + "\n");
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {
        logArea.append("Mouse Released: " + e.getX() + ", " + e.getY() + " Button: " + e.getButton() + "\n");
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent e) {
        logArea.append("Mouse Moved: " + e.getX() + ", " + e.getY() + "\n");
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent e) {
        logArea.append("Mouse Dragged: " + e.getX() + ", " + e.getY() + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EventTestGUI::new);
    }
}
