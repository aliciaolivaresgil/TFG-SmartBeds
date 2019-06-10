package com.example.smartbeds;

public class Bed {

    private String bedName;
    private String bedState;
    private boolean color;

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

    public void setColor(boolean color){this.color=color; }

    public boolean getColor(){ return this.color; }
}
