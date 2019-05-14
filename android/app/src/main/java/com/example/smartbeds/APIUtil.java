package com.example.smartbeds;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class APIUtil {

    public static JSONObject petitionAPI(String path, String urlParams){
        Log.d("peticion", urlParams);

        APICommunication communication = new APICommunication(path, urlParams);
        Thread thread = new Thread(communication);

        try {
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int status = communication.getStatus();
        JSONObject resultado = communication.getResult();
        return resultado;
    }

    public static int getStatusFromJSON(JSONObject json){
        int status=0;
        try {
            status = (int) json.get("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return status;
    }

    public static List<String> JSONArrayToList(JSONArray jsonArray) {
        List<String> items = new ArrayList<String>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                items.add((String) jsonArray.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return items;
    }


}
