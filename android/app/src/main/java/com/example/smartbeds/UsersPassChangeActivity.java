package com.example.smartbeds;

import android.content.Context;
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

public class UsersPassChangeActivity extends AppCompatActivity {

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_pass_change);

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

        ListView listView = (ListView) findViewById(R.id.user_pass_change_list);
        listView.setAdapter(namesAdapter);

        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = namesAdapter.getItem(position);

                //lanzar Activity con parámetro name
                Intent intent = new Intent(context, UserPassChangeActivity.class);
                Bundle b = new Bundle();
                b.putString("username", name);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }


}
