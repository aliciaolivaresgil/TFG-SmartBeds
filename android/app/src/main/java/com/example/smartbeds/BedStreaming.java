package com.example.smartbeds;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.locks.ReentrantLock;

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

    public BedStreaming(int bedId, String bedName, String nameSpace, Handler handler){
        this.bedId=bedId;
        this.handler=handler;
        this.bedName=bedName;
        this.namespace=nameSpace;
        this.thread = new Thread(this);
        this.session = Session.getInstance();
        this.thread.start();
    }


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
            manager = new Manager(new URI("https://ubu.joselucross.com"));
            manager.open();
            mSocket = manager.socket("/"); //namespace genérico
            inSocket = manager.socket("/"+namespace);
            mSocket.open();
            inSocket.open();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if(mSocket.connected()){
                    Log.d("CONECTADO", "SI");
                }
                mSocket.emit("give_me_data", data);
            }
        });

        inSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if(inSocket.connected()){
                    Log.d("CONECTADO 2", "SI");
                }
            }
        });

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
