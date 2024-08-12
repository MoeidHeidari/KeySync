package com.keysync.keysync;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import javax.swing.JTextArea;

class EventReceiver implements Runnable {

    private final Socket socket;
    private final JTextArea logArea;
    private Robot robot;

    public EventReceiver(Socket socket, JTextArea logArea) {
        this.socket = socket;
        this.logArea = logArea;
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            logArea.append("Error creating Robot instance: " + e.getMessage() + "\n");
        }
    }

    @Override
    public void run() {
        try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
            SerializableEvent event;
            while ((event = (SerializableEvent) inputStream.readObject()) != null) {
                processEvent(event);
            }
        } catch (IOException | ClassNotFoundException e) {
            logArea.append("Error: " + e.getMessage() + "\n");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                logArea.append("Error closing socket: " + e.getMessage() + "\n");
            }
        }
    }

    private void processEvent(SerializableEvent event) {
        switch (event.getEventType()) {
            case "MouseMoved" -> robot.mouseMove(event.getMouseX(), event.getMouseY());
            case "MousePressed" -> {
                int buttonMask = switch (event.getButton()) {
                    case 1 -> InputEvent.BUTTON1_DOWN_MASK;
                    case 2 -> InputEvent.BUTTON2_DOWN_MASK;
                    case 3 -> InputEvent.BUTTON3_DOWN_MASK;
                    default -> 0;
                };
                robot.mousePress(buttonMask);
            }
            case "MouseReleased" -> {
                int buttonMask = switch (event.getButton()) {
                    case 1 -> InputEvent.BUTTON1_DOWN_MASK;
                    case 2 -> InputEvent.BUTTON2_DOWN_MASK;
                    case 3 -> InputEvent.BUTTON3_DOWN_MASK;
                    default -> 0;
                };
                robot.mouseRelease(buttonMask);
            }
            case "KeyPressed" -> robot.keyPress(event.getKeyCode());
            case "KeyReleased" -> robot.keyRelease(event.getKeyCode());
            default -> logArea.append("Unknown event type: " + event.getEventType() + "\n");
        }
        logArea.append("Processed event: " + event.getEventType() + "\n");
    }
}
