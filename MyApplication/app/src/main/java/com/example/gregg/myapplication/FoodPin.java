package com.example.gregg.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by jessicamay on 10/25/17.
 */

public class FoodPin extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makepins);

        final Button create = (Button) findViewById(R.id.create);
        final EditText review = (EditText) findViewById(R.id.review);
        final Spinner local = (Spinner) findViewById(R.id.local);

        final String comment = review.getText().toString();
        final String location = local.toString();

        create.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                JDBCInterface.addPin(location, comment, "food", JDBCInterface.lastUsername);
                startActivity(new Intent(FoodPin.this, MapsActivity.class));
            }

        });
    }
}
