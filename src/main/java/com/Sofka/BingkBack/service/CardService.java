package com.Sofka.BingkBack.service;

import com.Sofka.BingkBack.entity.Card;
import com.Sofka.BingkBack.interfaces.ICardInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardService implements ICardInterface {
    @Override
    public Card generateCard() {
        List<Integer> columnB = generateColumn(1, 15);
        List<Integer> columnI = generateColumn(16, 30);
        List<Integer> columnN = generateColumn(31, 45);
        columnN.set(2, 0);
        List<Integer> columnG = generateColumn(46, 60);
        List<Integer> columnO = generateColumn(61, 75);

        return new Card(columnB, columnI, columnN, columnG, columnO);
    }

    private List<Integer> generateColumn(int min, int max) {
        List<Integer> column = new ArrayList<>();
        for (int i = min; i <= max; i++) {
            column.add(i);
        }
        Collections.shuffle(column);
        return column.subList(0, 5);
    }
}
