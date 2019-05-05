package com.example.smartbeds;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Communication implements Runnable{

    private int status;
    private String urlParameters;
    private String path;
    private JSONObject result;

    public Communication(String path, String urlParameters){
        this.status=0;
        this.urlParameters=urlParameters;
        this.path=path;
    }

    @Override
    public void run() {
        try  {
            URL url = new URL("http://ubu.joselucross.com"+path);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Accept","application/json");
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
            Log.d("status", ""+status);

            StringBuilder content;

            try(BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
                String line;
                content = new StringBuilder();

                while((line = in.readLine()) != null){
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }

            Log.d("resultado", content.toString());


            connection.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }

    }

    public int getStatus(){
        return this.status;
    }

    public JSONObject getResult(){
        return this.result;
    }
}
