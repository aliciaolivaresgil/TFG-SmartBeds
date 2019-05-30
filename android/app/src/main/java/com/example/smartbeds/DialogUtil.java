package com.example.smartbeds;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogUtil {

    public static void showDialog(Context context, String title, String description){
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle(title);
        dialog.setMessage(description);
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });
        dialog.show();
    }

    public static void showConnectionLostDialog(Context context){
        final Context finalContext = context;
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle("Error de conexión");
        dialog.setMessage("No existe conexión a internet.");
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Reintentar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!ConnectivityUtil.checkInternetConnection(finalContext)){
                    DialogUtil.showConnectionLostDialog(finalContext);
                }
            }
        });
        dialog.show();
    }
}
