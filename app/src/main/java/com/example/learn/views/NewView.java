package com.example.learn.views;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learn.config.Alphabetical;
import com.example.learn.config.CountingGame;
import com.example.learn.config.GameSettings;
import com.example.learn.config.Multiply;
import com.example.learn.config.ReverseM;
import com.example.learn.config.SpellingGame;
import com.example.learn.objects.Event;
import com.example.learn.config.Game_Style;
import com.example.learn.objects.HighScore;
import com.example.learn.objects.NumberedSquares;
import com.example.learn.R;
import com.example.learn.objects.TickListener;
import com.example.learn.objects.Timer;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static com.example.learn.objects.NumberedSquares.findThePerfectFontSize;


/**
 * Created by JHT.
 */

public class NewView extends android.support.v7.widget.AppCompatImageView implements TickListener{
    /**
     * Field Declarations
     */
    private float w, h; //width and height of Canvas
    private boolean firstRun, timerEnabled, bonusGreen;
    private List<NumberedSquares> squares;
    private Timer t;
    private Toast pop;
    private Game_Style game;
    private List<String> labels;
    private Paint pT, pB;
    private int score, highScore, speedBonus, timer, level, rankingSize;
    private List<HighScore> highScoreList;
    private String l_score, l_bonus, l_highest, l_level;

    /**
     * Constructor. Creates "squares" ArrayList.
     * Selects the Game mode.
     * Also initializes the Timer.
     * @param c
     */
    public NewView(Context c) {
        super(c);
        rankingSize = 5;
        score = 0;
        level = 1;
        l_score = c.getString(R.string.d_score);
        l_level= c.getString(R.string.d_level);
        l_bonus= c.getString(R.string.d_bonus);
        l_highest= c.getString(R.string.d_highest);
        bonusGreen = true;
        pT = new Paint();
        pT.setColor(Color.WHITE);
        pT.setTextSize(60);
        pB = new Paint();
        pB.setColor(Color.GREEN);
        pB.setTextSize(60);
        pB.setTextAlign(Paint.Align.CENTER);
        setImageResource(R.drawable.screen);
        setScaleType(ScaleType.FIT_XY);
        firstRun = true;
        if (GameSettings.getMode(c).equals(c.getString(R.string.mode_spell))){
            game = new SpellingGame(c);
        }
        else if (GameSettings.getMode(c).equals(c.getString(R.string.mode_multiples))){
            game = new Multiply(c);
        }
        else if (GameSettings.getMode(c).equals(c.getString(R.string.mode_alpha))){
            game = new Alphabetical(c);
        }
        else if (GameSettings.getMode(c).equals(c.getString(R.string.mode_revMult))){
            game = new ReverseM(c);
        }
        else {
            game = new CountingGame(c);
        }
        highScoreList = new ArrayList<>();
        squares = new ArrayList<>();
        t = Timer.getInstance();
        t.addListener(this);
        readScore();
        Log.d("found", ""+highScoreList.size());
        highScore = highScoreList.get(0).getScore();
    }

    /**
     * Reads from a file and sets the game's high score.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void readScore(){
        try (FileInputStream fi = this.getContext().openFileInput(game.toString());
             Scanner s = new Scanner(fi)){
            StringBuilder json = new StringBuilder();
            while (s.hasNext()) {
                json.append(s.nextLine());
            }
            String j = json.toString();
            JSONArray jList = new JSONArray(j);

            for (int i=0; i<rankingSize; i++) {
                JSONObject player = jList.getJSONObject(i);
                HighScore yipee = new HighScore(player);
                highScoreList.add(yipee);
            }

        } catch (FileNotFoundException e) {
            populate();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(highScoreList.size()<5){
            populate();
        }
        Collections.sort(highScoreList);
    }

    /**
     *  Writes the score to a file if a user gets a new high score.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setHighScore(){
        JSONArray scoreBoard = new JSONArray();
        try (FileOutputStream fos = this.getContext().openFileOutput(game.toString(), Context.MODE_PRIVATE)){
        for (HighScore top: highScoreList) {
            JSONObject j = top.saveState();
            scoreBoard.put(j);
        }
            String jsonText = scoreBoard.toString();
            fos.write(jsonText.getBytes());
            Log.d("Found", jsonText);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException|JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Populates the "highScoreList" array.
     */
    public void populate(){
        for (int i=0; i<5; i++) {
            HighScore yipee = new HighScore(100+(100*i), "Player "+ (i+1));
            highScoreList.add(yipee);
            Log.d("found", ""+highScoreList.size());
        }
    }
    /**
     * Creates squares and stores them into the ArrayList when they do not intersect any of the previously created squares.
     */
    public void sqCreate() {
        squares.clear(); //clears the squares in the ArrayList
        NumberedSquares.resetCounter();
        speedBonus = 50;
        timer = 0;
        bonusGreen = true;
        pB.setColor(Color.GREEN);
        labels = game.getSquareLabels();
        boolean intersected = false; // number of squares that intersected
        int level = labels.size();
        Toast progress = Toast.makeText(this.getContext(), game.getNextLevelLabel(), Toast.LENGTH_SHORT);
        progress.show();
        for (int i = 0; i < level; i++) {
            String s = labels.get(i);
            NumberedSquares ns = new NumberedSquares(this, s);
            if(squares.size()==0){
                squares.add(ns);
                t.addListener(ns);
            }
            else {
                for (NumberedSquares index: squares){
                    if(index.intersects(ns)){
                        intersected = true;
                        break;
                    }
                }
                if (!intersected){
                    squares.add(ns); //add the created square to the ArrayList;
                    intersected= false; //reset
                    t.addListener(ns);
                }
                else {
                    i--; //decreases for loop index to keep the loop going when not enough squares are created
                    ns.decreaseCounter(); //decreases counter
                    intersected= false; // reset
                }
            }
        }
    }


