package com.example.smartbeds.userManagement;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.smartbeds.bedManagement.BedsManagementActivity;
import com.example.smartbeds.generalActivities.MainActivity;
import com.example.smartbeds.R;
import com.example.smartbeds.session.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * UI para la gestión de usuarios.
 * @author Alicia Olivares Gil
 */
public class UsersManagementActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Context context = this;
    private BottomNavigationView bottomNavigation;

    private boolean itemSelected = false;

    private DrawerLayout drawer;
    private NavigationView navigation;
    private TextView tvSelected;

    /**
     * Cierra los menús de navegación o finaliza el activity.
     */
    @Override
    public void onBackPressed() {
        bottomNavigation = findViewById(R.id.users_management_navigation);

        if (bottomNavigation.getVisibility() == View.VISIBLE) {
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
     * Refresca el activity para mostrar la lista de usuarios actualizada.
     * @param requestCode Código de petición.
     * @param resultCode Código de respuesta.
     * @param data Datos.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //refrescar Activity
        finish();
        startActivity(getIntent());
    }

    /**
     * Construye la UI, crea el menú de navegación y muestra la lista de usuarios.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_management);

        Session session = Session.getInstance();
        if (session.getToken() == null) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }

        //Crea el menú de navegación.
        createNavigationMenu();

        //Consulta la lista de usuarios
        String urlParameters = "token=" + session.getToken();
        JSONObject resultado = APIUtil.petitionAPI("/api/users", urlParameters, context);
        int status = APIUtil.getStatusFromJSON(resultado);

        if(status == 200) {
            List<String> names = null;
            try {
                names = APIUtil.JSONArrayToList((JSONArray) resultado.get("users"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Asociar listView al un adapter
            final ArrayAdapter<String> namesAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, names);

            final ListView listView = (ListView) findViewById(R.id.users_management_list);
            listView.setAdapter(namesAdapter);

            bottomNavigation = findViewById(R.id.users_management_navigation);
            tvSelected = findViewById(R.id.users_management_selected);

            //Si se mantiene pulsado un item de la lista se selecciona un usuario y se abre el menú de opciones.
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    bottomNavigation.setVisibility(View.VISIBLE);
                    itemSelected = true;
                    tvSelected.setText(namesAdapter.getItem(position));
                    tvSelected.setVisibility(View.VISIBLE);

                    if (tvSelected.getText().toString().equals("admin")) {
                        bottomNavigation.getMenu().getItem(1).setVisible(false);
                    } else {
                        bottomNavigation.getMenu().getItem(1).setVisible(true);
                    }
                    return true;
                }
            });

            //Si se pulsa otro item de la lista se cambia la selección del usuario.
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (itemSelected) {
                        tvSelected.setText(namesAdapter.getItem(position));
                        if (tvSelected.getText().toString().equals("admin")) {
                            bottomNavigation.getMenu().getItem(1).setVisible(false);
                        } else {
                            bottomNavigation.getMenu().getItem(1).setVisible(true);
                        }
                    }
                }
            });

            //Si se pulsa una opción del menú...
            bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch ((String) menuItem.getTitle()) {
                        case "Eliminar":
                            eliminarUsuario(tvSelected.getText().toString());
                            break;
                        case "Cambiar contraseña":
                            cambiarContrasena(tvSelected.getText().toString());
                            break;
                    }
                    return true;
                }
            });
        }
    }

    /**
     * Crea el menú de navegación.
     */
    private void createNavigationMenu() {
        Session session = Session.getInstance();
        drawer = findViewById(R.id.drawer_layout);
        navigation = findViewById(R.id.navigation_view);
        LinearLayout lLayout = (LinearLayout) navigation.getHeaderView(0).getRootView();
        RelativeLayout rLayout = (RelativeLayout) lLayout.getChildAt(0);

        TextView username = (TextView) rLayout.getChildAt(0);
        username.setText(session.getUsername());

        Menu menu = navigation.getMenu();

        menu.getItem(1).setEnabled(false);

        navigation.setNavigationItemSelectedListener(this);
    }

    /**
     * Listener para el botón que muestra el menú de navegación.
     * @param view Vista.
     */
    public void showMenu(View view) {
        drawer.openDrawer(Gravity.LEFT);
    }

    /**
     * Listener para el botón que lleva a la pantalla de añadir usuario.
     * @param view Vista.
     */
    public void anadirUsuario(View view) {
        Intent intent = new Intent(context, UserAddActivity.class);
        startActivityForResult(intent, 1);
    }

    /**
     * LLeva a la pantalla de modificar contraseña.
     * @param username Nombre del usuario.
     */
    private void cambiarContrasena(String username) {
        Intent intent = new Intent(context, UserPassChangeActivity.class);
        Bundle b = new Bundle();
        b.putString("username", username);
        intent.putExtras(b);
        startActivity(intent);
    }

    /**
     * Muestra el cuadro de diálogo que permite eliminar un usuario.
     * @param username Nombre de usuario.
     */
    private void eliminarUsuario(String username) {
        final String finalName = username;
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle("Eliminar Usuario");
        dialog.setMessage("¿Desea eliminar el usuario " + username + " permanentemente?");
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

    /**
     * Barra un usuario
     * @param name Nombre de usuario.
     */
    private void delUser(String name) {
        Session session = Session.getInstance();
        String urlParameters = "token=" + session.getToken() + "&username=" + name;
        JSONObject resultado = APIUtil.petitionAPI("/api/user/del", urlParameters, context);
        int status = APIUtil.getStatusFromJSON(resultado);

        //refrescar Activity
        finish();
        startActivity(getIntent());
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
        switch ((String) menuItem.getTitle()) {
            case "Modificar Contraseña":
                intent = new Intent(context, UserPassChangeActivity.class);
                Bundle b = new Bundle();
                b.putString("username", session.getUsername());
                intent.putExtras(b);
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

