package com.example.smartbeds;

public class Bed {

    private String bedName;
    private String bedState;

    public Bed(String bedName, String bedState){
        this.bedName=bedName;
        this.bedState=bedState;
    }

    public String getBedName(){
        return this.bedName;
    }

    public String getBedState(){
        return this.bedState;
    }

    public void setBedState(String bedState){ this.bedState=bedState; }
}
