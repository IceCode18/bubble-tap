package com.example.learn.objects;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.view.View;

import com.example.learn.R;
import com.example.learn.config.GameSettings;

import java.util.List;

/**
 * Created by JHT.
 */

public class NumberedSquares implements TickListener {
    /**
     * Field Declarations
     */
    private RectF mySquare;
    private Paint squareStyle;
    private Paint sText;
    private float w, h; //width and height of Canvas
    private float center, sqWidth, imgWidth;
    private static int counter = 0;
    private int id;
    private String label;
    private PointF velocity;
    private boolean frozen, fxEnabled;
    private Bitmap image, image1,image2;
    private View v;
    private MediaPlayer fx;

    private enum Impact {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT,
        NONE
    }


    /**
     * Keeps track of the Number times "NumberedSquares" is instantiated. The if statement is a workaround to keep the numbers from exceeding five.
     * Also creates a square and instantiates the Velocity.
     */
    public NumberedSquares(View parent, String s) {
        counter++;
        id = counter;
        label = s;
        v = parent;
        w = parent.getWidth();
        h = parent.getHeight();
        frozen = false;
        fxEnabled = false;
        squareConfig();
        int moveSpeed = GameSettings.getMoveSpeed(parent.getContext());
        velocity = new PointF((float) (Math.random() * moveSpeed*2) - moveSpeed, (float) (Math.random() * moveSpeed*2) - moveSpeed);
        image1 = BitmapFactory.decodeResource(v.getResources(), R.drawable.energy1);
        image1 = Bitmap.createScaledBitmap(image1,
                (int)imgWidth, (int)imgWidth, true);
        image2 = BitmapFactory.decodeResource(v.getResources(), R.drawable.energy2);
        image2 = Bitmap.createScaledBitmap(image2,
                (int)imgWidth, (int)imgWidth, true);
        image = image1;
        if (GameSettings.getSoundEffectOption(v.getContext())) {
            fx = MediaPlayer.create(v.getContext(), R.raw.slip);
            fxEnabled = true;
        }
    }

    /**
     * Getter for id.
     * @return - id
     */
    public int getID(){
       return id;
    }

    /**
     * Decreases the counter. To be used by the NewView class every time a square that intersects the stored squares is created.
     * Keeps proper numbering of the squares.
     */
    public void decreaseCounter() {
        counter--;
    }

    /**
     * Prevents the ID numbers from growing too large.
     */
    public static void resetCounter() {
        counter = 0;
    }

    /**
     * Method that checks for intersection. Built to improve readability.
     * @param s - NumberedSquares class
     * @return
     */
    public boolean intersects(NumberedSquares s) {
        return RectF.intersects(this.mySquare, s.mySquare);
    }

    /**
     * Boolean method to check if the user tapped a square. Written for convenience.
     * @param x
     * @param y
     * @return
     */
    public boolean contains(float x, float y) {
        return mySquare.contains(x, y);
    }

    /**
     * Configures and creates the square
     */
    public void squareConfig() {
        float base = Math.min(w, h);
        sqWidth = base * 0.15f;
        imgWidth = sqWidth*1.2f;
        float fontSize = findThePerfectFontSize(h * 0.05f);
        center = (sqWidth / 2) + (fontSize / 2);
        float top = (float) Math.random() * (h - sqWidth);
        float left = (float) Math.random() * (w - sqWidth);
        float bottom = top + sqWidth;
        float right = left + sqWidth;

        squareStyle = new Paint();

        sText = new Paint();
        sText.setColor(Color.WHITE);
        sText.setTextSize(fontSize);
        sText.setTextAlign(Paint.Align.CENTER);

        mySquare = new RectF();
        mySquare.set(left, top, right, bottom);
    }

    /**
     * Method that offsets the coordinates of the squares.
     * The algorithm makes it possible for the squares to bounce off the edge of the screen.
     */
    public void move() {
        if (mySquare.left < 0 || mySquare.right > w) {
            mySquare.offset(-velocity.x, 0);
            velocity.x = -velocity.x;
        }
        if (mySquare.top < 0 || mySquare.bottom > h) {
            mySquare.offset(0, -velocity.y);
            velocity.y = -velocity.y;
        }
        mySquare.offset(velocity.x, velocity.y);
    }


