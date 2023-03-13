package main;

import data.Usuario;
import data.SalaChat;
import hilos.HiloConexion;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import static util.MetodosSocket.*;

public class Main {

    public static DatagramSocket socketServidor;
    public static final int PUERTO = 4444;
    static SalaChat sala;
    static int MAXIMO_CONEXIONES = 10;
    static ArrayList<Thread> hilos = new ArrayList<>();

    public static void main(String[] args) {
        if (iniciarServidor()) {
            sala = new SalaChat(MAXIMO_CONEXIONES);
            System.out.println("Se ha creado la sala con un límite máximo establecido en " + MAXIMO_CONEXIONES + " conexiones");
            while (sala.getNumUsuarios() < MAXIMO_CONEXIONES) {
                System.out.println("Conexiones actuales: " + sala.getNumUsuarios());
                System.out.println("Esperando conexiones...");
                Usuario nuevoUsuario = aceptarUsuario();
                if (nuevoUsuario != null) {
                    if (nuevoUsuario.pedirNombreUsuario()) {
                        if (sala.nombreDisponible(nuevoUsuario.getNombreUsuario())) {
                            sala.anadirUsuario(nuevoUsuario);
                            HiloConexion hilo = new HiloConexion(sala, nuevoUsuario);
                            Thread t = new Thread(hilo);
                            t.start();
                            hilos.add(t);
                        } else {
                            System.out.printf("El nombre ya está cogido por otro usuario");
                            nuevoUsuario.enviar("C:Ese nombre no está disponible");
                        }

                    } else {
                        System.out.println("No se puede unir a la sala sin un nombre válido. Cerrando la conexión");
                        nuevoUsuario.enviar("C:El nombre no puede estar vacío");
                    }
                }
            }
            System.out.println("Se ha llegado al límite de conexiones (" + MAXIMO_CONEXIONES + ")");
            esperarHilos();
            cerrarServidor();
        }
    }

    private static boolean iniciarServidor() {
        boolean correcto = false;
        try {
            socketServidor = new DatagramSocket(PUERTO);
            correcto = true;
            System.out.println("Servidor arrancado en el puerto " + PUERTO);
        } catch (IOException e) {
            System.err.println("Error al intentar arrancar el sevidor");
            throw new RuntimeException(e);
        }
        return correcto;
    }

    private static Usuario aceptarUsuario() {
        Usuario nuevoUsuario = null;
        DatagramPacket paquete = recibirDatagrama(socketServidor);
        if (paquete.getData()[0] == 4) {
            nuevoUsuario = new Usuario(paquete.getAddress(), paquete.getPort());
            System.out.println("Creado nuevo usuario (" + nuevoUsuario.getIPyPuerto() + ")");
            nuevoUsuario.enviarBytes(new byte[]{8});
            System.out.println("Enviada confirmación al nuevo usuario");
        } else {
            System.out.println("El datagrama recibido no es una petición de nuevo usuario");
        }
        return nuevoUsuario;
    }

    private static void esperarHilos() {
        try {
            System.out.println("El servidor se mantendrá abierto hasta que se cierren todos los hilos actuales");
            for (Thread t : hilos) {
                t.join();
            }
        } catch (InterruptedException e) {
            sala.broadcast("C:");
            throw new RuntimeException(e);
        }
    }

    private static void cerrarServidor() {
        sala.broadcast("C:");
        socketServidor.close();
        System.out.println("Servidor desconectado");
    }

}