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

public class FreeStuffPin extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makepins);

        final Button create = (Button) findViewById(R.id.create);
        final EditText review = (EditText) findViewById(R.id.review);
        final Spinner local = (Spinner) findViewById(R.id.local);

        create.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final String comment = review.getText().toString();
                final String location = local.getSelectedItem().toString();
                JDBCInterface.addPin(location, comment, "Free Stuff", JDBCInterface.lastUsername);
                startActivity(new Intent(FreeStuffPin.this, MapsActivity.class));
            }

        });
    }
}
