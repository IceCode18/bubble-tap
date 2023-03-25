package com.example.learn.config;

import android.content.Context;
import android.util.Log;

import com.example.learn.R;
import com.example.learn.objects.Event;
import com.example.learn.objects.NumberedSquares;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by JHT.
 */

public class SpellingGame implements Game_Style {

    private int level;
    private int current;
    private List<String> words;
    private String[] word;
    private int cWord = 0;
    private String splitWord;
    private Context c;

    public SpellingGame(Context context){
        c = context;
        level = 1;
        current = 0;
        String[] wordList = {"HI", "MOM", "LOVE", "SMILE", "WALKING"};
        words = Arrays.asList(wordList);
        setWord();
    }

    /**
     * Chooses a word and splits it into an array.
     */
    private void setWord(){
        if (cWord==words.size()){
            cWord = 0;
        }
        splitWord = words.get(cWord);
        word = splitWord.split("");
    }

    @Override
    public String toString(){
        return "SpellingGame";
    }

    @Override
    public String getNextLevelLabel() {
        return c.getString(R.string.spell_nextLevel,splitWord);
    }

    @Override
    public String getTryAgainLabel(){
        return c.getString(R.string.spell_tryAgain,word[current]);
    }

    @Override
    public List<String> getSquareLabels(){
        List<String> labels = new ArrayList<String>();
        for(int n=0; n<word.length; n++){
            labels.add(word[n]);
        }

        return labels;
    }

    @Override
    public Event getEvent(NumberedSquares c){
        if(!word[current].equals(c.getLabel())){
            return Event.TRY_AGAIN;
        }
        if(current==(word.length-1)){
            current = 0;
            level++;
            cWord++;
            setWord();
            if(level>words.size()){
                level=1;
                return Event.Game_Over;
            }
            return Event.LEVEL_COMPLETE;
        }
        else{
            current++;
            return Event.CONTINUE;
        }
    }

}
