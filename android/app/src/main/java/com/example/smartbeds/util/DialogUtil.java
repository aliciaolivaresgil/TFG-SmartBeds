package com.example.smartbeds.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.example.smartbeds.session.Session;
import com.example.smartbeds.generalActivities.MainActivity;

/**
 * Clase de utilidad que genera y muestra cuadros de diálogo.
 */
public class DialogUtil {

    /**
     * Genera y muestra cuadro de diálogo genérico.
     * @param context Contexto del activity actual.
     * @param title Título del cuadro.
     * @param description Descripción del cuadro.
     */
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

    /**
     * Genera y muestra un cuadro de diálogo de pérdida de la conexión a internet.
     * @param context Contexto del activity actual.
     */
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
                }else{
                    Activity activity = (Activity) finalContext;
                    if(!activity.getLocalClassName().equals("MainActivity")) {
                        activity.finish();
                    }
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

    /**
     * Genera y muestra un cuadro de diálogo de sesión inactiva.
     * @param context Contexto del activity actual.
     */
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

    /**
     * Genera y muestra un cuadro de diálogo de error interno del servidor.
     * @param context Contexto del activity actual.
     */
    public static void showInternalServerErrorDialog(Context context){
        final Context finalContext = context;
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle("Error interno del servidor");
        dialog.setMessage("Se ha producido un error interno en el servidor.");
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Terminar", new DialogInterface.OnClickListener() {
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
