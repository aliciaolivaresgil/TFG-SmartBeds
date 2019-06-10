package com.example.smartbeds;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private final Context context = this;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.main_progress);

    }

    @Override
    protected void onResume(){
        super.onResume();

        progressBar.setVisibility(View.GONE);

        Session session = Session.getInstance();
        session.resetSession();
        Log.d("SESION", "sesión reseteada");
    }

    public void iniciarSesion(View view) {

        EditText user = (EditText) findViewById(R.id.input_nombre);
        EditText pass = (EditText) findViewById(R.id.input_contrasena);

        String urlParameters = "user="+user.getText().toString()+"&pass="+pass.getText().toString();
        JSONObject resultado = APIUtil.petitionAPI("/api/auth", urlParameters, context);
        int status = APIUtil.getStatusFromJSON(resultado);

        switch (status){
            case 200:

                progressBar.setVisibility(View.VISIBLE);

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
                DialogUtil.showDialog(context, "Error interno del servidor", "Inténtelo más tarde.");
                break;
            case 502:
                DialogUtil.showDialog(context, "Error de conexión con el servidor", "Inténtelo más tarde.");
                break;
            case 401:
                DialogUtil.showDialog(context, "Error de autentificación del usuario", "Nombre de usuaro o contraseña incorrectos.");
                break;
        }
    }

}