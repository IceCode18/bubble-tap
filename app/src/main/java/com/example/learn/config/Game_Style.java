package com.example.learn.config;

import com.example.learn.objects.Event;
import com.example.learn.objects.NumberedSquares;

import java.util.List;

/**
 * Created by JHT.
 */

public interface Game_Style {

    String getNextLevelLabel();

    String getTryAgainLabel();

    List<String> getSquareLabels();

    Event getEvent(NumberedSquares c);
}
