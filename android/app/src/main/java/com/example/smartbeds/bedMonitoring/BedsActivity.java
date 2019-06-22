package com.example.smartbeds.bedMonitoring;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.smartbeds.API.APIUtil;
import com.example.smartbeds.generalActivities.AboutActivity;
import com.example.smartbeds.API.BedStreaming;
import com.example.smartbeds.generalActivities.MainActivity;
import com.example.smartbeds.R;
import com.example.smartbeds.session.Session;
import com.example.smartbeds.bedManagement.BedsManagementActivity;
import com.example.smartbeds.userManagement.UserPassChangeActivity;
import com.example.smartbeds.userManagement.UsersManagementActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * UI de visualización de camas.
 * @author Alicia Olivares Gil
 */
public class BedsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final Context context = this;
    private boolean created = false;
    private int contador = 0;

    private List<BedStreaming> threads = new ArrayList<BedStreaming>();

    private List<Bed> bedsArray = Collections.synchronizedList(new LinkedList<Bed>());

    private BedAdapter adapter;
    private ListView listView;

    private DrawerLayout drawer;
    private NavigationView navigation;

    private ProgressBar progressBar;

    /**
     * Handler para recibir notificaciones de actualización de los datos.
     */
    private Handler handler = new Handler(){
        /**
         * Cuando se recibe un mensaje de notificación se actualiza la interfaz con los nuevos datos.
         * @param inputMessage Mensaje con los nuevos datos.
         */
        @Override
        public void handleMessage(Message inputMessage){

            int bedId = -1;
            int state = -1;
            try {
                JSONObject resultado = (JSONObject) inputMessage.obj;
                JSONArray aux = (JSONArray) resultado.get("result");
                state = (int) aux.get(0);
                bedId = inputMessage.arg1;
            }catch (JSONException e){
                e.printStackTrace();
            }
            refresh(bedId, state);
        }
    };

    /**
     * Cierra el menú de navegación o termina el activity.
     */
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(Gravity.LEFT)){
            drawer.closeDrawers();
        }else {
            super.onBackPressed();
        }
    }

    /**
     * Cierra los hilos de comunicación con la API.
     */
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d("CERRANDO HILOS", "Los hilos se están cerrando.");
        for(BedStreaming thread: threads){
            thread.stop();
        }
    }

    /**
     * Cierra los hilos de comunicación con la API.
     */
    @Override
    protected void onPause(){
        super.onPause();
        Log.d("CERRANDO HILOS", "Los hilos se están cerrando ");
        for(BedStreaming thread: threads){
            thread.stop();
        }
    }

    /**
     * Reinicia la ejecución de los hilos de comunicación con la API.
     */
    @Override
    protected void onResume(){
        super.onResume();
        progressBar.setVisibility(View.GONE);
        if(created && contador>0) {
            for (BedStreaming thread : threads) {
                thread.run();
            }
        }
        this.contador++;
    }

    /**
     * Construye la UI, crea el menú de navegación y muestra la lista de camas con su estado.
     * @param savedInstanceState savedInstanceState
     */
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

        progressBar = findViewById(R.id.beds_progress);

        //crea menú de navegación
        createNavigationMenu();

        //Muestra las camas con su estado en tiempo real
        showBeds(session);
    }

    /**
     * Muestra las camas con su estado en tiempo real
     * @param session Sesión
     */
    private void showBeds(Session session){
        String urlParameters = "token="+session.getToken();
        JSONObject resultado = APIUtil.petitionAPI("/api/beds", urlParameters, context);
        int status = APIUtil.getStatusFromJSON(resultado);

        try {
            JSONArray beds_info = (JSONArray) resultado.get("beds");
            String namespace=null;

            TextView info = findViewById(R.id.beds_info);
            if(beds_info.length()==0){
                info.setVisibility(View.VISIBLE);
            }

            //por cada cama se lanza un hilo para recuperar sus datos en tiempo real
            for (int i = 0; i < beds_info.length(); i++) {
                JSONObject bed_info = (JSONObject) beds_info.get(i);
                String bed_name = (String) bed_info.get("bed_name");

                bedsArray.add(new Bed(bed_name, "Estado: ..."));

                urlParameters = "token="+session.getToken()+"&bedname="+bed_name;
                resultado = APIUtil.petitionAPI("/api/bed", urlParameters, context);
                namespace = (String) resultado.get("namespace");

                BedStreaming bedStreaming = new BedStreaming(i, bed_name, namespace, handler);
                threads.add(bedStreaming);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        //Se genera la vista de la lista de camas
        ArrayList<Bed> aux = new ArrayList<>();
        for(Bed bed: bedsArray){
            aux.add(bed);
        }

        adapter = new BedAdapter(this, aux);
        listView = (ListView) findViewById(R.id.beds_list);
        listView.setAdapter(adapter);

        listView.setClickable(true);
        //Listener de cada item cama
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Inicia la pantalla de visualización de datos de la cama.
             * @param parent Vista padre.
             * @param view Vista.
             * @param position Posición dentro del adapter.
             * @param id Identificador.
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                progressBar.setVisibility(View.VISIBLE);
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

    /**
     * Crea el menú de navegación.
     */
    private void createNavigationMenu(){
        Session session = Session.getInstance();
        drawer = findViewById(R.id.drawer_layout);
        navigation = findViewById(R.id.navigation_view);
        LinearLayout lLayout = (LinearLayout) navigation.getHeaderView(0).getRootView();
        RelativeLayout rLayout = (RelativeLayout) lLayout.getChildAt(0);

        TextView username = (TextView) rLayout.getChildAt(0);
        username.setText(session.getUsername());

        Menu menu = navigation.getMenu();

        if(session.getRole().equals("user")){
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(false);
        }else{
            menu.getItem(3).setEnabled(false);
        }

        navigation.setNavigationItemSelectedListener(this);
    }

    /**
     * Refresca la UI en función de los datos en tiempo real
     * @param bedId Identificador de la cama.
     * @param state Estado del paciente de la cama.
     */
    public void refresh(int bedId, int state){
        switch (state){
            case 0:
                bedsArray.get(bedId).setBedState("Estado: dormido");
                bedsArray.get(bedId).setColor(false);
                break;
            case 1:
                bedsArray.get(bedId).setBedState("Estado: crisis epiléptica");
                bedsArray.get(bedId).setColor(true);
                break;
            case 2:
                bedsArray.get(bedId).setBedState("Estado: cama vacía");
                bedsArray.get(bedId).setColor(false);
                break;
            case 3:
                bedsArray.get(bedId).setBedState("Estado: datos insuficientes");
                bedsArray.get(bedId).setColor(false);
        }

        adapter.clear();
        adapter.addAll(bedsArray);
    }

    /**
     * Listener para el botón que muestra el menú de navegación.
     * @param view view
     */
    public void showMenu(View view){
        drawer.openDrawer(Gravity.LEFT);
    }

    /**
     * Listener para la selección de una opción del menú de navegación.
     * @param menuItem Item seleccionado.
     * @return boolean
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Session session = Session.getInstance();
        Intent intent;
        switch ((String) menuItem.getTitle()){
            case "Modificar Contraseña":
                intent = new Intent(context, UserPassChangeActivity.class);
                Bundle b = new Bundle();
                b.putString("username", session.getUsername());
                intent.putExtras(b);
                startActivity(intent);
                break;
            case "Gestión de usuarios":
                intent = new Intent(context, UsersManagementActivity.class);
                startActivity(intent);
                break;
            case "Gestión de camas":
                intent = new Intent(context, BedsManagementActivity.class);
                startActivity(intent);
                break;
            case "Visualización de camas":
                intent = new Intent(context, BedsActivity.class);
                startActivity(intent);
                break;
            case "Cerrar sesión":
                Session.resetSession();
                intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case "Acerca de":
                intent = new Intent(context, AboutActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
