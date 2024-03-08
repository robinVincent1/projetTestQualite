package org.acme.webSocket;

import org.acme.models.GameStateCardsChange;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.acme.GameService;
import org.acme.models.GameEvent;
import org.acme.models.gameEvent.GameEventMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/game")
@ApplicationScoped
public class WebSocketServer {

    // Map pour stocker les sessions WebSocket
    Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Inject
    GameService gameService;

    // Méthode appelée lorsqu'un événement de jeu est observé
    public void onGameEvent(@Observes GameEventMessage event) {
        broadcast(event); // Diffuser l'événement à tous les clients connectés
    }

    // Méthode appelée lorsqu'une nouvelle connexion WebSocket est ouverte
    @OnOpen
    public void onOpen(Session session) {
        sessions.put(session.getId(), session); // Ajouter la session à la map

        // Envoyer l'état initial du jeu au nouveau client
        String initialState = gameService.getInitialState();
        session.getAsyncRemote().sendText(initialState);
    }

    // Méthode appelée lorsqu'une connexion WebSocket est fermée
    @OnClose
    public void onClose(Session session) {
        sessions.remove(session.getId()); // Supprimer la session de la map
        // Gérer la déconnexion du client
    }

    // Méthode appelée en cas d'erreur WebSocket
    @OnError
    public void onError(Session session, Throwable throwable) {
        // Gérer les erreurs
    }

    // Méthode appelée lorsqu'un message est reçu depuis un client WebSocket
    @OnMessage
    public void onMessage(String message, Session session) {
        // Traiter le message reçu
        // (supposons que le message contient l'ID du joueur et son action)
        // (par exemple, "playerId:action")
        String[] parts = message.split(":");
        if (parts.length == 2) {
            String playerId = parts[0];
            String action = parts[1];
            try {
                // Exécuter l'action correspondante en fonction du message reçu
                switch (action) {
                    case "deal" -> gameService.startGame();
                    case "hit" -> gameService.hit(playerId);
                    case "stand" -> gameService.stand(playerId);
                    case "reload" -> gameService.reload(playerId);
                    case "double" -> gameService.doubleDown(playerId);
                    case "assurance" -> gameService.insurance(playerId);
                }
            } catch (NumberFormatException e) {
                // Envoyer un message d'erreur au client en cas de format invalide
                session.getAsyncRemote().sendText("Message invalide. Format attendu: 'playerId:action'.");
            }
        } else if (parts.length == 3) { // Correction ici
            // Traiter un message avec trois parties (par exemple, "playerId:action:amount")
            String playerId = parts[0];
            String action = parts[1];
            String amount = parts[2];
            try {
                // Exécuter l'action correspondante avec un montant en fonction du message reçu
                switch (action) {
                    case "bet" -> gameService.bet(playerId, amount);
                    case "pseudo" -> gameService.pseudo(playerId, amount);
                }
            } catch (NumberFormatException e) {
                // Envoyer un message d'erreur au client en cas de format invalide
                session.getAsyncRemote().sendText("Message invalide. Format attendu: 'playerId:action:amount'.");
            }
        }
    }

    // Méthode pour diffuser un message à tous les clients connectés
    public void broadcast(Object message) {
        String jsonMessage;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonMessage = mapper.writeValueAsString(message); // Convertir l'objet en JSON
            System.out.println("Broadcasting message: " + jsonMessage);
        } catch (JsonProcessingException e) {
            // Gérer les erreurs de conversion JSON
            System.err.println("Erreur lors de la conversion du message en JSON : " + e.getMessage());
            jsonMessage = "{\"error\":\"Problème interne du serveur\"}";
        }

        final String finalJsonMessage = jsonMessage;
        // Envoyer le message JSON à tous les clients connectés
        sessions.values().forEach(session -> {
            session.getAsyncRemote().sendText(finalJsonMessage, result -> {
                if (result.getException() != null) {
                    // Gérer les erreurs d'envoi
                    System.err.println("Erreur lors de l'envoi du message : " + result.getException().getMessage());
                }
            });
        });
    }

    // Méthode pour convertir un GameStateCardsChange en JSON
    private String convertChangeToJson(GameStateCardsChange change) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(change);
        } catch (JsonProcessingException e) {
            // Gérer les erreurs de conversion JSON
            e.printStackTrace();
            return "{}";
        }
    }

    // Méthode pour envoyer un message à un client spécifique
    public void sendToClient(String sessionId, Object message) {
        Session session = sessions.get(sessionId);
        if (session != null) {
            try {
                String jsonMessage = new ObjectMapper().writeValueAsString(message);
                session.getAsyncRemote().sendText(jsonMessage); // Envoyer le message JSON au client
            } catch (JsonProcessingException e) {
                // Gérer les erreurs de conversion JSON
                System.err.println("Erreur lors de la conversion du message en JSON : " + e.getMessage());
                // Envoyer un message d'erreur générique au client
                session.getAsyncRemote().sendText("{\"error\":\"Problème interne du serveur\"}");
            }
        } else {
            // Gérer le cas où la session est introuvable
            System.err.println("Tentative d'envoi de message à une session inexistante : " + sessionId);
        }
    }
}
