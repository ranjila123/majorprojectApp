package com.example.major;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class ErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
    }
    public void back(){
        Intent intent = new Intent(ErrorActivity.this,ImageActivity.class);
        startActivity(intent);
    }
}