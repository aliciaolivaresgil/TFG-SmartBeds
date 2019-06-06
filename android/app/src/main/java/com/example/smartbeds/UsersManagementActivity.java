package com.example.smartbeds;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class UsersManagementActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Context context = this;
    private BottomNavigationView bottomNavigation;
    private int focusedItem = 0;
    private boolean itemSelected = false;

    private DrawerLayout drawer;
    private NavigationView navigation;

    @Override
    public void onBackPressed() {
        bottomNavigation = findViewById(R.id.users_management_navigation);
        ListView listView = findViewById(R.id.users_management_list);
        if (bottomNavigation.getVisibility() == View.VISIBLE) {
            bottomNavigation.setVisibility(View.GONE);
            listView.getChildAt(focusedItem).setBackgroundColor(ContextCompat.getColor(context, R.color.background));
            itemSelected = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //refrescar Activity
        finish();
        startActivity(getIntent());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_management);

        Session session = Session.getInstance();
        if (session.getToken() == null) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }

        createNavigationMenu();

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

            final ArrayAdapter<String> namesAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, names);

            final ListView listView = (ListView) findViewById(R.id.users_management_list);
            listView.setAdapter(namesAdapter);

            bottomNavigation = findViewById(R.id.users_management_navigation);

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    bottomNavigation.setVisibility(View.VISIBLE);
                    parent.getChildAt(focusedItem).setBackgroundColor(ContextCompat.getColor(context, R.color.background));
                    view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryLight));
                    focusedItem = position;
                    itemSelected = true;
                    if (namesAdapter.getItem(focusedItem).equals("admin")) {
                        bottomNavigation.getMenu().getItem(1).setVisible(false);
                    } else {
                        bottomNavigation.getMenu().getItem(1).setVisible(true);
                    }
                    return true;
                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position != focusedItem && itemSelected) {
                        parent.getChildAt(focusedItem).setBackgroundColor(ContextCompat.getColor(context, R.color.background));
                        view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryLight));
                        focusedItem = position;
                        if (namesAdapter.getItem(focusedItem).equals("admin")) {
                            bottomNavigation.getMenu().getItem(1).setVisible(false);
                        } else {
                            bottomNavigation.getMenu().getItem(1).setVisible(true);
                        }
                    }
                }
            });

            bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch ((String) menuItem.getTitle()) {
                        case "Eliminar":
                            eliminarUsuario(namesAdapter.getItem(focusedItem));
                            break;
                        case "Cambiar contraseña":
                            cambiarContrasena(namesAdapter.getItem(focusedItem));
                            break;
                    }
                    return true;
                }
            });
        }
    }

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

    protected void showMenu(View view) {
        drawer.openDrawer(Gravity.LEFT);
    }


    public void anadirUsuario(View view) {
        Intent intent = new Intent(context, UserAddActivity.class);
        startActivityForResult(intent, 1);
    }

    private void cambiarContrasena(String username) {
        Intent intent = new Intent(context, UserPassChangeActivity.class);
        Bundle b = new Bundle();
        b.putString("username", username);
        intent.putExtras(b);
        startActivity(intent);
    }

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

    private void delUser(String name) {
        Session session = Session.getInstance();
        String urlParameters = "token=" + session.getToken() + "&username=" + name;
        JSONObject resultado = APIUtil.petitionAPI("/api/user/del", urlParameters, context);
        int status = APIUtil.getStatusFromJSON(resultado);

        //refrescar Activity
        finish();
        startActivity(getIntent());
    }

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
        }
        return true;
    }
}

