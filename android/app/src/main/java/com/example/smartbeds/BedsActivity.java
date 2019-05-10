package com.example.smartbeds;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BedsActivity extends AppCompatActivity {

    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beds);

        Session session = Session.getInstance();
        if(session.getToken()==null) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }

        String urlParameters = "token="+session.getToken();
        Communication communication = new Communication("/api/beds", urlParameters);
        Thread thread = new Thread(communication);

        try {
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int status = communication.getStatus();
        Log.d("STATUS", ""+status);

        JSONObject resultado = communication.getResult();
        JSONArray bedNames=null;
        try {
            bedNames = (JSONArray) resultado.get("beds");

            Log.d("BEDS", bedNames.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
