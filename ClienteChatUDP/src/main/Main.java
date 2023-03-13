package main;

import gui.ClienteFrame;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static util.MetodosSocket.*;

public class Main {

    public static DatagramSocket socket = null;
    static ClienteFrame frame;
    static String servidor = "localhost";
    public static int puerto = 4444;
    public static InetAddress IPservidor;

    static String nombreUsuario = "";

    public static void main(String[] args) {
        pedirNombre();
        if (inicializarSocket()) {
            if(pedirSocket()){
                escuchar();
            }
        }
    }

    public static boolean inicializarSocket() {
        boolean correcto = false;
        try {
            System.out.println("Inicializando socket y la InetAddress del servidor (" + servidor + " en el puerto " + puerto + ")");
            IPservidor = InetAddress.getByName(servidor);
            socket = new DatagramSocket();
            correcto = true;
            System.out.println("Socket e InetAddress inicializados correctamente");
        } catch (IOException e) {
            System.err.println("Error al inicializar el socket o la InetAddress");
            e.printStackTrace();
            mostrarError("No se ha podido establecer la conexión con el servidor.", "Error");
        }
        return correcto;
    }

    private static boolean pedirSocket(){
        boolean aceptado = false;
        // Le pido al servidor que me abra un hilo y me cree un socket dedicado
        enviarDatagrama(new byte[]{4});
        DatagramPacket paquete = recibirDatagrama();
        if(paquete.getData()[0] == 8){
            IPservidor = paquete.getAddress();
            puerto = paquete.getPort();
            aceptado = true;
            System.out.println("El servidor ha aceptado crear un socket");
        }else{
            System.err.println("El servidor no ha aceptado crear un socket dedicado");
        }
        return aceptado;
    }

    public static void iniciarGUI() {
        frame = new ClienteFrame(nombreUsuario);
        frame.setVisible(true);
        System.out.println("GUI iniciada");
    }

    public static void escuchar() {
        while (true) {
            System.out.println("Esperando a recibir una cadena...");
            String recibido = recibir();
            System.out.println("Cadena recibida: \"" + recibido + "\"");
            tratarCadenaRecibida(recibido);
        }
    }

    private static void tratarCadenaRecibida(String recibido) {
        String contenido = recibido.substring(2);
        if (recibido.startsWith("J:")) { // J = joined (unido a la sala)
            tratarUnido(contenido);
        } else if (recibido.startsWith("N:")) { // N = notificación
            tratarNotificacion(contenido);
        } else if (recibido.startsWith("R:")) { // R = request (petición)
            tratarRequest(contenido);
        } else if (recibido.startsWith("M:")) { // M = mensaje
            tratarMensaje(contenido);
        } else if (recibido.startsWith("C:")) { // C = cierre
            tratarCierre(contenido);
        } else {
            System.err.println("La cadena recibida tiene un formato incorrecto");
        }
    }

    private static void tratarUnido(String historialChat) {
        iniciarGUI();
        if (!historialChat.trim().isEmpty()) {
            System.out.println("Cargando historial de mensajes");
            String[] mensajes = historialChat.split("\n");
            for (String m : mensajes) {
                if (!m.isEmpty()) {
                    tratarCadenaRecibida(m);
                }
            }
        } else {
            System.out.println("Historial de mensajes vacío");
        }
    }

    private static void tratarNotificacion(String notificacion) {
        frame.anadirTexto(notificacion);
    }

    private static void tratarRequest(String peticion) {
        if (peticion.startsWith("username")) {
            System.out.println("Petición de nombre de usuario");
            //pedirNombre();
            enviar("U:" + nombreUsuario);
        } else {
            System.err.println("Petición desconocida");
        }
    }

    private static void tratarMensaje(String mensaje) {
        int posSeparador = mensaje.indexOf(">");
        if (posSeparador != -1) {
            // Separo el nombre del usuario del contenido del mensaje
            String emisor = mensaje.substring(0, posSeparador);
            String contenidoMensaje = mensaje.substring(posSeparador + 1);
            System.out.println("El usuario \"" + emisor + " ha enviado el mensaje \"" + contenidoMensaje + "\"");
            // Añado el mensaje al GUI
            frame.anadirMensaje(emisor, contenidoMensaje);
        } else {
            System.err.println("Mensaje con formato incorrecto: \"" + mensaje + "\"");
        }
    }

    private static void tratarCierre(String mensajeCierre) {
        System.out.println("Recibida notificación de cierre de la conexión con el siguiente mensaje: \""
                + mensajeCierre + "\"");
        JOptionPane.showMessageDialog(null, mensajeCierre);
        System.out.println("Cerrando la aplicación");
        System.exit(0);
    }

    static void pedirNombre() {
        boolean nombreCorrecto = false;
        while (!nombreCorrecto) {
            System.out.println("Pidiendo al usuario que elija un nombre");
            nombreUsuario = JOptionPane.showInputDialog("Introduce tu nombre: ");
            if (nombreUsuario.trim().equals("")) {
                System.err.println("El usuario ha introducido un nombre no válido");
                mostrarError("Elige un nombre válido", "Error");
            } else {
                nombreCorrecto = true;
                System.out.println("El usuario ha elegido el nombre: " + nombreUsuario);
            }
        }
    }

    public static void mostrarError(String mensaje, String titulo) {
        JOptionPane.showMessageDialog(null, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
    }

}