package com.example.smartbeds.API;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.smartbeds.util.ConnectivityUtil;
import com.example.smartbeds.util.DialogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Clase de utilidad para la comunicación con la API.
 * @author Alicia Olivares Gil
 */
public class APIUtil {

    /**
     * Lanza el hilo que realiza la petición POST a la API.
     * @param path Ruta del servicio de la API.
     * @param urlParams Parámetros POST en forma de string query.
     * @param context Contexto del activity actual.
     * @return JSONObject con el resultado de la petición POST.
     */
    public static JSONObject petitionAPI(String path, String urlParams, Context context){
        Log.d("peticion", urlParams);
        JSONObject resultado;

        if(ConnectivityUtil.checkInternetConnection(context)) { //comprobar si existe conexión a internet.

            //Lanzar hilo
            APICommunication communication = new APICommunication(path, urlParams, context);
            Thread thread = new Thread(communication);

            try {
                thread.start();
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //Comprobar status y gestionar errores de comunicación
            int status = communication.getStatus();
            Activity activity = (Activity) context;
            if(status==401 && !activity.getLocalClassName().equals("MainActivity")){
                DialogUtil.showUnactiveSessionDialog(context);
            }else if(status==500 && !activity.getLocalClassName().equals("MainActivity")){
                DialogUtil.showInternalServerErrorDialog(context);
            }

            //Obtener resultado
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

    /**
     * Obtiene el status del JSONObject con el resultado.
     * @param json JSONObject con el resultado de la petición POST.
     * @return status
     */
    public static int getStatusFromJSON(JSONObject json){
        int status=0;
        try {
            status = (int) json.get("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return status;
    }

    /**
     * Convierte un JSONArray a un List<String>.
     * @param jsonArray JSONArray devuelto en el resultado de la petición.
     * @return Lista de strings
     */
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
