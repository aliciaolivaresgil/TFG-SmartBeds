package com.example.smartbeds;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class UserAddActivity extends AppCompatActivity {

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add);

        Session session = Session.getInstance();
        if(session.getToken()==null) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }
    }

    protected void anadirUsuario(View view){

        EditText user = (EditText) findViewById(R.id.add_user_input_nombre);
        EditText pass = (EditText) findViewById(R.id.add_user_input_contrasena);
        EditText repass = (EditText) findViewById(R.id.add_user_input_repita_contrasena);
        TextView error = (TextView) findViewById(R.id.user_add_mensaje_error);
        TextView mssg = (TextView) findViewById(R.id.user_add_mensaje);

        if(user.getText().toString().equals("")||pass.getText().toString().equals("")||repass.getText().toString().equals("")){
            error.setText("Todos los campos son obligatorios.");
            error.setVisibility(View.VISIBLE);
        }else if(!pass.getText().toString().equals(repass.getText().toString())){
            error.setText("La contraseña es incorrecta.");
            error.setVisibility(View.VISIBLE);
        }else{
            error.setVisibility(View.GONE);

            Session session = Session.getInstance();
            String token = session.getToken();
            String urlParameters = "token="+token+"&username="+user.getText().toString()+"&password="+pass.getText().toString()+"&password-re="+repass.getText().toString();
            Log.d("peticion", urlParameters);

            Communication communication = new Communication("/api/user/add", urlParameters);
            Thread thread = new Thread(communication);

            try {
                thread.start();
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int status = communication.getStatus();
            if(status==200){
                mssg.setText("Se ha añadido el usuario "+user.getText().toString()+" correctamente.");
                mssg.setVisibility(View.VISIBLE);
                user.setText("");
                pass.setText("");
                repass.setText("");
            }else{
                error.setText("No se ha podido crear el usuario.");
                error.setVisibility(View.VISIBLE);
            }

        }
    }
}
