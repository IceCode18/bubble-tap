package com.example.learn.config;

import android.content.Context;
import android.util.Log;

import com.example.learn.R;
import com.example.learn.config.Game_Style;
import com.example.learn.objects.Event;
import com.example.learn.objects.NumberedSquares;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JHT.
 */

public class CountingGame implements Game_Style {

    private int level;
    private int current;
    private int gameOver=6;
    private Context c;

    public CountingGame(Context context){
        level = 1;
        current = 0;
        c = context;
    }

    @Override
    public String toString(){
        return "CountingGame";
    }
    @Override
    public String getNextLevelLabel(){
        return c.getString(R.string.d_level) + level;
    }

    @Override
    public String getTryAgainLabel(){
        return c.getString(R.string.counting_tryAgain);
    }

    @Override
    public List<String> getSquareLabels(){
        List<String> labels = new ArrayList<String>();
        for (int i = 0; i<level; i++){
            int l = i+1;
            labels.add("" +l);
        }
        return labels;
    }

    @Override
    public Event getEvent(NumberedSquares c){
        if(c.getID()-current!=1){
            return Event.TRY_AGAIN;
        }
        current++;
        if(current==level){
            current = 0;
            level++;
            if(level==gameOver){
                level=1;
                return Event.Game_Over;
            }
            return Event.LEVEL_COMPLETE;
        }
        else{
            return Event.CONTINUE;
        }

    }



}
