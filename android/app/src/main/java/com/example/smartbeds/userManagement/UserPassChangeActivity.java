package com.example.smartbeds.userManagement;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.smartbeds.API.APIUtil;
import com.example.smartbeds.util.DialogUtil;
import com.example.smartbeds.generalActivities.MainActivity;
import com.example.smartbeds.R;
import com.example.smartbeds.session.Session;

import org.json.JSONObject;

/**
 * UI para cambiar la contraseña de un usuario.
 * @author Alicia Olivares Gil
 */
public class UserPassChangeActivity extends AppCompatActivity {

    private Context context = this;
    private String username = null;
    private String connectedUser = null;

    /**
     * Construye la UI en función del rol del usuario.
     * @param savedInstanceState savedInstanceState.
     */
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

        //Si el usuario es admin no necesita conocer la contraseña antigua para cambiarla.
        if(!username.equals("admin") && connectedUser.equals("admin")){
            findViewById(R.id.user_pass_change_antigua_contrasena_text).setVisibility(View.GONE);
            findViewById(R.id.user_pass_change_input_antigua).setVisibility(View.GONE);
        }

        TextView toolbar = (TextView) findViewById(R.id.user_pass_change_title);
        String title = toolbar.getText().toString();
        toolbar.setText(title+username);
    }

    /**
     * Listener del botón que cambia la contraseña.
     * @param view Vista.
     */
    public void cambiarContrasena(View view){

        EditText oldPass = (EditText) findViewById(R.id.user_pass_change_input_antigua);
        EditText newPass = (EditText) findViewById(R.id.user_pass_change_input_nueva);
        EditText reNewPass = (EditText) findViewById(R.id.user_pass_change_input_repetir_nueva);

        if(!username.equals("admin") && connectedUser.equals("admin")){
            oldPass.setText("-");
        }

        //Comprobación de campos obligatorios y contraseña correcta.
        if(oldPass.getText().toString().equals("")||newPass.getText().toString().equals("")||reNewPass.getText().toString().equals("")){
            DialogUtil.showDialog(context, "Error" ,"Todos los campos son obligatorios.");
        }else if(!newPass.getText().toString().equals(reNewPass.getText().toString())) {
            DialogUtil.showDialog(context, "Error", "La nueva contraseña es incorrecta.");
        }else{
            Session session = Session.getInstance();
            String token = session.getToken();
            String urlParameters = null;

            //Petición de modificación de la contraseña a la API.
            //Si el usuario es admin no necesita conocer la contraseña anterior.
            if(!username.equals("admin") && connectedUser.equals("admin")) {
                urlParameters = "token=" + token + "&username=" + username + "&password=" + newPass.getText().toString() + "&password-re=" + reNewPass.getText().toString();
            //Si el usuario no es admin necesita conocer la contraseña anterior.
            }else{
                urlParameters = "token=" + token + "&username=" + username + "&password=" + newPass.getText().toString() + "&password-re=" + reNewPass.getText().toString() +"&password-old=" + oldPass.getText().toString();
            }
                JSONObject resultado = APIUtil.petitionAPI("/api/user/mod", urlParameters, context);
            int status = APIUtil.getStatusFromJSON(resultado);

            //Comprobación del estatus de la petición.
            if(status==200){
                DialogUtil.showDialog(context, "Contraseña modificada", "La contraseña del usuario "+username+" se ha modificado correctamente.");
                oldPass.setText("");
                newPass.setText("");
                reNewPass.setText("");
            }else if (status==403){
                DialogUtil.showDialog(context, "Error", "La antigua contraseña es incorrecta. ");
            }else{
                DialogUtil.showDialog(context, "Error", "No se ha podido modificar la contraseña.");
            }
        }
    }

    /**
     * Listener para el botón que finaliza el activity.
     * @param view view
     */
    public void back(View view){
        finish();
    }
}
