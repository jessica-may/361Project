package com.example.gregg.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by jessicamay on 11/15/17.
 */

public class AccountActivity extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        final Button logout = (Button) findViewById(R.id.logout);
        final EditText changePass = (EditText) findViewById(R.id.new_password);
        final Button delete = (Button) findViewById(R.id.delete_account);
        final Button change = (Button) findViewById(R.id.change_password);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JDBCInterface.lastUsername = null;
                startActivity(new Intent(AccountActivity.this, LoginActivity.class));
            }
        });

        delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                JDBCInterface.deleteUser(JDBCInterface.lastUsername);
                startActivity(new Intent(AccountActivity.this, LoginActivity.class));
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = changePass.getText().toString();
                if(!password.equals("")) {
                    JDBCInterface.changePassword(JDBCInterface.lastUsername, password);
                    startActivity(new Intent(AccountActivity.this, MapsActivity.class));
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
                    builder.setMessage("ERROR: Password must not be empty.")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                }
            }
        });
    }
}
