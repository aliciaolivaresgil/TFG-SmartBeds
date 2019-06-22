package com.example.smartbeds.API;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * Hilo que realiza la petición POST a la API.
 * @author Alicia Olivares Gil
 */
public class APICommunication implements Runnable {

    private int status;
    private String urlParameters;
    private String path;
    private JSONObject result;
    private Context context;

    /**
     * Constructor del hilo.
     * @param path Ruta del servicio de la API.
     * @param urlParameters Parámetros POST en forma de string query.
     * @param context Contexto del activity actual.
     */
    public APICommunication(String path, String urlParameters, Context context) {
        this.status = 0;
        this.urlParameters = urlParameters;
        this.path = path;
        this.result = null;
        this.context = context;
    }

    /**
     * Método run del hilo.
     * Realiza la petición y almacena el resultado.
     */
    @Override
    public void run() {

        try {

            //Realizar la petición POST
            URL url = new URL("https://ubu.joselucross.com" + path);

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            connection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                    return hv.verify("jkanetwork.com", session);
                }
            });

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("User-Agent", "Java client");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(postData);
            }

            //Obtener respuesta
            this.status = connection.getResponseCode();
            String msg = connection.getResponseMessage();
            Log.d("status", msg);
            Log.d("status", "" + status);

            StringBuilder content = new StringBuilder();

            //Comprobar si la respuesta es exitosa
            if (status == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        content.append(line);
                        content.append(System.lineSeparator());
                    }
                }
            } else {
                content.append("{\"status\":" + status + ",\"message\":\"" + msg + "\"}");
            }

            Log.d("resultado", content.toString());
            result = new JSONObject(content.toString());
            connection.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * Devuelve el status de la petición POST.
     * @return status
     */
    public int getStatus() {
        return this.status;
    }

    /**
     * Devuelve el resultado de la petición POST en forma de JSONObject.
     * @return result
     */
    public JSONObject getResult() {
        return this.result;
    }
}
