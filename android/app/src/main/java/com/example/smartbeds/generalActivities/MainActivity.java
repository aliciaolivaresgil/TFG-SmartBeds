package com.example.smartbeds.generalActivities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.smartbeds.API.APIUtil;
import com.example.smartbeds.util.DialogUtil;
import com.example.smartbeds.R;
import com.example.smartbeds.session.Session;
import com.example.smartbeds.bedMonitoring.BedsActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * UI con la pantalla de login.
 * @author Alicia Olivares Gil
 */
public class MainActivity extends AppCompatActivity {

    private final Context context = this;
    private ProgressBar progressBar;

    /**
     * Construye la UI.
     * @param savedInstanceState savedInstanceState.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.main_progress);

    }

    /**
     * Borra los datos de sesión y oculta la barra de carga.
     */
    @Override
    protected void onResume(){
        super.onResume();

        progressBar.setVisibility(View.GONE);

        Session session = Session.getInstance();
        session.resetSession();
        Log.d("SESION", "sesión reseteada");
    }

    /**
     * Listener para el botón que inicia la sesión y lleva a la pantalla correspondiente al rol.
     * @param view Vista.
     */
    public void iniciarSesion(View view) {

        EditText user = (EditText) findViewById(R.id.input_nombre);
        EditText pass = (EditText) findViewById(R.id.input_contrasena);

        //Consultar autentificación a la API
        String urlParameters = "user="+user.getText().toString()+"&pass="+pass.getText().toString();
        JSONObject resultado = APIUtil.petitionAPI("/api/auth", urlParameters, context);
        int status = APIUtil.getStatusFromJSON(resultado);

        switch (status){
            case 200: //Si la autentificación es correcta

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