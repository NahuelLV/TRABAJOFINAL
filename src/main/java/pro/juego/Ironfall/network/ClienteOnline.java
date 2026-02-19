package pro.juego.Ironfall.network;

import java.net.*;

public class ClienteOnline {

    private static final int PUERTO = 4321;

    public boolean buscarServidor() {

        try {

            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);

            byte[] buffer = "BUSCAR".getBytes();

            DatagramPacket packet = new DatagramPacket(
                    buffer,
                    buffer.length,
                    InetAddress.getByName("255.255.255.255"),
                    PUERTO
            );

            socket.send(packet);

            byte[] respuestaBuffer = new byte[256];
            DatagramPacket respuesta = new DatagramPacket(
                    respuestaBuffer,
                    respuestaBuffer.length
            );

            socket.setSoTimeout(5000);
            socket.receive(respuesta);

            String msg = new String(respuesta.getData()).trim();

            socket.close();

            return msg.equals("OK");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
