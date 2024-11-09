package com.Sofka.BingkBack.handler;

import com.Sofka.BingkBack.entity.Card;
import com.Sofka.BingkBack.interfaces.ICardInterface;
import com.Sofka.BingkBack.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandlerImpl extends TextWebSocketHandler {

    private static final ConcurrentHashMap<String, WebSocketSession> connectedUsers = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        URI uri = session.getUri();
        String query = uri.getQuery();
        String username = null;

        if (query != null) {
            for (String param : query.split("&")) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && "username".equals(keyValue[0])) {
                    username = keyValue[1];
                    break;
                }
            }
        }

        if (username != null) {
            session.getAttributes().put("username", username);
            connectedUsers.put(username, session);
            broadcastUsersList();
        } else {
            session.close(CloseStatus.BAD_DATA);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String username = (String) session.getAttributes().get("username");
        String msgPayload = message.getPayload();

        System.out.println("Mensaje recibido del usuario '" + username + "': " + msgPayload);

        if ("crear tarjeta".equals(msgPayload) && username != null) {
            ICardInterface cardGenerator = new CardService();
            Card newCard = cardGenerator.generateCard();

            ObjectMapper mapper = new ObjectMapper();
            String cardJson = mapper.writeValueAsString(newCard);

            System.out.println("Tarjetón generado para " + username + ": " + cardJson);

            session.sendMessage(new TextMessage("Tarjeton:" + cardJson));
        } else {
            System.out.println("El mensaje no coincide con 'crear tarjeta' o el usuario es nulo.");
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = (String) session.getAttributes().get("username");
        if (username != null) {
            connectedUsers.remove(username);
            broadcastUsersList();
        }
    }

    private void broadcastUsersList() {
        List<String> usersList = new ArrayList<>(connectedUsers.keySet());
        String usersMessage = "Usuarios conectados: " + String.join(", ", usersList);

        List<WebSocketSession> closedSessions = new ArrayList<>();

        for (WebSocketSession session : connectedUsers.values()) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(usersMessage));
                }
            } catch (Exception e) {
                System.out.println("Error al enviar mensaje a la sesión " + session.getId() + ": " + e.getMessage());
                closedSessions.add(session);
            }
        }

        for (WebSocketSession session : closedSessions) {
            connectedUsers.values().remove(session);
        }
    }
}
