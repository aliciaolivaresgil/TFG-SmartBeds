package com.example.smartbeds;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class APICommunication implements Runnable {

    private int status;
    private String urlParameters;
    private String path;
    private JSONObject result;
    private Context context;

    public APICommunication(String path, String urlParameters, Context context) {
        this.status = 0;
        this.urlParameters = urlParameters;
        this.path = path;
        this.result = null;
        this.context = context;
    }

    @Override
    public void run() {

        try {
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

            this.status = connection.getResponseCode();
            String msg = connection.getResponseMessage();
            Log.d("status", msg);
            Log.d("status", "" + status);

            StringBuilder content = new StringBuilder();

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

    public int getStatus() {

        return this.status;
    }

    public JSONObject getResult() {

        return this.result;
    }
}
