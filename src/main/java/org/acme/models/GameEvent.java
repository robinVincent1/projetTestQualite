package org.acme.models;

public class GameEvent {
    private String message; // message de l'événement

    //constructeur
    public GameEvent(String message) {
        this.message = message;
    }

    // Getters
    public String getMessage() {
        return message;
    }
}
