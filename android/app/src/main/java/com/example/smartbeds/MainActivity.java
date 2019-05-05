package com.example.smartbeds;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


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
        thread.start();

        try {
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

                Intent intent = new Intent(context, Admin.class);
                startActivity(intent);

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