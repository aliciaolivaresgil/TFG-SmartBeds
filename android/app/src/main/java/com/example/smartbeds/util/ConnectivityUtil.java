package com.example.smartbeds.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Clase de utilidad para comprobar si existe conexión a internet.
 */
public class ConnectivityUtil {

    /**
     * Comprueba si existe conexión a internet.
     * @param context Contexto del activity actual.
     * @return true si existe, false si no existe.
     */
    public static boolean checkInternetConnection(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

}
