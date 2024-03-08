package org.acme.webSocket;

import org.junit.jupiter.api.Test;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import jakarta.websocket.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebSocketServerTest {

    private final CountDownLatch messageLatch = new CountDownLatch(1);

    @Test
    public void testWebSocket() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        String uri = "ws://localhost:8080/game"; // Remplacez par l'URL de votre WebSocket
        CountDownLatch messageLatch = new CountDownLatch(1);
        Session session = container.connectToServer(new TestClientEndPoint(messageLatch), URI.create(uri));

        session.getBasicRemote().sendText("test message");

        assertTrue(messageLatch.await(3, TimeUnit.SECONDS), "Le message attendu n'a pas été reçu");
        session.close();
    }

}