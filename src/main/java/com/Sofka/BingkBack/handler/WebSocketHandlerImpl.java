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
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WebSocketHandlerImpl extends TextWebSocketHandler {

    private static final ConcurrentHashMap<String, WebSocketSession> connectedUsers = new ConcurrentHashMap<>();
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static List<Integer> drawnNumbers = new ArrayList<>();
    private static boolean gameStarted = false;
    private static boolean countdownRunning = false;
    private static int countdownTime = 0;
    private static int currentNumber = -1;
    private static Random random = new Random();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        URI uri = session.getUri();
        String username = extractUsername(uri);

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

        if ("crear tarjeta".equals(msgPayload) && username != null) {
            ICardInterface cardGenerator = new CardService();
            Card newCard = cardGenerator.generateCard();

            ObjectMapper mapper = new ObjectMapper();
            String cardJson = mapper.writeValueAsString(newCard);

            session.sendMessage(new TextMessage("Tarjeton:" + cardJson));
        }

        if ("iniciar juego".equals(msgPayload)) {
            startGame();
        }

        if ("start countdown".equals(msgPayload)) {
            startCountdown();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = (String) session.getAttributes().get("username");
        if (username != null) {
            connectedUsers.remove(username);
            broadcastUsersList();
            checkGameStatus();
        }
    }

    private String extractUsername(URI uri) {
        String query = uri.getQuery();
        if (query != null) {
            for (String param : query.split("&")) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && "username".equals(keyValue[0])) {
                    return keyValue[1];
                }
            }
        }
        return null;
    }

    private void broadcastUsersList() {
        List<String> usersList = new ArrayList<>(connectedUsers.keySet());
        String usersMessage = "Usuarios conectados: " + String.join(", ", usersList);

        for (WebSocketSession session : connectedUsers.values()) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(usersMessage));
                }
            } catch (IOException e) {
                System.out.println("Error al enviar mensaje a la sesión " + session.getId() + ": " + e.getMessage());
            }
        }
    }

    private void startCountdown() {
        if (countdownRunning) return; // Evita reiniciar si ya está en marcha

        countdownRunning = true;
        countdownTime = 0;

        scheduler.scheduleAtFixedRate(() -> {
            countdownTime++;

            int userCount = connectedUsers.size();
            broadcastMessage("Tiempo: " + countdownTime + " segundos");

            if (userCount >= 2 && countdownTime == 30) {
                countdownRunning = false;
                startGame(); // Inicia el juego
            } else if (countdownTime >= 60 && userCount < 2) {
                countdownRunning = false;
                countdownTime = 0;
                broadcastMessage("Reinicio del contador, no hay suficientes jugadores.");
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void startGame() {
        if (gameStarted) return; // Evita iniciar si el juego ya comenzó

        gameStarted = true;
        broadcastMessage("¡El juego ha comenzado!");

        scheduler.scheduleAtFixedRate(() -> {
            while (gameStarted && drawnNumbers.size() < 75) {
                generateAndBroadcastNumber();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            if (drawnNumbers.size() == 75) {
                broadcastMessage("¡El juego ha terminado! Todos los números han sido generados.");
                gameStarted = false; // Finaliza el juego
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    private void generateAndBroadcastNumber() {
        int randomNumber;

        do {
            randomNumber = random.nextInt(75) + 1;
        } while (drawnNumbers.contains(randomNumber));

        drawnNumbers.add(randomNumber);
        currentNumber = randomNumber;

        System.out.println("Número generado: " + currentNumber);

        String numberMessage = "Número generado: " + currentNumber;
        broadcastMessage(numberMessage);
    }

    private void broadcastMessage(String message) {
        for (WebSocketSession session : connectedUsers.values()) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                System.out.println("Error al enviar mensaje a la sesión " + session.getId() + ": " + e.getMessage());
            }
        }
    }

    private void checkGameStatus() {
        if (connectedUsers.size() < 2) {
            gameStarted = false;
            drawnNumbers.clear();
            currentNumber = -1;
        }
    }
}
