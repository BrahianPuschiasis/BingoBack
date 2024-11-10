package com.Sofka.BingkBack.service;


import com.Sofka.BingkBack.entity.Game;
import com.Sofka.BingkBack.repository.GameRepository;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    private final GameRepository gameRepository;

    @Autowired
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    // Método para guardar un nuevo Game
    @Transactional
        public Game saveGame(Game game) {
        return gameRepository.save(game);
    }

    // Método para obtener un Game por ID
    public Optional<Game> findGameById(Long id) {
        return gameRepository.findById(id);
    }

    // Método para obtener todos los Games
    public List<Game> findAllGames() {
        return gameRepository.findAll();
    }

    // Método para actualizar un Game existente
    @Transactional
    public Game updateGame(Long id, Game gameDetails) {
        return gameRepository.findById(id).map(game -> {
            // Actualizar los atributos necesarios de game aquí, por ejemplo:
            // game.setName(gameDetails.getName());
            // game.setDescription(gameDetails.getDescription());
            return gameRepository.save(game);
        }).orElseThrow(() -> new RuntimeException("Game not found with id " + id));
    }

    // Método para eliminar un Game por ID
    public void deleteGame(Long id) {
        gameRepository.deleteById(id);
    }
}
