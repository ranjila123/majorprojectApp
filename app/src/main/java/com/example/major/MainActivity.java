package com.example.major;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent i  = new Intent(MainActivity.this,HomeActivity.class);


        //so this is working fine after 5 sec it redirect to home screen
        //now make UI for home screen
        Thread thread =new Thread()
        {
            @Override
            public void run() {
                try
                {
                    sleep(5000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                finally {
                    startActivity(i);
                    finish();
                }
            }
        };thread.start();

    }
}
