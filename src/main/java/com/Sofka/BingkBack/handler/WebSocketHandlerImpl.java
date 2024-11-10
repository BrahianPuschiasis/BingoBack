package com.Sofka.BingkBack.handler;

import com.Sofka.BingkBack.entity.Card;
import com.Sofka.BingkBack.entity.Game;
import com.Sofka.BingkBack.interfaces.ICardInterface;
import com.Sofka.BingkBack.service.CardService;
import com.Sofka.BingkBack.service.GameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.io.IOException;
import java.util.Random;
public class WebSocketHandlerImpl extends TextWebSocketHandler {

    private ScheduledFuture<?> gameTask;
    private static final ConcurrentHashMap<String, WebSocketSession> connectedUsers = new ConcurrentHashMap<>();
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static List<Integer> drawnNumbers = new ArrayList<>();
    private static boolean gameStarted = false;
    private static boolean countdownRunning = false;
    private static int countdownTime = 0;
    private static int currentNumber = -1;
    private static Random random = new Random();



    @Autowired
    private GameService gameService;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        URI uri = session.getUri();
        String username = extractUsername(uri);

        if (username != null) {
            session.getAttributes().put("username", username);
            connectedUsers.put(username, session);
            broadcastUsersList();
            checkGameStatus();
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

        }

        if ("start countdown".equals(msgPayload)) {
            startCountdown();
        }

        if (msgPayload.startsWith("Gano ")) {
            String winnerUsername = msgPayload.substring(5).trim();
            endGame(winnerUsername);
        }

        if (msgPayload.startsWith("disconnect")) {
            String usernameToDisconnect = msgPayload.substring(11).trim();
            connectedUsers.remove(usernameToDisconnect);
            broadcastUsersList();
            checkGameStatus();
        }


    }

    private void endGame(String winnerUsername) {
        if (!gameStarted) return;

        gameStarted = false;  // Finaliza el juego.
        String endMessage = "Se terminó el juego, ganó " + winnerUsername;
        broadcastMessage(endMessage);

        drawnNumbers.clear();
        currentNumber = -1;

        if (gameTask != null && !gameTask.isCancelled()) {
            gameTask.cancel(true);
        }

        connectedUsers.clear();
        broadcastUsersList();

        countdownTime = 0;
        countdownRunning = false;
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
        if (countdownRunning) return;

        int userCount = connectedUsers.size();
        if (userCount < 2) {
            broadcastMessage("No hay suficientes jugadores. Esperando...");
            return;
        }

        countdownRunning = true;
        countdownTime = 0;

        scheduler.scheduleAtFixedRate(() -> {
            countdownTime++;
            broadcastMessage("Tiempo: " + countdownTime + " segundos");

            if (countdownTime == 30) {
                startGame();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void startGame() {
        if (gameStarted) return;

        gameStarted = true;
        broadcastMessage("¡El juego ha comenzado!");

        String firstUser = connectedUsers.keySet().iterator().next();
        WebSocketSession firstUserSession = connectedUsers.get(firstUser);

        try {
            sendHostMessage(firstUserSession);
        } catch (IOException e) {
            e.printStackTrace();
        }

        gameTask = scheduler.scheduleAtFixedRate(() -> {
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

    private void sendHostMessage(WebSocketSession session) throws IOException {
        String message = "¡Eres el host! Eres responsable de crear la sala.";
        session.sendMessage(new TextMessage(message));

    }

    private void generateAndBroadcastNumber() {
        int randomNumber;

        do {
            // Si el juego ha terminado, salimos inmediatamente del ciclo
            if (!gameStarted) {
                System.out.println("El juego ha terminado. No se pueden generar más números.");
                return;
            }

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
        if (gameStarted && drawnNumbers.size() < 75) {
            return;
        }

        if (connectedUsers.size() < 2) {
            gameStarted = false;
            drawnNumbers.clear();
            currentNumber = -1;

            if (gameTask != null && !gameTask.isCancelled()) {
                gameTask.cancel(true);
            }

            broadcastMessage("No hay suficientes jugadores para comenzar. El juego está detenido.");
        }

        if (connectedUsers.size() == 0) {
            gameStarted = false;  // Detiene el juego
            drawnNumbers.clear();  // Limpia los números generados
            currentNumber = -1;    // Resetea el número actual
            countdownTime = 0;     // Reinicia el contador
            countdownRunning = false; // Detiene cualquier cuenta regresiva
            broadcastMessage("No hay jugadores, el juego está detenido.");
        }
    }
}
