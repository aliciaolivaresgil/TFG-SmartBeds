package com.example.smartbeds.bedManagement;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.smartbeds.API.APIUtil;
import com.example.smartbeds.generalActivities.AboutActivity;
import com.example.smartbeds.bedMonitoring.BedsActivity;
import com.example.smartbeds.util.DialogUtil;
import com.example.smartbeds.generalActivities.MainActivity;
import com.example.smartbeds.R;
import com.example.smartbeds.session.Session;
import com.example.smartbeds.userManagement.UserPassChangeActivity;
import com.example.smartbeds.userManagement.UsersManagementActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * UI de gestión de camas.
 * @author Alicia Olivares Gil
 */
public class BedsManagementActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Context context = this;
    private boolean itemSelected=false;

    private BottomNavigationView bottomNavigation;

    private DrawerLayout drawer;
    private NavigationView navigation;
    private TextView tvSelected;


    /**
     * Cierra los menús o finaliza el activity.
     */
    @Override
    public void onBackPressed(){
        bottomNavigation = findViewById(R.id.beds_management_navigation);

        if(bottomNavigation.getVisibility()==View.VISIBLE) {
            bottomNavigation.setVisibility(View.GONE);
            tvSelected.setVisibility(View.GONE);
            itemSelected = false;
        }else if(drawer.isDrawerOpen(Gravity.LEFT)){
            drawer.closeDrawers();
        }else{
            super.onBackPressed();
        }
    }

    /**
     * Construye la UI, crea el menú de navegación, muestra la lista de camas y gestiona las operaciones.
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beds_management);

        Session session = Session.getInstance();
        if(session.getToken()==null) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }

        //Crear menú de navegación
        createNavigationMenu();

        //Consultar camas
        String urlParameters = "token="+session.getToken();
        JSONObject resultado = APIUtil.petitionAPI("/api/beds", urlParameters, context);
        int status = APIUtil.getStatusFromJSON(resultado);

        if(status==200) {
            List<String> bedNames = new ArrayList<>();
            try {
                JSONArray bedsJSON = (JSONArray) resultado.get("beds");
                for (int i = 0; i < bedsJSON.length(); i++) {
                    JSONObject bedJSON = (JSONObject) bedsJSON.get(i);
                    bedNames.add((String) bedJSON.get("bed_name"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Asociar listView al un adapter
            final ArrayAdapter<String> bedNamesAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, bedNames);
            final ListView listView = (ListView) findViewById(R.id.beds_management_list);
            listView.setAdapter(bedNamesAdapter);

            bottomNavigation = findViewById(R.id.beds_management_navigation);
            tvSelected = findViewById(R.id.beds_management_selected);

            //Si se mantiene pulsado un item de la lista se selecciona una cama y se abre el menú de opciones.
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    bottomNavigation.setVisibility(View.VISIBLE);
                    tvSelected.setText(bedNamesAdapter.getItem(position));
                    tvSelected.setVisibility(View.VISIBLE);
                    itemSelected = true;
                    return true;
                }
            });

            //Si se pulsa otro item de la lista se cambia la selección de la cama.
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (itemSelected) {
                        tvSelected.setText(bedNamesAdapter.getItem(position));
                    }
                }
            });

            //Si se pulsa una opción del menú...
            bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch ((String) menuItem.getTitle()) {
                        case "Eliminar":
                            DialogUtil.showDialog(context, "Eliminar cama", "Esta acción no está disponible en la versión 1.0 de la aplicación.");
                            break;
                        case "Modificar":
                            DialogUtil.showDialog(context, "Modificar datos de la cama", "Esta acción no está disponible en la versión 1.0 de la aplicación.");
                            break;
                        case "Asignar usuarios":
                            Intent intent = new Intent(context, BedAsignUsersActivity.class);
                            Bundle b = new Bundle();
                            b.putString("bedName", tvSelected.getText().toString());
                            intent.putExtras(b);
                            startActivity(intent);
                            break;
                    }
                    return true;
                }
            });
        }
    }

    /**
     * Listener del botón para añadir cama.
     * @param view view
     */
    public void anadirCama(View view){
        DialogUtil.showDialog(context, "Añadir cama", "Esta acción no está disponible en la versión 1.0 de la aplicación.");
    }

    /**
     * Crea el menú de navegación con los items pertinentes.
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

        menu.getItem(2).setEnabled(false);

        navigation.setNavigationItemSelectedListener(this);
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

    /**
     * Listener para el botón que muestra el menú de navegación.
     * @param view view
     */
    public void showMenu(View view){
        drawer.openDrawer(Gravity.LEFT);
    }
}
