package com.example.learn.objects;

import android.os.Handler;
import android.os.Message;

import com.example.learn.objects.TickListener;

import java.util.ArrayList;


/**
 * Created by JHT.
 */

public class Timer extends Handler{

    private ArrayList<TickListener> tickListeners;
    private int do_nothing = 0;
    private int freeze = 1;
    private int unfreeze = 2;
    private int action = do_nothing;
    private String squareString = "reset";
    private TickListener removedListener;
    private static Timer singleton;

    /**
     * Kick-starts the timer. Also instantiates the ArrayList.
     */
    private Timer() {
        tickListeners = new ArrayList<>();
        sendMessageDelayed(obtainMessage(), 5);
    }

    /**
     * Singleton Method
     * @return
     */
    public static Timer getInstance() {
        if (singleton == null) {
            singleton = new Timer();
        }
        return singleton;
    }
    /**
     * Adds an object as a listener.
     * @param listener
     */
    public void addListener(TickListener listener){
        tickListeners.add(listener);
    }

    /**
     * Removes an object from the ticklisteners array.
     * @param listener
     */
    public void removeListener(TickListener  listener){
        tickListeners.remove(listener);
    }

    /**
     * Identifies the square to be frozen/unfrozen using its toString() value.
     * Is used in looking up the square to be frozen from the ticklisteners array.
     * @param s - toString() value of the object
     */
    public void setSquare(String s){
        squareString = s;
    }

    /**
     * Sets action to freeze.  This method is called in the view class' onTouchEvent method.
     */
    public void squareFreeze(){
        action = freeze;
    }

    /**
     * Sets action to unfreeze. This method is called in the view class' onTouchEvent method.
     * @param listener
     */
    public void squareUnfreeze(TickListener listener){
        action = unfreeze;
        removedListener = listener;
    }


    /**
     * Receives and sends a message to grant a continuous loop.
     * Looks up the square to be acted upon using its toString() value.
     * Acts on the square depending on the action value: freeze, unfreeze, or do_nothing
     * @param m - message
     */
    @Override
    public void handleMessage(Message m) {
        int square=-1;
        for (TickListener listener : tickListeners){
            listener.tick();
            if(listener.toString().equals(squareString)){
                square = tickListeners.indexOf(listener);
            }
        }
        if (action==freeze) {
            tickListeners.remove(tickListeners.get(square));
            action = do_nothing;
            squareString = "reset";
        }
        else if(action==unfreeze){
            tickListeners.add(removedListener);
            action = do_nothing;
            squareString = "reset";
        }
    }

    public void sendMessage(Boolean b){
        if(b){
            sendMessageDelayed(obtainMessage(), 5);
        }
    }


}
