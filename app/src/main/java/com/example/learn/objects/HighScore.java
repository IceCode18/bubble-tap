package com.example.learn.objects;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.learn.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JHT.
 */

public class HighScore implements Comparable<HighScore> {

    private String name;
    private int score;
    private static final String JSON_NAME = "name";
    private static final String JSON_SCORE = "score";

    /**
     * Constructor for populating the scoreboard with fixed values.
     * @param point
     * @param n
     */
    public HighScore(int point, String n){
        score = point;
        name = n;
    }

    /**
     * Constructor for reading a JSONObject
     * @param j - JSONObject
     */
    public HighScore(JSONObject j){
        try {
            name = j.getString(JSON_NAME);
            score = Integer.parseInt(j.getString(JSON_SCORE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter for player score.
     * @return - score
     */
    public int getScore(){
        return score;
    }

    /**
     * Saves player information as a JSONObject
     * @return
     * @throws JSONException
     */
    public JSONObject saveState() throws JSONException {
        JSONObject j = new JSONObject();
        j.put(JSON_NAME, name);
        j.put(JSON_SCORE, ""+score);
        return j;
    }

    /**
     * Sets the row input and serves as a getter for NewView class.
     * @param c - Context
     * @return - TableRow
     */
    public TableRow getRow(final Context c) {
        TableRow r = new TableRow(c);
        TextView scoreText = new TextView(c);
        scoreText.setText("" + score);
        scoreText.setTextSize(20);
        final TextView nameField;
        if (name != null) {
            nameField = new TextView(c);
            nameField.setText(name);
        } else {
            nameField = new EditText(c);
            nameField.setHint(R.string.edit_hint);
            nameField.setMaxLines(1);
            nameField.setInputType(InputType.TYPE_CLASS_TEXT);
            nameField.setOnKeyListener(new View.OnKeyListener() {

                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event)
                {
                    if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        nameField.setFocusable(false);
                        nameField.setFocusableInTouchMode(false);
                        nameField.setClickable(false);
                        InputMethodManager imm = (InputMethodManager)c.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        return false;
                    }

                    return false;
                }
            });
            nameField.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

             }

                /**
                 * Method that is activated when user is typing.
                 * @param s
                 * @param start
                 * @param before
                 * @param count
                 */
             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {
                name = s.toString() + "   ";
             }

             @Override
             public void afterTextChanged(Editable s) {
             }
         });
        }
        if (name==null || name.equals("")){
            name = c.getString(R.string.unknown);
        }
        nameField.setTextSize(20);
        r.addView(nameField);
        r.addView(scoreText);
        return r;
    }

    /**
     * Logic for comparing high scores. Reverse sorting.
     * @param other
     * @return
     */
    @Override
    public int compareTo(HighScore other) {
        int score1 = this.score;
        int score2 = other.score;
        return score2-score1;
    }

    
    @Override
    public boolean equals(Object o){
        if (o instanceof HighScore) {
            HighScore other = (HighScore)o;
            if (this.score==other.score){
                return true;
            }
            else {
                return false;
            }
        } else {
            return false;
        }
    }
}
