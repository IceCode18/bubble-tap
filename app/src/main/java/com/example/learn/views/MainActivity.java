package com.example.learn.views;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.example.learn.R;
import com.example.learn.config.GameSettings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Scanner;

public class MainActivity extends Activity {

    private MediaPlayer music;
    private NewView nv;

    /**
     * Starts up the music based on the preferred music choice and  sets the view.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.main_title);

        if (GameSettings.getBgMusic(this).equals("First Steps")){
            music = MediaPlayer.create(this, R.raw.firststeps);
        }
        else if(GameSettings.getBgMusic(this).equals("Pluck Loop")){
            music = MediaPlayer.create(this, R.raw.pluckloop);
        }
        if (GameSettings.getMusicOption(this) && music != null) {
            try {
                music.start();
                music.setLooping(true);
            } catch (IllegalStateException e) {
                throw new RuntimeException(e);
            }

        }

        nv = new NewView(this);
        setContentView(nv);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        music.release();
        nv.tickTimer(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (GameSettings.getMusicOption(this)) {
            music.pause();
            nv.tickTimer(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (GameSettings.getMusicOption(this)) {
            music.start();
        }
        nv.tickTimer(true);
    }

}
