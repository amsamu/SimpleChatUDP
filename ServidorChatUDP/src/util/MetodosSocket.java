package util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class MetodosSocket {

	public static void enviarDatagrama(byte[] enviados, DatagramSocket socket, InetAddress IPdestino, int puerto) {
		DatagramPacket envio = new DatagramPacket(enviados, enviados.length, IPdestino, puerto);
		try {
			socket.send(envio);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void enviarCadena(String cadena, DatagramSocket socket, InetAddress IPdestino, int puerto) {
		byte[] bytes = cadena.getBytes();
		int posInicio = 0;
		while(posInicio < bytes.length) {
			// Mando los bytes de la cadena de 1024 en 1024.
			enviarDatagrama(Arrays.copyOfRange(bytes, posInicio, posInicio + 1024), socket, IPdestino, puerto);
			posInicio += 1024;
		}
		byte[] fin = new byte[1024];
		fin[0] = -128;
		enviarDatagrama(fin, socket, IPdestino, puerto);
	}

	public static DatagramPacket recibirDatagrama(DatagramSocket socket) {
		byte[] buffer = new byte[1024];
		DatagramPacket recibo = new DatagramPacket(buffer, buffer.length);
		try {
			socket.receive(recibo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return recibo;
	}

	public static String recibirCadena(DatagramSocket socket) {
		StringBuilder sb = new StringBuilder();
		DatagramPacket recibo = recibirDatagrama(socket);
		byte[] bytes = recibo.getData();
		while(bytes[0] != -128) {
			sb.append(new String(bytes));
			bytes = recibirDatagrama(socket).getData();
		}
		return sb.toString().trim();
	}

}
