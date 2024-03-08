package org.acme.models.gameEvent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.acme.models.GameEvent;

public class GameEventMessage {
    private String eventType; // Type de l'événement
    private Object eventData; // Données associées à l'événement

    // Constructeur
    public GameEventMessage(String eventType, Object eventData) {
        this.eventType = eventType;
        this.eventData = eventData;
    }

    // Méthode pour convertir l'objet en format JSON
    public String toJson() {
        ObjectMapper mapper = new ObjectMapper(); // Création d'un objet ObjectMapper pour la sérialisation JSON
        try {
            return mapper.writeValueAsString(this); // Conversion de l'objet en chaîne JSON et retour
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // Affichage de l'erreur de sérialisation
            return "{}"; // Retourne un objet JSON vide en cas d'erreur
        }
    }

    // Getters pour accéder aux attributs
    public String getEventType() {
        return eventType;
    }

    public Object getEventData() {
        return eventData;
    }
}
