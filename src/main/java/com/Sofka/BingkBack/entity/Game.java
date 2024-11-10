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

    private String ganador;

    @ElementCollection
    private List<Integer> columnB;

    @ElementCollection
    private List<Integer> columnI;

    @ElementCollection
    private List<Integer> columnN;

    @ElementCollection
    private List<Integer> columnG;

    @ElementCollection
    private List<Integer> columnO;

    @ElementCollection
    private List<Integer> numerosGenerados;


}
