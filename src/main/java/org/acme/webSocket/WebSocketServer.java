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

    Map<String, Session> sessions = new ConcurrentHashMap<>();
    @Inject
    GameService gameService;

    public void onGameEvent(@Observes GameEventMessage event) {
        broadcast(event);
    }

    @OnOpen
    public void onOpen(Session session) {
        sessions.put(session.getId(), session);

        String initialState = gameService.getInitialState();

        System.out.println("Sending initial state to client: " + initialState);
        session.getAsyncRemote().sendText(initialState);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session.getId());
        // Gérer la déconnexion du client
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Gérer les erreurs
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received message: " + message);
        // Supposons que le message contient l'ID du joueur et son action sous la forme "playerId:deal/hit/stand/reload"
        String[] parts = message.split(":");
        if (parts.length == 2) {
            String playerId = parts[0];
            String action = parts[1];// Correction ici

            try {
                switch (action) {
                    case "deal" -> gameService.startGame();
                    case "hit" -> gameService.hit(playerId);
                    case "stand" -> gameService.stand(playerId);
                    case "reload" -> gameService.reload(playerId);
                    case "double" -> gameService.doubleDown(playerId);
                    case "assurance" -> gameService.insurance(playerId);
                }
            } catch (NumberFormatException e) {
                session.getAsyncRemote().sendText("Message invalide. Format attendu: 'playerId:action'.");
            }
        }
        if (parts.length == 3){
            String playerId = parts[0];
            String action = parts[1];
            String amount = parts[2];
            try {
                switch (action) {
                    case "bet" -> gameService.bet(playerId,amount);
                    case "pseudo" -> gameService.pseudo(playerId, amount);
                }
            } catch (NumberFormatException e) {
                session.getAsyncRemote().sendText("Message invalide. Format attendu: 'playerId:action:amount'.");
            }
        }
    }

    public void broadcast(Object message) {

        String jsonMessage;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonMessage = mapper.writeValueAsString(message);
            System.out.println("Broadcasting message: " + jsonMessage);
        } catch (JsonProcessingException e) {
            System.err.println("Erreur lors de la conversion du message en JSON : " + e.getMessage());
            jsonMessage = "{\"error\":\"Problème interne du serveur\"}";
        }

        final String finalJsonMessage = jsonMessage;

        // Envoi du message JSON à tous les clients connectés
        sessions.values().forEach(session -> {
            session.getAsyncRemote().sendText(finalJsonMessage, result -> {
                if (result.getException() != null) {
                    System.err.println("Erreur lors de l'envoi du message : " + result.getException().getMessage());
                }
            });
        });
    }


    private String convertChangeToJson(GameStateCardsChange change) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(change);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    public void sendToClient(String sessionId, Object message) {
        Session session = sessions.get(sessionId);
        if (session != null) {
            try {
                String jsonMessage = new ObjectMapper().writeValueAsString(message);
                session.getAsyncRemote().sendText(jsonMessage);
            } catch (JsonProcessingException e) {
                System.err.println("Erreur lors de la conversion du message en JSON : " + e.getMessage());
                // Gérer l'erreur, par exemple, en envoyant un message d'erreur générique au client
                session.getAsyncRemote().sendText("{\"error\":\"Problème interne du serveur\"}");
            }
        } else {
            System.err.println("Tentative d'envoi de message à une session inexistante : " + sessionId);
        }
    }

}