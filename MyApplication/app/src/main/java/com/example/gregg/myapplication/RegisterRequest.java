package com.example.gregg.myapplication;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

/**
 * Created by jessicamay on 10/22/17.
 */

public class RegisterRequest extends StringRequest {

    private Map<String, String> list;

    public RegisterRequest(String email, String password, Response.Listener<String> listener){
        super(Method.POST, null, listener, null);

        //list = new HashMap<>();
        //list.put("email", email);
        //list.put("password", password);
		//try{
		//	JDBCInterface.addUser(email,password);
		//}catch(Exception e){
		//	e.printStackTrace();
		//}
    }


    public Map<String, String> getList() {
        return list;
    }
}
