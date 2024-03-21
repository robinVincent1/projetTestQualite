package org.acme.webSocket;

import org.junit.jupiter.api.Test;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import jakarta.websocket.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebSocketServerTest {

    private final CountDownLatch messageLatch = new CountDownLatch(1);


}