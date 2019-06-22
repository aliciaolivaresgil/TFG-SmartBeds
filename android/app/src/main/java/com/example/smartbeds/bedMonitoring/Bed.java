package com.example.smartbeds.bedMonitoring;

/**
 * Clase Bed que representa una cama.
 * @author Alicia Olivares Gil
 */
public class Bed {

    private String bedName;
    private String bedState;
    private boolean color;

    /**
     * Constructor que almacena los datos de la cama.
     * @param bedName Nombre de la cama.
     * @param bedState Estado del paciente de la cama.
     */
    public Bed(String bedName, String bedState){
        this.bedName=bedName;
        this.bedState=bedState;
    }

    /**
     * Devuelve el nombre de la cama.
     * @return Nombre de la cama.
     */
    public String getBedName(){
        return this.bedName;
    }

    /**
     * Devuelve el estado del paciente de la cama.
     * @return Estado del paciente de la cama.
     */
    public String getBedState(){
        return this.bedState;
    }

    /**
     * Modifica el estado del paciente de la cama.
     * @param bedState Estado del paciente de la cama.
     */
    public void setBedState(String bedState){ this.bedState=bedState; }

    /**
     * Modifica el color asociado al estado del paciente de la cama.
     * @param color Color asociado.
     */
    public void setColor(boolean color){this.color=color; }

    /**
     * Devuelve el color asociado al estado del paciente de la cama.
     * @return Color asociado.
     */
    public boolean getColor(){ return this.color; }
}
