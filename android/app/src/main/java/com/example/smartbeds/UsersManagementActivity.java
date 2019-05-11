package com.example.smartbeds;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class UsersManagementActivity extends AppCompatActivity {

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_management);

        Session session = Session.getInstance();
        if(session.getToken()==null) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }
    }

    protected void anadirUsuario(View view){
        Intent intent = new Intent(context, UserAddActivity.class);
        startActivity(intent);
    }

    protected void cambiarContrasena(View view){
        Intent intent = new Intent(context, UsersPassChangeActivity.class);
        startActivity(intent);
    }

    protected void eliminarUsuario(View view){
        Intent intent = new Intent(context, UserDelActivity.class);
        startActivity(intent);
    }
}
