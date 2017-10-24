package com.example.gregg.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jessicamay on 10/22/17.
 */

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        System.out.println("is it here?");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText email = (EditText) findViewById(R.id.email);
        final EditText password = (EditText) findViewById(R.id.password);
        final Button register = (Button) findViewById(R.id.register);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString();
                String userPw = password.getText().toString();
                try{
                    JDBCInterface.addUser(userEmail, userPw);
                    System.out.println("user added");
                }catch(Exception e){
                    e.printStackTrace();
                }

                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                /*Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            boolean pass = jsonObject.getBoolean("Pass");
                            JDBCInterface.addUser(userEmail,userPw);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };

                RegisterRequest registerRequest = new RegisterRequest(userEmail, userPw, responseListener);
                RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
                requestQueue.add(registerRequest);*/
            }
        });
    }
}
