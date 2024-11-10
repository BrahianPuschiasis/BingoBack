package com.Sofka.BingkBack.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ganador;  // Nombre del usuario ganador

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<PlayerGame> playerGames;  // Relación con los jugadores en el juego



}
