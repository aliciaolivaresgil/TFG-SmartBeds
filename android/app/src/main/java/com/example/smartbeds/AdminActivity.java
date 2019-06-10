package com.example.smartbeds;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final Context context = this;

    private ProgressBar progressBar;
    private DrawerLayout drawer;
    private NavigationView navigation;

    @Override
    protected void onResume(){
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Session session = Session.getInstance();
        if (session.getToken() == null) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }

        progressBar = findViewById(R.id.admin_progress);
        createNavigationMenu();
    }

    public void visualizarCamas(View view) {
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(context, BedsActivity.class);
        startActivity(intent);
    }

    public void gestionarCamas(View view) {
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(context, BedsManagementActivity.class);
        startActivity(intent);
    }

    public void gestionarUsuarios(View view) {
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(context, UsersManagementActivity.class);
        startActivity(intent);
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


        menu.getItem(1).setVisible(false);
        menu.getItem(2).setVisible(false);
        menu.getItem(3).setVisible(false);

        navigation.setNavigationItemSelectedListener(this);
    }

    protected void showMenu(View view){
        drawer.openDrawer(Gravity.LEFT);
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
