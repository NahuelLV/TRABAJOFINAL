package pro.juego.Ironfall.network;

import pro.juego.Ironfall.enums.TipoUnidad;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

public class ClienteOnline extends Thread {

    public interface Listener {
        void onEquipoAsignado(int equipo);
        void onStart();
        void onEsperando();
        void onRivalSpawn(TipoUnidad tipo);
        void onError(String msg);
    }

    public static final int PORT = 4321;

    private DatagramSocket socket;
    private InetAddress ipServer;
    private int puertoServer = PORT;

    private volatile java.util.function.Consumer<TipoUnidad> rivalSpawnHook = null;

    private final Listener listener;
    private volatile boolean fin = false;

    public ClienteOnline(Listener listener) {
        this.listener = listener;
        setDaemon(true);
    }

    public InetAddress buscarServidor() {
        DatagramSocket s = null;
        try {
            s = new DatagramSocket();
            s.setBroadcast(true);
            s.setSoTimeout(1500);

            byte[] data = "BUSCAR".getBytes();

            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            while (ifaces.hasMoreElements()) {
                NetworkInterface ni = ifaces.nextElement();
                if (!ni.isUp() || ni.isLoopback()) continue;

                for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
                    InetAddress bcast = ia.getBroadcast();
                    if (bcast == null) continue;
                    DatagramPacket p = new DatagramPacket(data, data.length, bcast, PORT);
                    s.send(p);
                    System.out.println("[CLIENTE] Envié BUSCAR a " + bcast.getHostAddress() + ":" + PORT + " (iface " + ni.getName() + ")");
                }
            }

            DatagramPacket pGlobal = new DatagramPacket(data, data.length, InetAddress.getByName("255.255.255.255"), PORT);
            s.send(pGlobal);
            System.out.println("[CLIENTE] Envié BUSCAR a 255.255.255.255:" + PORT);

            byte[] buf = new byte[256];
            DatagramPacket resp = new DatagramPacket(buf, buf.length);

            long end = System.currentTimeMillis() + 5000;
            while (System.currentTimeMillis() < end) {
                try {
                    s.receive(resp);
                    String msg = new String(resp.getData(), 0, resp.getLength()).trim();
                    System.out.println("[CLIENTE] Recibí '" + msg + "' desde " + resp.getAddress().getHostAddress() + ":" + resp.getPort());
                    if (msg.equals("ENCONTRAR")) return resp.getAddress();
                } catch (SocketTimeoutException ignored) {}
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (s != null) s.close();
        }
    }

    public void conectarA(InetAddress serverIp) throws SocketException {
        this.ipServer = serverIp;
        this.socket = new DatagramSocket(); // puerto random
        this.socket.setSoTimeout(1000);
        this.socket.setBroadcast(true);

        System.out.println("[CLIENTE] Conectando a " + ipServer.getHostAddress() + ":" + puertoServer);
        enviar("Conectar");

        start(); // empieza a escuchar
    }

    @Override
    public void run() {
        while (!fin) {
            try {
                byte[] buf = new byte[1024];
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                socket.receive(dp);

                String msg = new String(dp.getData(), 0, dp.getLength()).trim();
                if (!msg.equals("PONG")) System.out.println("[CLIENTE] <- " + msg);

                if (msg.startsWith("EQUIPO:")) {
                    int eq = Integer.parseInt(msg.substring("EQUIPO:".length()).trim());
                    if (listener != null) listener.onEquipoAsignado(eq);
                    continue;
                }

                if (msg.equals("START")) {
                    if (listener != null) listener.onStart();
                    continue;
                }

                if (msg.equals("ESPERANDO_RIVAL")) {
                    if (listener != null) listener.onEsperando();
                    continue;
                }

                if (msg.startsWith("RIVAL_SPAWN:")) {
                    String t = msg.substring("RIVAL_SPAWN:".length()).trim();
                    try {
                        TipoUnidad tipo = TipoUnidad.valueOf(t);

                        if (listener != null) listener.onRivalSpawn(tipo);

                        // ✅ Hook extra (para que la pantalla de juego lo aplique)
                        if (rivalSpawnHook != null) rivalSpawnHook.accept(tipo);

                    } catch (Exception ignored) {}
                    continue;
                }

                if (msg.startsWith("ERROR")) {
                    if (listener != null) listener.onError(msg);
                }

            } catch (SocketTimeoutException ignored) {
                // normal
            } catch (IOException e) {
                if (!fin) e.printStackTrace();
            }
        }
    }

    public void enviarSpawn(TipoUnidad tipo) {
        enviar("SPAWN:" + tipo.name());
    }

    public void enviar(String msg) {
        if (socket == null || ipServer == null) return;
        try {
            byte[] data = msg.getBytes();
            DatagramPacket out = new DatagramPacket(data, data.length, ipServer, puertoServer);
            socket.send(out);
            System.out.println("[CLIENTE] -> " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cerrar() {
        fin = true;
        if (socket != null) socket.close();
        interrupt();
    }

    public void setRivalSpawnHook(java.util.function.Consumer<TipoUnidad> hook) {
        this.rivalSpawnHook = hook;
    }
}
