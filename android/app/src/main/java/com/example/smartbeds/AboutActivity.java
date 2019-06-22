package com.example.smartbeds;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView text1 = findViewById(R.id.about_text1);
        TextView text2 = findViewById(R.id.about_text2);
        TextView text3 = findViewById(R.id.about_text3);
        TextView text4 = findViewById(R.id.about_text4);
        text1.setText(Html.fromHtml("<b>"+"SmartBeds"+"</b>"+" es un detector automático de crisis epilépticas que emplea datos tomados mediante una matriz de sensores de presión instalados en un colchón. Esta aplicación Android permite gestionar los usuarios y las camas registradas y monitorizar el estado de los pacientes en tiempo real."));
        text2.setText(Html.fromHtml("Este clasificador ha sido desarrollado por "+"<b>"+"José Luis Garrido Labrador"+"</b>"+" y "+"<b>"+"Alicia Olivares Gil"+"</b>"+" como parte del proyecto MINERÍA DE DATOS APLICADA A LA DETECCIÓN DE CRISIS EPILÉPTICAS tutorizado por el "+"<b>"+"Dr. Álvar Arnaiz González"+"</b>"+" y el "+"<b>"+"Dr. José Francios Díez Pastor"+"</b>"+" del grupo ADMIRABLE del Área de Lenguajes y Sistemas Informáticos del departamento de Ingeniería Civil de la Universidad de Burgos."));
        text3.setText("Este proyecto está alojado en la plataforma GitHub bjo la licencia GPLv3: https://github.com/aog0036/TFG-SmartBeds");
        text4.setText(Html.fromHtml("- Android Support Library: Apache License 2.0"+"<br>"+"- MPAndroidChart: Apache License 2.0"+"<br>"+"- Socket.IO-client Library: MIT License"));
    }

    public void back(View view){
        finish();
    }
}
