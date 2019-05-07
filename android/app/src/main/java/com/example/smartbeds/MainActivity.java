package com.example.smartbeds;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void iniciarSesion(View view) {

        EditText user = (EditText) findViewById(R.id.input_nombre);
        EditText pass = (EditText) findViewById(R.id.input_contrasena);

        Log.d("user", user.getText().toString());
        Log.d("pass", pass.getText().toString());

        String urlParameters = "user="+user.getText().toString()+"&pass="+pass.getText().toString();
        Log.d("peticion", urlParameters);

        Communication communication = new Communication("/api/auth", urlParameters);
        Thread thread = new Thread(communication);

        try {
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int status = communication.getStatus();
        Log.d("REAL STATUS", ""+status);

        TextView mensajeError = (TextView) findViewById(R.id.mensaje_error_login);;

        switch (status){
            case 200:
                mensajeError.setVisibility(View.GONE);

                JSONObject resultado = communication.getResult();
                String token = null;
                String username = null;
                String role = null;
                try {
                    token = (String) resultado.get("token");
                    username = (String) resultado.get("username");
                    role = (String) resultado.get("role");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("TOKEN", token);

                Session session = Session.getInstance();
                session.setUsername(username);
                session.setToken(token);
                session.setRole(role);

                Intent intent;

                if(role.equals("admin")){
                    intent = new Intent(context, AdminActivity.class);
                    startActivity(intent);
                }else{
                    intent = new Intent(context, BedsActivity.class);
                    startActivity(intent);
                }

                user.setText("");
                pass.setText("");
                break;
            case 500:
                mensajeError.setText("Error interno del servidor. Inténtelo más tarde.");
                mensajeError.setVisibility(View.VISIBLE);
                break;
            case 401:
                mensajeError.setText("Error en la identificación del usuario. Usuario o contraseña incorrectos.");
                mensajeError.setVisibility(View.VISIBLE);
                break;
        }


    }
}