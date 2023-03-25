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

public class Multiply implements Game_Style {

    private int level, current, starting;
    private List<String> spheres;
    private int gameOver = 11;
    private Context c;

    public Multiply(Context context){
        c = context;
        level = 1;
        starting = 3;
        current = 0;
    }

    @Override
    public String toString(){
        return "Multiply";
    }

    @Override
    public String getNextLevelLabel() {
        return c.getString(R.string.mult_nextLevel, level);
    }

    @Override
    public String getTryAgainLabel(){
        return c.getString(R.string.mult_tryAgain, level);
    }
    @Override
    public List<String> getSquareLabels(){
        List<String> labels = new ArrayList<String>();
        for(int n=level; n<=starting*level; n+=level){
            labels.add("" + n);
        }
        spheres = labels;
        return labels;
    }

    @Override
    public Event getEvent(NumberedSquares c){
        String targetLabel = spheres.get(current);
        if(!c.getLabel().equals(targetLabel)){
            return Event.TRY_AGAIN;
        }
        current++;
        if(current==spheres.size()){
            current = 0;
            level++;
            starting++;
            if(starting==gameOver){
                level = 1;
                starting = 3;
                current = 0;
                return Event.Game_Over;
            }
            return Event.LEVEL_COMPLETE;
        }
        else{
            return Event.CONTINUE;
        }

    }

}