    /**
     * Checks whether this square has collided with any others. If so, both
     * squares velocities are adjusted.
     *
     * @param others list of other squares to compare against this one.
     */
    public void collisions(List<NumberedSquares> others) {
        for (NumberedSquares other : others) {
            if (other.id > this.id) {
                if (this.intersects(other)) {
                    if(!this.isFrozen() && !other.isFrozen()) {
                        playFX();
                    }
                    Impact impacted = Impact.NONE;
                    float dtop = Math.abs(other.mySquare.bottom - this.mySquare.top);
                    float dbot = Math.abs(other.mySquare.top - this.mySquare.bottom);
                    float dleft = Math.abs(other.mySquare.right - this.mySquare.left);
                    float drt = Math.abs(other.mySquare.left - this.mySquare.right);
                    float min = Math.min(Math.min(dtop, dbot), Math.min(drt, dleft));
                    if (min == dtop) {
                        impacted = Impact.TOP;
                    }
                    if (min == dbot) {
                        impacted = Impact.BOTTOM;
                    }
                    if (min == dleft) {
                        impacted = Impact.LEFT;
                    }
                    if (min == drt) {
                        impacted = Impact.RIGHT;
                    }
                    exchangeMomentum(other, impacted);
                }
            }
        }
    }
    /**
     * Exchanges velocity of the two colliding squares.
     * @param other - the square the impacts the current square
     * @param impacted - side of the square that took the impact
     */
    private void exchangeMomentum(NumberedSquares other, Impact impacted) {
        float tmp;
//        PointF tmp;
        forceApart(other, impacted);
        if (this.isFrozen() || other.isFrozen()){
            if (this.isFrozen()){
                other.velocity.x *= -1;
                other.velocity.y *= -1;
            }
            else if (other.isFrozen()){
                this.velocity.x *= -1;
                this.velocity.y *= -1;
            }
        }
        else{
            if (impacted == Impact.TOP || impacted == Impact.BOTTOM) {
                tmp = this.velocity.y;
                this.velocity.y = other.velocity.y;
                other.velocity.y = tmp;
            } else {
                tmp = this.velocity.x;
                this.velocity.x = other.velocity.x;
                other.velocity.x = tmp;
            }
        }
    }
    /**
     * Forces the two squares apart when they collide.
     * @param other - the square that is making an impact
     * @param impacted - the side of the square that took the impact1
     */
    private void forceApart(NumberedSquares other, Impact impacted) {
        RectF myBounds = new RectF(this.mySquare);
        RectF otherBounds = new RectF(other.mySquare);
        NumberedSquares impactedSquare = this;
        NumberedSquares impactingSquare = other;
        NumberedSquares tmp = new NumberedSquares(v,"clone");
        tmp.squareConfig();
        if (this.isFrozen()) {
            impactedSquare = tmp;
        }
        else if (other.isFrozen()) {
            impactingSquare = tmp;
        }
        switch (impacted) {
            case LEFT:
                impactedSquare.setLeft(otherBounds.right + 1);
                impactingSquare.setRight(myBounds.left - 1);
                break;
            case RIGHT:
                impactedSquare.setRight(otherBounds.left - 1);
                impactingSquare.setLeft(myBounds.right + 1);
                break;
            case TOP:
                impactedSquare.setTop(otherBounds.bottom + 1);
                impactingSquare.setBottom(myBounds.top - 1);
                break;
            case BOTTOM:
                impactedSquare.setBottom(otherBounds.top - 1);
                impactingSquare.setTop(myBounds.bottom + 1);
        }
    }
    private void setBottom(float b) {
        float dy = b - mySquare.bottom;
        mySquare.offset(0, dy);
    }
    private void setRight(float r) {
        float dx = r - mySquare.right;
        mySquare.offset(dx, 0);
    }
    private void setLeft(float lf) {
        mySquare.offsetTo(lf, mySquare.top);
    }
    private void setTop(float t) {
        mySquare.offsetTo(mySquare.left, t);
    }


    /**
     * Sets the status of the square to frozen. Changes the square color of the frozen square.
     */
    public void freeze() {
        squareStyle.setColor(Color.MAGENTA);
        image = image2;
        frozen = true;
    }
    /**
     * Removes the frozen status of the square.
     */
    public void unfreeze() {
        squareStyle.setColor(Color.BLUE);
        image = image1;
        frozen = false;
    }
    /**
     * Gets the status of the square.
     * @return
     */
    public boolean isFrozen() {
        return frozen;
    }

    /**
     * Plays the in-game sound effect.
     */
    public void playFX(){
        if (fxEnabled){
            fx.start();
        }
    }

    public String getLabel(){
        return label;
    }
    /**
     * Inherited from interface. Calls the move method. Allows the class to do polymorphism to be considered as a Ticklistener.
     */
    @Override
    public void tick() {
        move();

    }

    /**
     * Draws the Squares.
     *
     * @param c
     */
    public void Draw(Canvas c) {
        float indent = (imgWidth-sqWidth)/2;
        c.drawBitmap(image, mySquare.left-indent, mySquare.top-indent, squareStyle);
        //c.drawRect(mySquare, squareStyle);
        c.drawText(label, mySquare.left + (sqWidth / 2), mySquare.top + center, sText);
    }


    /**
     *
     * @param lowerThreshold how many pixels high the text should be
     * @return the font size that corresponds to the requested pixel height
     */
    public static float findThePerfectFontSize(float lowerThreshold) {
        float fontSize = 1;
        Paint p = new Paint();
        p.setTextSize(fontSize);
        while (true) {
            float asc = -p.getFontMetrics().ascent;
            if (asc > lowerThreshold) {
                break;
            }
            fontSize++;
            p.setTextSize(fontSize);
        }
        return fontSize;
    }
}
