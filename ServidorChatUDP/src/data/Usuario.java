package data;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import static util.MetodosSocket.*;

public class Usuario {

    // Atributos
    private InetAddress IPusuario;
    private int puerto;
    private DatagramSocket socket;
    private String nombreUsuario;


    //Constructor
    public Usuario(InetAddress IPusuario, int puerto) {
        this.IPusuario = IPusuario;
        this.puerto = puerto;
        inicializarSocket();
    }

    private void inicializarSocket() {
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    // Getters y setters
    public InetAddress getIPusuario() {
        return IPusuario;
    }

    public void setIPusuario(InetAddress IPusuario) {
        this.IPusuario = IPusuario;
    }

    public int getPuerto() {
        return puerto;
    }

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getIPyPuerto(){
        return IPusuario.getHostAddress() + ":" + puerto;
    }

    public String getNombreYHost() {
        return nombreUsuario + "@" + IPusuario.getHostAddress();
    }


    // Métodos de socket
    public void enviarBytes(byte[] contenido) {
        enviarDatagrama(contenido, socket, IPusuario, puerto);
    }

    public void enviar(String cadena) {
        enviarCadena(cadena, socket, IPusuario, puerto);
        System.out.println("Se ha enviado a " + getNombreYHost() + " la siguiente cadena: \"" + cadena + "\"");
    }

    public String recibir() {
        String recibido = null;
        System.out.println("Esperando a recibir una cadena...");
        recibido = recibirCadena(socket);
        System.out.println("El usuario " + getNombreYHost() + " ha enviado al servidor la siguiente cadena: \"" + recibido + "\"");
        return recibido;
    }

    public boolean pedirNombreUsuario() {
        boolean correcto = true;
        // Enviar al usuario una petición de nombre de usuario
        enviar("R:username");
        // Recibir el nombre de usuario
        String recibido = recibir();
        // Tratar la cadena recibida
        if (recibido.startsWith("U:")) { // U = username
            correcto = establecerNombreUsuario(recibido.substring(2));
        } else {
            System.err.println("La cadena recibida tiene un formato incorrecto");
        }
        return correcto;
    }

    private boolean establecerNombreUsuario(String nombre) {
        boolean correcto = false;
        if (!nombre.isEmpty()) {
            setNombreUsuario(nombre);
            System.out.println("Se ha establecido el nombre de usuario " + nombre);
            correcto = true;
        } else {
            System.err.println("El nombre que se ha recibido está vacío");
        }
        return correcto;
    }

}
