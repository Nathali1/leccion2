package com.example.nathali.leccion2;


import android.os.Handler;
import android.os.HandlerThread;


/**
 * Created by nathali on 25/08/17.
 */



public class AccelerometerThread  extends HandlerThread {
    private Handler handler;

    public AccelerometerThread(String name) {
        super(name);
    }

    public void postTask(Runnable task){
        handler.post(task);
    }

    public void prepareHandler(){
        handler = new Handler(getLooper());
    }
}
