package com.example.smartbeds.bedManagement;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smartbeds.API.APIUtil;
import com.example.smartbeds.generalActivities.MainActivity;
import com.example.smartbeds.R;
import com.example.smartbeds.session.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * UI de aignación de usuarios a una cama.
 */
public class BedAsignUsersActivity extends AppCompatActivity {

    private Context context = this;
    private String bedName;
    private List<String> permUsers = new ArrayList<>();
    private List<CheckBox> checkBoxList = new ArrayList<>();
    String token=null;

    /**
     * Construye la UI, muestra los usuarios y gestiona los eventos de la checkbox list.
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bed_asign_users);

        Session session = Session.getInstance();
        token = session.getToken();
        if(session.getToken()==null) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }

        //Obtener nombre de la cama
        Bundle b = getIntent().getExtras();
        bedName = b.getString("bedName");

        TextView title = findViewById(R.id.bed_asign_users_title);
        title.setText(title.getText()+bedName);

        //Consultar usuarios que tienen permisos sobre la cama
        String urlParameters = "token="+token+"&mode=info";
        JSONObject resultado = APIUtil.petitionAPI("/api/bed/perm", urlParameters, context);

        try {
            JSONArray permissionJSON = (JSONArray) resultado.get("permission");
            String bedNameJSON = null;
            String user = null;
            for (int i = 0; i < permissionJSON.length(); i++) {
                JSONObject permission = (JSONObject) permissionJSON.get(i);
                bedNameJSON = (String) permission.get("bed_name");
                if(bedNameJSON.equals(bedName)) {
                    user = (String) permission.get("username");
                    permUsers.add(user);
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        //Consultar todos los usuarios
        urlParameters = "token="+token;
        resultado = APIUtil.petitionAPI("/api/users", urlParameters, context);

        //Mostrar usuarios y marcar los que tienen permisos sobre la cama
        LinearLayout linearLayout = findViewById(R.id.bed_asign_users_linear_layout);
        try {
            JSONArray users = (JSONArray) resultado.get("users");
            String user = null;
            for (int i = 0; i < users.length(); i++) {
                CheckBox checkBox = new CheckBox(context);
                user = (String) users.get(i);
                checkBox.setText(user);
                checkBox.setTextSize(20);
                if(user.equals("admin")){
                    checkBox.setChecked(true);
                    checkBox.setEnabled(false);
                }
                if(permUsers.contains(user)){
                    checkBox.setChecked(true);
                }
                checkBoxList.add(checkBox);
                linearLayout.addView(checkBox);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        //Realizar petición de escritura a la API cuando se marca/desmarca un checkbox.
        for(CheckBox checkBox: checkBoxList){
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String username = (String) buttonView.getText();
                    String urlParameters = "token="+token+"&mode=change&bed_name="+bedName+"&username="+username;
                    JSONObject resultado = APIUtil.petitionAPI("/api/bed/perm", urlParameters, context);
                }
            });
        }
    }

    /**
     * Termina el activity.
     */
    public void back(View view){
        finish();
    }
}
