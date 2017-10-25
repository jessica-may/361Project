package com.example.gregg.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by jessicamay on 10/25/17.
 */

public class CreatePin extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        System.out.println("now reached here");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        final Button food = (Button) findViewById(R.id.food);
        final Button freeStuff = (Button) findViewById(R.id.freeStuff);

        food.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                System.out.println("and here");
                startActivity(new Intent(CreatePin.this, FoodPin.class));
            }
        });

        freeStuff.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(CreatePin.this, FreeStuffPin.class));
            }
        });

    }
}
