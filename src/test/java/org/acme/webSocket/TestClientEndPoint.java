package org.acme.webSocket;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.OnMessage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@ClientEndpoint
public class TestClientEndPoint {

    private final CountDownLatch messageLatch;

    public TestClientEndPoint(CountDownLatch messageLatch) {
        this.messageLatch = messageLatch;
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Message reçu : " + message);
        messageLatch.countDown();
    }
}
