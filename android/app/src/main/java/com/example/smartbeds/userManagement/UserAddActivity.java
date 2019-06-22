package com.example.smartbeds.userManagement;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.smartbeds.API.APIUtil;
import com.example.smartbeds.util.DialogUtil;
import com.example.smartbeds.generalActivities.MainActivity;
import com.example.smartbeds.R;
import com.example.smartbeds.session.Session;

import org.json.JSONObject;

/**
 * UI para crear un nuevo usuario.
 * @author Alicia Olivares Gil
 */
public class UserAddActivity extends AppCompatActivity {

    private Context context = this;

    /**
     * Construye la UI.
     * @param savedInstanceState savedInstanceState.
     */
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

    /**
     * Listener para el botón que crea un nuevo usuario.
     * @param view Vista.
     */
    public void anadirUsuario(View view){

        EditText user = (EditText) findViewById(R.id.add_user_input_nombre);
        EditText pass = (EditText) findViewById(R.id.add_user_input_contrasena);
        EditText repass = (EditText) findViewById(R.id.add_user_input_repita_contrasena);

        //Comprobaciones de campos obligatorios y contraseñas correctas
        if(user.getText().toString().equals("")||pass.getText().toString().equals("")||repass.getText().toString().equals("")){
            DialogUtil.showDialog(context, "Error", "Todos los campos son obligatorios.");
        }else if(!pass.getText().toString().equals(repass.getText().toString())){
            DialogUtil.showDialog(context, "Error", "La contraseña es incorrecta.");
        }else{
            Session session = Session.getInstance();
            String token = session.getToken();

            //Si las comprobaciones son correctas se hace la petición de escritura a la API.
            String urlParameters = "token="+token+"&username="+user.getText().toString()+"&password="+pass.getText().toString()+"&password-re="+repass.getText().toString();
            JSONObject resultado = APIUtil.petitionAPI("/api/user/add", urlParameters, context);
            int status = APIUtil.getStatusFromJSON(resultado);

            if(status==200){
                DialogUtil.showDialog(context, "Usuario añadido", "Se ha añadido el usuario "+user.getText().toString()+" correctamente.");

                user.setText("");
                pass.setText("");
                repass.setText("");
            }else{
                Log.d("ERROR", resultado.toString());
                DialogUtil.showDialog(context, "Error", "No se ha podido añadir el usuario.");
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
