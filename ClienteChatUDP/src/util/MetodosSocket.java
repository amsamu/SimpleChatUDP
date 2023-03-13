package util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

import static main.Main.socket;
import static main.Main.IPservidor;
import static main.Main.puerto;

public class MetodosSocket {
	
	public static void enviar(String cadena) {
		byte[] bytes = cadena.getBytes();
		int posInicio = 0;
		while(posInicio < bytes.length) {
			// Mando los bytes de la cadena de 1024 en 1024.
			enviarDatagrama(Arrays.copyOfRange(bytes, posInicio, posInicio + 1024));
			posInicio += 1024;
		}
		byte[] fin = new byte[1024];
		fin[0] = -128;
		enviarDatagrama(fin);
		System.out.println("Se ha enviado: " + cadena);
	}

	public static String recibir() {
		StringBuilder sb = new StringBuilder();
		byte[] bytes = recibirDatagrama().getData();
		while(bytes[0] != -128) {
			sb.append(new String(bytes));
			bytes = recibirDatagrama().getData();
		}
		return sb.toString().trim();
	}

	public static void enviarDatagrama(byte[] enviados) {
		DatagramPacket envio = new DatagramPacket(enviados, enviados.length, IPservidor, puerto);
		try {
			socket.send(envio);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static DatagramPacket recibirDatagrama() {
		byte[] buffer = new byte[1024];
		DatagramPacket recibo = new DatagramPacket(buffer, buffer.length);
		try {
			socket.receive(recibo);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return recibo;
	}
	
}
