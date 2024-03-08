package org.acme.webSocket;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.OnMessage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Classe représentant un point de terminaison client pour les tests WebSocket.
 * Elle est utilisée pour recevoir des messages WebSocket et décompter les verrous de comptage.
 */
@ClientEndpoint
public class TestClientEndPoint {

    private final CountDownLatch messageLatch; // Verrou de comptage pour synchroniser les messages reçus

    /**
     * Initialise un point de terminaison client avec un verrou de comptage donné.
     *
     * @param messageLatch Le verrou de comptage à utiliser pour synchroniser les messages reçus.
     */
    public TestClientEndPoint(CountDownLatch messageLatch) {
        this.messageLatch = messageLatch;
    }

    /**
     * Méthode appelée lorsqu'un message WebSocket est reçu.
     * Elle affiche le message reçu et décompte le verrou de comptage.
     *
     * @param message Le message WebSocket reçu.
     */
    @OnMessage
    public void onMessage(String message) {
        System.out.println("Message reçu : " + message); // Affiche le message reçu
        messageLatch.countDown(); // Décompte le verrou de comptage
    }
}
