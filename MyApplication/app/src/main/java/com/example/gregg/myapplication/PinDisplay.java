package com.example.gregg.myapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by jessicamay on 11/28/17.
 */

public class PinDisplay extends AppCompatActivity{
    private ArrayAdapter<String> desc;
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
        final ArrayList<String> comments = MapsActivity.comments;
        final ListView list = (ListView) findViewById(R.id.comment_list);
        desc = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, comments);

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
        list.setAdapter(desc);


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
            int votes = 0;
            @Override
            public void onClick(View v) {

                try {
                    JDBCInterface.addVote(JDBCInterface.lastUsername, position, 1);
                } catch (Exception e) {
                    System.out.println("!!!"+e.getStackTrace());
                }
                try {
					votes=JDBCInterface.getVotes(position);
                } catch (Exception e) {
                    System.out.println("!!!"+e.getStackTrace());
                }
                vote.setText("VOTES: " + votes);
            }
        });

        downvote.setOnClickListener(new View.OnClickListener(){
            int votes = 0;
            @Override
            public void onClick(View v) {
                try {
                    JDBCInterface.addVote(JDBCInterface.lastUsername, position, -1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
					votes=JDBCInterface.getVotes(position);
                } catch (Exception e) {
                    System.out.println("!!!"+e.getStackTrace());
                }
                vote.setText("VOTES: " + votes);

            }
        });
    }
}
