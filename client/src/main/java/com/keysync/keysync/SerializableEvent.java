package com.keysync.keysync;
import java.io.Serializable;

public class SerializableEvent implements Serializable {
    private String eventType;
    private int keyCode;
    private int mouseX;
    private int mouseY;
    private int button;

    public SerializableEvent(String eventType, int keyCode) {
        this.eventType = eventType;
        this.keyCode = keyCode;
    }

    public SerializableEvent(String eventType, int mouseX, int mouseY, int button) {
        this.eventType = eventType;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.button = button;
    }

    public String getEventType() {
        return eventType;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public int getButton() {
        return button;
    }
}
