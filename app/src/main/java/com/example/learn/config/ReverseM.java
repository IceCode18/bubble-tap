package com.example.learn.config;

import android.content.Context;
import android.util.Log;

import com.example.learn.R;
import com.example.learn.objects.Event;
import com.example.learn.objects.NumberedSquares;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JHT.
 */

public class ReverseM implements Game_Style {

    private int level, current, starting;
    private List<String> spheres;
    private int gameOver = 11;
    private Context c;

    public ReverseM(Context context){
        c = context;
        level = 1;
        starting = 3;
        current = starting;
    }

    @Override
    public String toString(){
        return "ReverseM";
    }

    @Override
    public String getNextLevelLabel() {
        return c.getString(R.string.revMult_nextLevel,level);
    }

    @Override
    public String getTryAgainLabel(){
        return c.getString(R.string.revMult_tryAgain,  spheres.get(current-1));
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
        String targetLabel = spheres.get(current-1);
        if(!c.getLabel().equals(targetLabel)){
            return Event.TRY_AGAIN;
        }
        current--;
        if(current==0){
            level++;
            starting++;
            current = starting;
            if(starting==gameOver){
                level = 1;
                starting = 3;
                current = starting;
                return Event.Game_Over;
            }
            return Event.LEVEL_COMPLETE;
        }
        else{
            return Event.CONTINUE;
        }

    }

}