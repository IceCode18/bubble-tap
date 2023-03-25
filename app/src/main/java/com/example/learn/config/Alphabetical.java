package com.example.learn.config;


import android.content.Context;

import com.example.learn.R;
import com.example.learn.objects.Event;
import com.example.learn.objects.NumberedSquares;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JHT.
 */

public class Alphabetical implements Game_Style {

    private int level;
    private int current;
    private String[] set;
    private Context c;

    public Alphabetical(Context context){
        level = 1;
        current = 0;
        c = context;
//        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String letters = "ABCDEFG";
        set = letters.split("");
    }

    @Override
    public String toString(){
        return "Alphabetical";
    }

    @Override
    public String getNextLevelLabel() {
        return c.getString(R.string.d_level) + level;
    }

    @Override
    public String getTryAgainLabel(){
        return c.getString(R.string.alpha_tryAgain, set[current]);
    }

    @Override
    public List<String> getSquareLabels(){
        List<String> labels = new ArrayList<String>();
        if (level==set.length){
            level=1;
        }
        for (int i = 0; i<level; i++){
            labels.add(set[i]);
        }
        return labels;
    }

    @Override
    public Event getEvent(NumberedSquares c){
        if(!set[current].equals(c.getLabel())){
            return Event.TRY_AGAIN;
        }
        if(current==(level-1)){
            current = 0;
            level++;
            if(level>=set.length){
                level=1;
                return Event.Game_Over;
            }
            return Event.LEVEL_COMPLETE;
        }
        else {
            current++;
            return Event.CONTINUE;
        }
    }

}
