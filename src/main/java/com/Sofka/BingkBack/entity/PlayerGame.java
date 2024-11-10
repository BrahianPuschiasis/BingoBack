package com.Sofka.BingkBack.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class PlayerGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;  // Relación con el juego

    private String username;  // Nombre de usuario del jugador

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "card_id")
    private Card card;  // La tarjeta de bingo del jugador

    @ElementCollection
    private List<Integer> selectedNumbers;  // Números seleccionados por el jugador

    private boolean won;

}
