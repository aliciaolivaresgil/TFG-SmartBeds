package com.example.smartbeds;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BedsActivity extends AppCompatActivity {

    private final Context context = this;
    private boolean created = false;
    private int contador = 0;

    private List<BedStreaming> threads = new ArrayList<BedStreaming>();

    private List<Bed> bedsArray = Collections.synchronizedList(new ArrayList<Bed>());

    private BedAdapter adapter;
    private ListView listView;


    @Override
    protected void onDestroy(){
        super.onDestroy();
        for(BedStreaming thread: threads){
            thread.stop();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(created && contador>0) {
            for (BedStreaming thread : threads) {
                thread.run();
            }
        }
        this.contador++;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.created=true;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beds);

        Session session = Session.getInstance();
        if(session.getToken()==null) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }
        showBeds(session);
    }

    private void showBeds(Session session){
        String urlParameters = "token="+session.getToken();
        JSONObject resultado = APIUtil.petitionAPI("/api/beds", urlParameters);
        int status = APIUtil.getStatusFromJSON(resultado);

        try {
            JSONArray beds_info = (JSONArray) resultado.get("beds");
            String namespace=null;

            //por cada cama se lanza un hilo
            for (int i = 0; i < beds_info.length(); i++) {
                JSONObject bed_info = (JSONObject) beds_info.get(i);
                String bed_name = (String) bed_info.get("bed_name");

                bedsArray.add(new Bed(bed_name, "Estado: ..."));

                urlParameters = "token="+session.getToken()+"&bedname="+bed_name;
                resultado = APIUtil.petitionAPI("/api/bed", urlParameters);
                namespace = (String) resultado.get("namespace");

                BedStreaming bedStreaming = new BedStreaming(i, bed_name, namespace, this);
                threads.add(bedStreaming);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        ArrayList<Bed> aux = new ArrayList<>();
        for(Bed bed: bedsArray){
            aux.add(bed);
        }

        adapter = new BedAdapter(this, aux);
        listView = (ListView) findViewById(R.id.beds_list);
        listView.setAdapter(adapter);

        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bed bed = adapter.getItem(position);

                Intent intent = new Intent(context, BedChartsActivity.class);
                Bundle b = new Bundle();
                b.putString("bedName", bed.getBedName());
                intent.putExtras(b);
                startActivity(intent);

                for(BedStreaming thread: threads){
                    thread.stop();
                }
            }
        });
    }

    public void refresh(int bedId, int state){
        Log.d("NUMERO DE CAMAS", this.bedsArray.toString());
        switch (state){
            case 0:
                this.bedsArray.get(bedId).setBedState("Estado: dormido");
                break;
            case 1:
                bedsArray.get(bedId).setBedState("Estado: crisis epiléptica");
                break;
            case 2:
                bedsArray.get(bedId).setBedState("Estado: cama vacía");
                break;
            case 3:
                bedsArray.get(bedId).setBedState("Estado: datos insuficientes");
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.clear();
                adapter.addAll(bedsArray);
            }
        });

    }
}
