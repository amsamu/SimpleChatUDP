package data;

import java.util.ArrayList;

public class SalaChat {

    // Atributos
    private ArrayList<Usuario> listaUsuarios;
    private final int MAXIMO;
    private String historialChat = "";

    // Constructor
    public SalaChat(int MAXIMO) {
        this.MAXIMO = MAXIMO;
        listaUsuarios = new ArrayList<>();
    }

    // Getters
    public int getNumUsuarios() {
        return listaUsuarios.size();
    }

    public int getMAXIMO() {
        return MAXIMO;
    }

    public Usuario getUsuario(int i) {
        Usuario usuario = null;
        if (i < MAXIMO) {
            usuario = listaUsuarios.get(i);
        }
        return usuario;
    }

    // Otros métodos
    public boolean anadirUsuario(Usuario usuario) {
        boolean correcto = false;
        if (listaUsuarios.size() < MAXIMO) {
            listaUsuarios.add(usuario);
            // Aviso al cliente de que se ha unido a la sala
            usuario.enviar("J:" + historialChat);
            // Imprimo por pantalla el aviso y lo muestro a toda la sala
            System.out.println("Se ha agregado a " + usuario.getNombreYHost() + " a la sala");
            broadcast("N:" + usuario.getNombreUsuario() + " se ha unido a la sala");
            correcto = true;
        }
        return correcto;
    }

    public void eliminarUsuario(Usuario u){
        listaUsuarios.remove(u);
    }

    public boolean nombreDisponible(String nombreUsuario){
        boolean disponible = true;
        int i = 0;
        while(disponible && i < listaUsuarios.size()){
            if(listaUsuarios.get(i).getNombreUsuario().equalsIgnoreCase(nombreUsuario)){
                disponible = false;
            }else{
                i++;
            }
        }
        return disponible;
    }

    public void broadcast(String cadena) {
        System.out.println("Broadcasting: \"" + cadena + "\"");
        historialChat += cadena + "\n";
        for (Usuario u : listaUsuarios) {
            u.enviar(cadena);
        }
    }

}
