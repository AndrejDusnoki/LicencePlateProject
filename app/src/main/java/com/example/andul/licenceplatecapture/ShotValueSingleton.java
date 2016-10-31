package com.example.andul.licenceplatecapture;

/**
 * Created by andul on 10/31/2016.
 */
public class ShotValueSingleton {
    //Contains value of ShotLimiter
    private int ShotLimiter;
    private static ShotValueSingleton ourInstance = new ShotValueSingleton();

    public static ShotValueSingleton getInstance() {
        return ourInstance;
    }

    private ShotValueSingleton() {

    }


    public int getShotLimiter() {
        return ShotLimiter;
    }

    public void setShotLimiter(int shotLimiter) {
        ShotLimiter = shotLimiter;
    }
}
