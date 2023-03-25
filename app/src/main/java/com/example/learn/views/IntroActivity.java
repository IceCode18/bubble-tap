package com.example.learn.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.example.learn.R;
import com.example.learn.config.GameSettings;

/**
 * Created by JHT.
 */

public class IntroActivity extends Activity{

    private Splash screen;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        screen = new Splash(this);
        setContentView(screen);
    }

    /**
     * Detects button touches and performs actions triggered by the pushed button.
     * @param m
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent m) {
        if (m.getAction() == MotionEvent.ACTION_DOWN) {
            float x = m.getX();
            float y = m.getY();
            RectF about = new RectF(screen.getAbout());
            RectF settings = new RectF(screen.getSettings());
            RectF play = new RectF(screen.getPlayGame());
            //if user tapped lower-right corner, launch
            //the preferences screen. Otherwise, launch
            //main activity.
            if (settings.contains(x,y)) {
                Intent i = new Intent(this, GameSettings.class);
                startActivity(i);
            } else if (about.contains(x,y)) {
                AlertDialog.Builder ab = new AlertDialog.Builder(this);
                ab.setTitle(R.string.about_title)
                        .setMessage(R.string.about_message)
                        .setNeutralButton(R.string.about_neutral, null);
                AlertDialog box = ab.create();
                box.show();
            } else if (play.contains(x,y)) {
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }
        return true;
    }


    /**
     * Splash screen is created and customized using an inner class.
     */
    private class Splash extends android.support.v7.widget.AppCompatImageView{

        private Bitmap info,setting, game;
        private RectF about, settings, playGame;

        private Splash(Context c) {
            super(c);
            setImageResource(R.drawable.splash);
            setScaleType(ImageView.ScaleType.FIT_XY);
        }

        /**
         * Draws the buttons.
         * @param c - Canvas
         */
        @Override
        public void onDraw(Canvas c){
            super.onDraw(c);
            float top = this.getHeight() * .9f;
            float w = this.getWidth();
            int base = (int) (w * .1f);
            Paint p = new Paint();
            setImages(base);
            about = new RectF(w*.05f, top, w*.05f+base, top+base);
            settings = new RectF(w*.85f, top, w*.85f+base, top+base);
            playGame = new RectF((w/2)-(base*2.5f), top*.6f, (w/2)+(base*2.5f), (top*.6f)+(base*2) );
            c.drawBitmap(info, about.left, about.top, p);
            c.drawBitmap(setting,settings.left, settings.top, p);
            c.drawBitmap(game,playGame.left,playGame.top,p);
        }

        /**
         * Sets the bitmap for the button images.
         * @param width
         */
        private void setImages(int width){
            info = BitmapFactory.decodeResource(this.getResources(), R.drawable.about);
            info = Bitmap.createScaledBitmap(info,
                    width, width, true);
            setting = BitmapFactory.decodeResource(this.getResources(), R.drawable.settings);
            setting = Bitmap.createScaledBitmap(setting,
                    width, width, true);
            game = BitmapFactory.decodeResource(this.getResources(), R.drawable.play);
            game = Bitmap.createScaledBitmap(game,
                    width*5,width*2, true);
        }

        /**
         * Getter for the "About" button.
         * @return
         */
        private RectF getAbout(){
            return about;
        }
        /**
         * Getter for the "Settings" button.
         * @return
         */
        private RectF getSettings(){
            return settings;
        }
        /**
         * Getter for the "Play" button.
         * @return
         */
        private RectF getPlayGame(){
            return playGame;
        }
    }


}
