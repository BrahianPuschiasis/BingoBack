package com.Sofka.BingkBack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class Card {


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

    public Card(List<Integer> columnB, List<Integer> columnI, List<Integer> columnN, List<Integer> columnG, List<Integer> columnO) {
        this.columnB = columnB;
        this.columnI = columnI;
        this.columnN = columnN;
        this.columnG = columnG;
        this.columnO = columnO;
    }
}

