package com.example.gregg.myapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by jessicamay on 11/28/17.
 */

public class PinDisplay extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_display);

        final Button upvote = (Button) findViewById(R.id.upvote);
        final EditText name = (EditText) findViewById(R.id.title);
        final Button downvote = (Button) findViewById(R.id.downvote);
        final Button report = (Button) findViewById(R.id.report);
        final int id = MapsActivity.pinID;
        final String position = MapsActivity.position;
        final EditText vote = (EditText) findViewById(R.id.vote);
        int temp_vote = 0;
        name.setText(MapsActivity.type + " at " + position);
        try {
            for(Pin pin : JDBCInterface.getPinList()){
                if(pin.pinID == id){
                    temp_vote = pin.votes;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        vote.setText("VOTES: " + temp_vote);

        

        report.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PinDisplay.this);
                builder.setMessage("Would you like to report this event?")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Report", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                JDBCInterface.addReport(id, JDBCInterface.lastUsername, "");   //FIX
                            }
                        })
                        .create()
                        .show();

            }
        });

        upvote.setOnClickListener(new View.OnClickListener(){
            int temp_vote = 0;
            @Override
            public void onClick(View v) {
                try {
                    for(Pin pin : JDBCInterface.getPinList()){
                        if(pin.pinID == id){
                            temp_vote = pin.votes;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                vote.setText("VOTES: " + temp_vote);
                try {
                    JDBCInterface.addVote(JDBCInterface.lastUsername, id, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        downvote.setOnClickListener(new View.OnClickListener(){
            int temp_vote = 0;
            @Override
            public void onClick(View v) {
                try {
                    for(Pin pin : JDBCInterface.getPinList()){
                        if(pin.pinID == id){
                            temp_vote = pin.votes;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                vote.setText("VOTES: " + temp_vote);
                try {
                    JDBCInterface.addVote(JDBCInterface.lastUsername, id, -1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
