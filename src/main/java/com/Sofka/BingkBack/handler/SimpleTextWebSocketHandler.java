package com.Sofka.BingkBack.handler;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleTextWebSocketHandler extends TextWebSocketHandler {

    // Contador de mensajes
    private static AtomicInteger messageCounter = new AtomicInteger(1);

    // Conjunto de sesiones WebSocket activas
    private static Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Agregar la nueva sesión a las sesiones activas
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Mensaje recibido: " + payload);

        // Obtener el número actual del mensaje y luego incrementar el contador
        int messageNumber = messageCounter.getAndIncrement();
        String responseMessage = "Mensaje #" + messageNumber + ": " + payload;

        // Enviar el mensaje a todas las sesiones activas
        for (WebSocketSession activeSession : sessions) {
            activeSession.sendMessage(new TextMessage(responseMessage));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Eliminar la sesión cerrada de las sesiones activas
        sessions.remove(session);
    }
}
