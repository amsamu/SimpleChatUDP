package hilos;

import data.Usuario;
import data.SalaChat;

public class HiloConexion implements Runnable {

    // Atributos
    SalaChat sala;
    Usuario usuario;
    boolean seguirEscuchando = true;

    // Constructor
    public HiloConexion(SalaChat sala, Usuario usuario) {
        this.sala = sala;
        this.usuario = usuario;
    }

    // Código del hilo
    @Override
    public void run() {
        while (seguirEscuchando) {
            String recibido = usuario.recibir();
            tratarCadenaRecibida(recibido);
        }
    }

    private void tratarCadenaRecibida(String recibido) {
        if (recibido.startsWith("M:")) { // M = mensaje
            tratarMensaje(recibido);
        } else if (recibido.startsWith("E:")) { // E = exit (salida)
            tratarSalida();
        } else {
            System.err.println("La cadena recibida tiene un formato incorrecto");
        }
    }

    private void tratarMensaje(String recibido) {
        int posSeparador = recibido.indexOf(">");
        if (posSeparador != -1) {
            // Si tiene el formato correcto, se envía a todos los usuarios del chat.
            sala.broadcast(recibido);
        } else {
            System.err.println("Mensaje con formato incorrecto");
        }
    }

    private void tratarSalida() {
        sala.eliminarUsuario(usuario);
        System.out.println(usuario.getNombreYHost() + " se ha desconectado");
        sala.broadcast("N:" + usuario.getNombreUsuario() + " se ha desconectado");
        seguirEscuchando = false;
    }

}
