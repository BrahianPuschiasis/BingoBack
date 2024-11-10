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

    @Transactional
        public Game saveGame(Game game) {
        return gameRepository.save(game);
    }

    public Optional<Game> findGameById(Long id) {
        return gameRepository.findById(id);
    }

    public List<Game> findAllGames() {
        return gameRepository.findAll();
    }

    @Transactional
    public Game updateGame(Long id, Game gameDetails) {
        return gameRepository.findById(id).map(game -> {

            return gameRepository.save(game);
        }).orElseThrow(() -> new RuntimeException("Game not found with id " + id));
    }

    public void deleteGame(Long id) {
        gameRepository.deleteById(id);
    }
}
