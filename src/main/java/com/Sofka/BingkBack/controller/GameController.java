package com.Sofka.BingkBack.controller;

import com.Sofka.BingkBack.entity.Game;
import com.Sofka.BingkBack.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/game")  // Ruta base para todos los endpoints de Game
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    // Endpoint para crear un nuevo Game (POST)
    @PostMapping
    public ResponseEntity<Game> createGame(@RequestBody Game game) {
        Game savedGame = gameService.saveGame(game);
        return new ResponseEntity<>(savedGame, HttpStatus.CREATED);  // Devuelve el Game creado con el estado 201
    }

    // Endpoint para obtener todos los Games (GET)
    @GetMapping
    public List<Game> getAllGames() {
        return gameService.findAllGames();
    }

    // Endpoint para obtener un Game por ID (GET)
    @GetMapping("/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable Long id) {
        Optional<Game> game = gameService.findGameById(id);
        return game.map(ResponseEntity::ok) // Si existe el Game, se devuelve con estado 200
                .orElseGet(() -> ResponseEntity.notFound().build()); // Si no se encuentra, se devuelve 404
    }

    // Endpoint para actualizar un Game (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<Game> updateGame(@PathVariable Long id, @RequestBody Game gameDetails) {
        try {
            Game updatedGame = gameService.updateGame(id, gameDetails);
            return ResponseEntity.ok(updatedGame); // Devuelve el Game actualizado con estado 200
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Devuelve 404 si el Game no existe
        }
    }

    // Endpoint para eliminar un Game por ID (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return ResponseEntity.noContent().build(); // Devuelve 204 si la eliminación es exitosa
    }
}
