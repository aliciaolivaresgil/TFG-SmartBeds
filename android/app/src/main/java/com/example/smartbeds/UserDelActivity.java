package com.example.smartbeds;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class UserDelActivity extends AppCompatActivity {

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_del);

        Session session = Session.getInstance();
        if(session.getToken()==null) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }

        String urlParameters = "token="+session.getToken();
        JSONObject resultado = APIUtil.petitionAPI("/api/users", urlParameters);
        int status = APIUtil.getStatusFromJSON(resultado);

        List<String> names = null;
        try {
            names = APIUtil.JSONArrayToList((JSONArray) resultado.get("users"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ArrayAdapter<String> namesAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, names);

        ListView listView = (ListView) findViewById(R.id.user_del_list);
        listView.setAdapter(namesAdapter);

        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = namesAdapter.getItem(position);
                showDialog(name);
            }
        });
    }

    public void showDialog(String name){

        final String finalName= name;
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle("Eliminar Usuario");
        dialog.setMessage("¿Desea eliminar el usuario "+name+" permanentemente?");
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delUser(finalName);
            }
        });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    public void delUser(String name){
        Session session = Session.getInstance();
        String urlParameters = "token="+session.getToken()+"&username="+name;
        JSONObject resultado = APIUtil.petitionAPI("/api/user/del", urlParameters);
        int status = APIUtil.getStatusFromJSON(resultado);

        //refrescar Activity
        finish();
        startActivity(getIntent());
    }
}
