package com.example.gregg.myapplication;


import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //names of all the items on the login screen
        final EditText email = (EditText) findViewById(R.id.email);
        final EditText password = (EditText) findViewById(R.id.password);
        final Button login = (Button) findViewById(R.id.login);
        final Button register = (Button) findViewById(R.id.register);



        //if the register link is clicked, redirect to the register class
        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
               startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }});


        //if the login in button is clicked
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userEmail = email.getText().toString();
                final String userPassword = password.getText().toString();

                        try {
                            System.out.println("!!!before");
                            String goodPas = JDBCInterface.getPassword(userEmail);
                            System.out.println("!!!after");
                            System.out.println("goodPas="+goodPas);
                            if (userPassword.equals(goodPas)) {
                                startActivity(new Intent(LoginActivity.this, MapsActivity.class));
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("Login has failed.")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
            }
        });
    }

}

