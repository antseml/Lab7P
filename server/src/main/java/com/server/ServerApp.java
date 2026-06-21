package com.server;

import com.common.network.Request;
import com.common.network.RequestStatus;
import com.common.network.Response;
import com.server.manager.ServerCommandManager;
import com.server.network.UDPServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicReference;

public final class ServerApp {
    private final ServerCommandManager serverCommandManager;
    private final UDPServer udpServer;
    private final ForkJoinPool responsePool = new ForkJoinPool();
    private static final Logger logger = LogManager.getLogger(ServerApp.class);

    public ServerApp(ServerCommandManager serverCommandManager, UDPServer udpServer) {
        this.serverCommandManager = serverCommandManager;
        this.udpServer = udpServer;
    }

    public void run() {
        logger.info("Server is running");
        while (true) {
            AtomicReference<UDPServer.ReceivedRequest> receivedReference = new AtomicReference<>();
            Thread readerThread = new Thread(() -> {
                try {
                    receivedReference.set(udpServer.receiveRequest());
                } catch (Exception e) {
                    logger.error("Failed to read request", e);
                }
            });
            readerThread.start();

            try {
                readerThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            UDPServer.ReceivedRequest received = receivedReference.get();
            if (received == null) {
                continue;
            }

            Thread handlerThread = new Thread(() -> processRequest(received));
            handlerThread.start();
        }
    }

    private void processRequest(UDPServer.ReceivedRequest received) {
        Request request = received.request();
        logger.info("Received request from {}:{} - {}", received.clientAddress(), received.clientPort(), request);

        Response response;
        try {
            response = serverCommandManager.handleRequest(request);
        } catch (Exception e) {
            logger.error("Request processing error", e);
            response = new Response(RequestStatus.ERROR, "Server error: " + e.getMessage(), null);
        }

        Response finalResponse = response;
        responsePool.execute(() -> {
            try {
                udpServer.sendResponse(finalResponse, received.clientAddress(), received.clientPort());
                logger.info("Response sent to {}:{} - {}", received.clientAddress(), received.clientPort(), finalResponse);
            } catch (IOException e) {
                logger.error("Failed to send response", e);
            }
        });
    }
}
