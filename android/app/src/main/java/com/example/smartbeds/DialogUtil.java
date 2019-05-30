package com.example.smartbeds;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

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
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Terminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Activity activity = (Activity) finalContext;
                Session.resetSession();
                Intent intent;
                intent = new Intent(finalContext, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void showUnactiveSessionDialog(Context context){
        final Context finalContext = context;
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle("Error de concurrencia");
        dialog.setMessage("Se ha iniciado sesión con la misma cuenta en otro dispositivo.");
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Activity activity = (Activity) finalContext;
                Session.resetSession();
                Intent intent;
                intent = new Intent(finalContext, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }
}
