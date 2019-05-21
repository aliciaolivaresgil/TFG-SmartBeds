package com.example.smartbeds;

import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class BedStreaming implements Runnable {

    private Thread thread;

    private BedsActivity listener1 = null;
    private BedChartsActivity listener2 = null;

    private int bedId;
    private String bedName;
    private int state;
    private String namespace;

    private Socket mSocket;
    private Socket inSocket;
    private Manager manager;

    private JSONObject data;

    public BedStreaming(int bedId, String bedName, String nameSpace, BedsActivity listener){
        this.bedId=bedId;
        this.listener1=listener;
        this.bedName=bedName;
        this.namespace=nameSpace;
        this.thread = new Thread(this);
        this.thread.start();
    }

    public BedStreaming(int bedId, String bedName, String nameSpace, BedChartsActivity listener){
        this.bedId=bedId;
        this.listener2=listener;
        this.bedName=bedName;
        this.namespace=nameSpace;
        this.thread = new Thread(this);
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
                Log.d("RESULTADO", resultado.toString());

                try {
                    if(listener1!=null) {
                        JSONArray array = (JSONArray) resultado.get("result");
                        state = (int) array.get(0);
                        listener1.refresh(bedId, state);
                    }else{
                        listener2.refresh(resultado);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

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