package com.Sofka.BingkBack.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class Card {

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
