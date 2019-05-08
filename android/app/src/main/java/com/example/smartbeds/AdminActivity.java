package com.example.smartbeds;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AdminActivity extends AppCompatActivity {

    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Session session = Session.getInstance();
        if(session.getToken()==null) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }
    }

    public void visualizarCamas(View view){
        Intent intent = new Intent(context, BedsActivity.class);
        startActivity(intent);
    }

    public void gestionarCamas(View view){
        Intent intent = new Intent(context, BedsManagementActivity.class);
        startActivity(intent);
    }

    public void gestionarUsuarios(View view){
        Intent intent = new Intent(context, UsersManagementActivity.class);
        startActivity(intent);
    }
}