    /**
     * Keeps the timer ticking.
     * @param b - boolean
     */
    public void tickTimer(Boolean b){
        timerEnabled = b;
        if(timerEnabled){
            t.sendMessage(true);
        }
    }

    /**
     * Method that calculates the bonus points.
     */
    public void bonusTick(){
        if(speedBonus>0){
            timer++;
        }
        else if(speedBonus==0 && bonusGreen){
            pB.setColor(Color.RED);
            bonusGreen= false;
        }
        if(speedBonus!=0 && timer%60==0) {
            speedBonus--;
        }
    }

    /**
     * Calls the collisions method and invalidates the view whenever the timer ticks.
     */
    @Override
    public void tick() {
        for (NumberedSquares ns : squares) {
            ns.collisions(squares);
            invalidate();
        }
        tickTimer(timerEnabled);
        bonusTick();
    }

    /**
     * Draws the squares from the ArrayList
     * @param c - Canvas
     */
    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        if (firstRun) {
            w = c.getWidth();
            h = c.getHeight();
            sqCreate();
            firstRun = false;
        }
        //c.drawColor(Color.BLACK);
        for (NumberedSquares ns : squares){
            ns.Draw(c);
        }
        c.drawText(l_score + score, w*.05f, h*.05f, pT);
        c.drawText(l_level + level, w*.75f, h*.05f, pT);
        c.drawText(l_bonus + speedBonus, w*.5f, h*.95f, pB);
        c.drawText(l_highest + highScore, w*.05f, h*.1f, pT);
    }

    /**
     * Calls square creation method every time user taps the screen.
     * Sets the event.
     * Calls methods for reading and writing JSON files.
     * @param m - MotionEvent
     * @return true
     */
    @Override
    public boolean onTouchEvent(MotionEvent m) {
        if (m.getAction() == MotionEvent.ACTION_DOWN) {
            float x = m.getX();
            float y = m.getY();
            for (NumberedSquares sq : squares) {
                if (sq.contains(x, y) && !sq.isFrozen()) {
                    t.setSquare(sq.toString());
                    Event touchEvent = game.getEvent(sq);
                    if (touchEvent == Event.TRY_AGAIN) {
                        pop = Toast.makeText(this.getContext(), game.getTryAgainLabel(), Toast.LENGTH_SHORT);
                        pop.show();
                        if((speedBonus-10)>0) {
                            speedBonus -= 10;
                        }
                        Log.d("event", ""+touchEvent);
                    } else if (touchEvent == Event.CONTINUE) {
                        Log.d("event", ""+touchEvent);
                        sq.freeze();
                        t.squareFreeze();
                        score+=(10+speedBonus);
                        invalidate();
                    } else if (touchEvent == Event.LEVEL_COMPLETE ) {
                        Log.d("event", ""+touchEvent);
                        score+=(10+speedBonus);
                        score+=50;
                        level++;
                        sqCreate();
                        invalidate();
                        break;
                    }
                    else if(touchEvent==Event.Game_Over){
                        Log.d("event", ""+touchEvent);
                        AlertDialog.Builder ab = new AlertDialog.Builder(getContext());
                        if(score>highScore){
                            ab.setMessage(R.string.high_score_msg);
                        }
                        else {
                            ab.setMessage(R.string.play_again_msg);
                        }
                        HighScore first = new HighScore(score, null );
                        highScoreList.add(first);
                        Collections.sort(highScoreList);
                        highScoreList.remove(highScoreList.size()-1);

                        ab.setTitle(R.string.game_over);
                        ab.setCancelable(false);
                        ab.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                setHighScore();
                                level = 1;
                                score = 0;
                                sqCreate();
                                invalidate();
                            }
                        });
                        ab.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                setHighScore();
                                ((Activity) getContext()).finish();
                            }
                        });
                        AlertDialog box = ab.create();
                        TableLayout board = new TableLayout(getContext());

                        TableRow title = new TableRow(getContext());
                        TextView name = new TextView(getContext());
                        TextView score = new TextView(getContext());
                        name.setText(R.string.player);
                        name.setTextSize(24);
                        name.setTextColor(Color.BLUE);
                        score.setText(R.string.score);
                        score.setTextSize(24);
                        score.setTextColor(Color.BLUE);
                        title.addView(name);
                        title.addView(score);
                        board.addView(title);
                        for (HighScore e: highScoreList){
                            TableRow row = e.getRow(getContext());
                            board.addView(row);
                        }
                        box.setView(board);
                        box.show();


                    }
                }
            }
        }

        return true;
    }
}