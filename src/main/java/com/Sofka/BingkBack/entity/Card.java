package com.Sofka.BingkBack.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private List<Integer> columnB;
    private List<Integer> columnI;
    private List<Integer> columnN;
    private List<Integer> columnG;
    private List<Integer> columnO;


    public Card(List<Integer> columnB, List<Integer> columnI, List<Integer> columnN, List<Integer> columnG, List<Integer> columnO) {
        this.columnB = columnB;
        this.columnI = columnI;
        this.columnN = columnN;
        this.columnG = columnG;
        this.columnO = columnO;
    }
}
