package com.clouddrums.clouddrums2;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

public class LaunchActivity extends AppCompatActivity {
    public int valid = 0;
    @Override
    protected void onStop (){
        super.onStop();
        valid = 1;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        //show start activity
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                ImageView img = (ImageView) findViewById(R.id.imageView);
                TranslateAnimation moveLefttoRight = new TranslateAnimation(0, 900, 0, 0);
                moveLefttoRight.setDuration(1000);
                moveLefttoRight.setFillAfter(true);
                img.startAnimation(moveLefttoRight);
            }
        }, 1000);


        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            public void run() {
                if (valid == 0) {
                    Intent startIntent = new Intent(LaunchActivity.this, MainActivity.class);
                    startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    LaunchActivity.this.startActivity(startIntent);
                }

            }
        }, 1750);


    }


}
