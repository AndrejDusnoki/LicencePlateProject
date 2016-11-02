package com.example.andul.licenceplatecapture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends Activity {
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        context=this;

        Runnable r = new Runnable() {
            @Override
            public void run(){
                Intent intent=new Intent(context,MainActivity.class);
                context.startActivity(intent);
            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 500);
    }
}
