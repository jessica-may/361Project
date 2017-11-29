package com.example.gregg.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

/**
 * Created by jessicamay on 11/28/17.
 */

public class PinDisplay extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_display);
    }

    Button upvote = (Button) findViewById(R.id.upvote);
}
