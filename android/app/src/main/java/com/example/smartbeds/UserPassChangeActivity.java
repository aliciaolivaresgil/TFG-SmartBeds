package com.example.smartbeds;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

public class UserPassChangeActivity extends AppCompatActivity {

    private Context context = this;
    private String username = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pass_change);

        Session session = Session.getInstance();
        if(session.getToken()==null) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }

        Bundle b = getIntent().getExtras();
        username = b.getString("username");

        TextView toolbar = (TextView) findViewById(R.id.user_pass_change_title);
        String title = toolbar.getText().toString();
        toolbar.setText(title+username);
    }

    protected void cambiarContrasena(View view){

        EditText oldPass = (EditText) findViewById(R.id.user_pass_change_input_antigua);
        EditText newPass = (EditText) findViewById(R.id.user_pass_change_input_nueva);
        EditText reNewPass = (EditText) findViewById(R.id.user_pass_change_input_repetir_nueva);
        TextView error = (TextView) findViewById(R.id.user_pass_change_mensaje_error);
        TextView mssg = (TextView) findViewById(R.id.user_pass_change_mensaje);

        if(oldPass.getText().toString().equals("")||newPass.getText().toString().equals("")||reNewPass.getText().toString().equals("")){
            error.setText("Todos los campos son obligatorios.");
            error.setVisibility(View.VISIBLE);
        }else if(!newPass.getText().toString().equals(reNewPass.getText().toString())) {
            error.setText("La nueva contraseña es incorrecta.");
            error.setVisibility(View.VISIBLE);
        }else if(!checkOldPass(oldPass.getText().toString())){
            error.setText("La antigua contraseña es incorrecta.");
            error.setVisibility(View.VISIBLE);
        }else{
            error.setVisibility(View.GONE);

            Session session = Session.getInstance();
            String token = session.getToken();
            String urlParameters = "token="+token+"&username="+username+"&password="+newPass.getText().toString()+"&password-re="+reNewPass.getText().toString();
            JSONObject resultado = APIUtil.petitionAPI("/api/user/mod", urlParameters);
            int status = APIUtil.getStatusFromJSON(resultado);

            if(status==200){
                mssg.setText("La contraseña del usuario "+username+" se ha modificado correctamente.");
                mssg.setVisibility(View.VISIBLE);
                oldPass.setText("");
                newPass.setText("");
                reNewPass.setText("");
            }else{
                error.setText("No se ha podido modificar la contraseña.");
                error.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean checkOldPass(String pass){
        Session session = Session.getInstance();
        String token = session.getToken();
        String urlParameters = "user="+username+"&pass="+pass;
        JSONObject resultado = APIUtil.petitionAPI("/api/auth", urlParameters);
        int status = APIUtil.getStatusFromJSON(resultado);

        if(status==200){
            return true;
        }else{
            return false;
        }

    }
}
