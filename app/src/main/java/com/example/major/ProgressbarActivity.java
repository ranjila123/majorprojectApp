package com.example.major;


import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class ProgressbarActivity extends AppCompatActivity {

    int progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progressbar);
        ProgressBar bar = findViewById(R.id.bar);
        TextView text = findViewById(R.id.text);

        Activity activity = ProgressbarActivity.this;

        bar.setProgress(progress);
        bar.setMax(100);


        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                progress = progress + 10;
                bar.setProgress(progress);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        text.setText(String.valueOf(progress));
                    }
                });

                if(bar.getProgress()>=100){
                    timer.cancel();
                    Intent intent = new Intent(ProgressbarActivity.this,DownloadActivity.class);
                    activity.startActivity(intent);
                    finish();
                }
            }
        },1000,50);

    }
}