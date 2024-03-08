package org.acme.webSocket;

import org.junit.jupiter.api.Test;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import jakarta.websocket.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Classe de test pour le serveur WebSocket.
 * Effectue un test de connexion et d'échange de messages avec le serveur WebSocket.
 */
public class WebSocketServerTest {

    private final CountDownLatch messageLatch = new CountDownLatch(1); // Verrou de comptage pour synchroniser les messages reçus

    /**
     * Teste la connexion et l'échange de messages avec le serveur WebSocket.
     *
     * @throws Exception En cas d'erreur lors de la connexion WebSocket.
     */
    @Test
    public void testWebSocket() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        String uri = "ws://localhost:8080/game"; // URL du serveur WebSocket (à remplacer par l'URL appropriée)
        CountDownLatch messageLatch = new CountDownLatch(1); // Verrou de comptage pour synchroniser les messages reçus
        Session session = container.connectToServer(new TestClientEndPoint(messageLatch), URI.create(uri)); // Connexion au serveur WebSocket

        session.getBasicRemote().sendText("test message"); // Envoi d'un message de test au serveur WebSocket

        assertTrue(messageLatch.await(3, TimeUnit.SECONDS), "Le message attendu n'a pas été reçu"); // Attend la réception d'un message avec un délai de 3 secondes
        session.close(); // Fermeture de la session WebSocket
    }
}
