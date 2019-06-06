package com.example.smartbeds;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
    private String connectedUser = null;

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

        connectedUser = session.getUsername();

        if(!username.equals("admin") && connectedUser.equals("admin")){
            findViewById(R.id.user_pass_change_antigua_contrasena_text).setVisibility(View.GONE);
            findViewById(R.id.user_pass_change_input_antigua).setVisibility(View.GONE);
        }

        TextView toolbar = (TextView) findViewById(R.id.user_pass_change_title);
        String title = toolbar.getText().toString();
        toolbar.setText(title+username);
    }

    protected void cambiarContrasena(View view){

        EditText oldPass = (EditText) findViewById(R.id.user_pass_change_input_antigua);
        EditText newPass = (EditText) findViewById(R.id.user_pass_change_input_nueva);
        EditText reNewPass = (EditText) findViewById(R.id.user_pass_change_input_repetir_nueva);

        if(!username.equals("admin") && connectedUser.equals("admin")){
            oldPass.setText("-");
        }

        if(oldPass.getText().toString().equals("")||newPass.getText().toString().equals("")||reNewPass.getText().toString().equals("")){
            DialogUtil.showDialog(context, "Error" ,"Todos los campos son obligatorios.");
        }else if(!newPass.getText().toString().equals(reNewPass.getText().toString())) {
            DialogUtil.showDialog(context, "Error", "La nueva contraseña es incorrecta.");
        }else if(!checkOldPass(oldPass.getText().toString())){
            DialogUtil.showDialog(context, "Error", "La antigua contraseña es incorrecta. ");

        }else{
            Session session = Session.getInstance();
            String token = session.getToken();
            String urlParameters = "token="+token+"&username="+username+"&password="+newPass.getText().toString()+"&password-re="+reNewPass.getText().toString();
            JSONObject resultado = APIUtil.petitionAPI("/api/user/mod", urlParameters, context);
            int status = APIUtil.getStatusFromJSON(resultado);

            if(status==200){
                DialogUtil.showDialog(context, "Contraseña modificada", "La contraseña del usuario "+username+" se ha modificado correctamente.");
                oldPass.setText("");
                newPass.setText("");
                reNewPass.setText("");
            }else{
                DialogUtil.showDialog(context, "Error", "No se ha podido modificar la contraseña.");
            }
        }
    }

    private boolean checkOldPass(String pass){
        Session session = Session.getInstance();
        String token = session.getToken();
        if(!username.equals("admin") && connectedUser.equals("admin")){
            return true;
        }else {
            String urlParameters = "user=" + username + "&pass=" + pass;
            JSONObject resultado = APIUtil.petitionAPI("/api/auth", urlParameters, context);
            int status = APIUtil.getStatusFromJSON(resultado);

            if (status == 200) {
                return true;
            } else {
                return false;
            }
        }
    }

    protected void back(View view){
        finish();
    }
}
