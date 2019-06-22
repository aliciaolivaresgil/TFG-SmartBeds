package com.example.smartbeds.session;

/**
 * Clase sessión que implementa el patrón singleton.
 * @author Alicia Olivares Gil
 */
public class Session {
    private static Session instance;
    private static String username;
    private static String token;
    private static String role;

    /**
     * Constructor que inicializa las variables de sesión a null.
     */
    private Session(){
        this.username=null;
        this.token=null;
        this.role=null;
    }

    /**
     * Método estático que devuelve la instancia de sesión.
     * @return Instancia de sesión.
     */
    public static Session getInstance(){
        if(instance == null){
            instance = new Session();
        }
        return instance;
    }

    /**
     * Borra las variables de sesión y resetea la instancia de sesión.
     */
    public static void resetSession(){
        instance=null;
        username=null;
        token=null;
        role=null;
    }

    /**
     * Modifica el nombre de usuario.
     * @param nickname Nombre de usuario.
     */
    public void setUsername(String nickname){
        this.username=nickname;
    }

    /**
     * Modifica el token.
     * @param token Token.
     */
    public void setToken(String token){
        this.token=token;
    }

    /**
     * Modifica el rol del usuario.
     * @param role Rol del usuario.
     */
    public void setRole(String role){
        this.role=role;
    }

    /**
     * Devuelve el nombre de usuario.
     * @return Nombre de usuario.
     */
    public String getUsername(){
        return this.username;
    }

    /**
     * Devuelve el token.
     * @return Token.
     */
    public String getToken(){
        return this.token;
    }

    /**
     * Devuelve el rol del usuario.
     * @return Rol del usuario.
     */
    public String getRole(){
        return this.role;
    }

}
