package com.example.smartbeds.API;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.smartbeds.session.Session;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Hilo que gestiona la comunicación en tiempo real con la API para la recepción de los datos de una cama.
 * @author Alicia Olivares Gil
 */
public class BedStreaming implements Runnable {

    private Thread thread;

    private int bedId;
    private String bedName;
    private int state;
    private String namespace;

    private Socket mSocket;
    private Socket inSocket;
    private Manager manager;

    private JSONObject data;
    Handler handler = null;

    Session session;

    /**
     * Constructor que lanza el hilo.
     * @param bedId Identificador de la cama.
     * @param bedName Nombre de la cama.
     * @param nameSpace Namespace.
     * @param handler Handler para la notificación de los cambios a la interfaz.
     */
    public BedStreaming(int bedId, String bedName, String nameSpace, Handler handler){
        this.bedId=bedId;
        this.handler=handler;
        this.bedName=bedName;
        this.namespace=nameSpace;
        this.thread = new Thread(this);
        this.session = Session.getInstance();
        this.thread.start();
    }


    /**
     * Método run del hilo.
     * Abre y gestiona los sockets para la recepción de datos en tiempo real.
     */
    @Override
    public void run() {

        data = new JSONObject();
        try {
            data.put("namespace", namespace);
            data.put("bedname", bedName);
        }catch(JSONException e){
            e.printStackTrace();
        }
        Log.d("data", data.toString());

        try {
            //Se abren los sockets
            manager = new Manager(new URI("https://ubu.joselucross.com"));
            manager.open();
            mSocket = manager.socket("/"); //namespace genérico
            inSocket = manager.socket("/"+namespace);
            mSocket.open();
            inSocket.open();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        //Al conectar el primer socket notificar y emitir evento give_me_data
        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if(mSocket.connected()){
                    Log.d("CONECTADO", "SI");
                }
                mSocket.emit("give_me_data", data);
            }
        });

        //Al conectar el segundo socket notificar.
        inSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if(inSocket.connected()){
                    Log.d("CONECTADO 2", "SI");
                }
            }
        });

        //Al recibir un evento package notificar a la interfaz.
        inSocket.on("package", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject resultado = (JSONObject) args[0];
                Log.d("RESULTADO", bedName+" "+resultado.toString());

                Message message = new Message();
                message.obj = resultado;
                message.arg1 = bedId;

                handler.sendMessage(message);

                try {
                    if (!thread.isInterrupted()) {
                        thread.sleep(200);
                    }
                }catch(Throwable e){
                        e.printStackTrace();
                }
            }
        });

    }

    /**
     * Método stop que para la ejecución del hilo.
     */
    public void stop(){
        if(mSocket!=null){
            mSocket.close();
        }
        if(inSocket!=null){
            inSocket.close();
        }
        inSocket.off("package");
        inSocket.off(Socket.EVENT_CONNECT);
        thread.interrupt();
    }
}
