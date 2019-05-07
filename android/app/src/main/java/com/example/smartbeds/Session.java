package com.example.smartbeds;

public class Session {
    private static Session instance;
    private static String username;
    private static String token;
    private static String role;

    private Session(){
        this.username=null;
        this.token=null;
        this.role=null;
    }

    public static Session getInstance(){
        if(instance == null){
            instance = new Session();
        }
        return instance;
    }

    public static void resetSession(){
        instance=null;
        username=null;
        token=null;
        role=null;
    }

    public void setUsername(String nickname){
        this.username=nickname;
    }

    public void setToken(String token){
        this.token=token;
    }

    public void setRole(String role){
        this.role=role;
    }

    public String getUsername(){
        return this.username;
    }

    public String getToken(){
        return this.token;
    }

    public String getRole(){
        return this.role;
    }
}
