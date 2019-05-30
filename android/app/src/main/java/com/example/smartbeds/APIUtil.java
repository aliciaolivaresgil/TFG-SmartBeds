package com.example.smartbeds;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class APIUtil {

    public static JSONObject petitionAPI(String path, String urlParams, Context context){
        Log.d("peticion", urlParams);
        JSONObject resultado;

        if(ConnectivityUtil.checkInternetConnection(context)) {
            APICommunication communication = new APICommunication(path, urlParams, context);
            Thread thread = new Thread(communication);

            try {
                thread.start();
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int status = communication.getStatus();
            resultado = communication.getResult();
        }else{
            DialogUtil.showConnectionLostDialog(context);
            resultado = new JSONObject();
            try {
                resultado.put("status", 503);
                resultado.put("message", "Service Unavilable");
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return resultado;
    }

    public static int getStatusFromJSON(JSONObject json){
        int status=0;
        try {
            status = (int) json.get("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("STATUSSSSS", ""+status);
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
