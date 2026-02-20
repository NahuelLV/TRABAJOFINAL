package pro.juego.Ironfall.network;

import pro.juego.Ironfall.enums.TipoUnidad;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;

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

    private volatile int equipoAsignado = -1;

    private volatile BiConsumer<Integer, TipoUnidad> spawnHook = null;
    private volatile BiConsumer<Integer, Integer> oroHook = null;
    private volatile IntConsumer gameOverHook = null;

    private final Listener listener;
    private volatile boolean fin = false;

    // ✅ heartbeat
    private long lastPingMs = 0;
    private static final long PING_INTERVAL_MS = 1000;

    public ClienteOnline(Listener listener) {
        this.listener = listener;
        setName("ClienteOnlineThread");
        setDaemon(true);
    }

    public int getEquipoAsignado() {
        return equipoAsignado;
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

        this.socket = new DatagramSocket(); // puerto random local
        this.socket.setSoTimeout(250);      // ✅ más corto, para que el loop haga heartbeat fluido
        this.socket.setBroadcast(true);

        System.out.println("[CLIENTE] Conectando a " + ipServer.getHostAddress() + ":" + puertoServer);
        enviar("Conectar");

        lastPingMs = System.currentTimeMillis();
        start();
    }

    @Override
    public void run() {
        while (!fin) {
            try {
                if (socket == null || socket.isClosed()) break;

                // ✅ heartbeat: manda PING cada 1s aunque no haya tráfico
                long now = System.currentTimeMillis();
                if (now - lastPingMs >= PING_INTERVAL_MS) {
                    enviar("PING");
                    lastPingMs = now;
                }

                byte[] buf = new byte[1024];
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                socket.receive(dp);

                String msg = new String(dp.getData(), 0, dp.getLength()).trim();
                if (!msg.equals("PONG")) System.out.println("[CLIENTE] <- " + msg);

                if (msg.startsWith("EQUIPO:")) {
                    int eq = Integer.parseInt(msg.substring("EQUIPO:".length()).trim());
                    equipoAsignado = eq;
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

                if (msg.startsWith("GAME_OVER:")) {
                    try {
                        int ganador = Integer.parseInt(msg.substring("GAME_OVER:".length()).trim());
                        if (gameOverHook != null) gameOverHook.accept(ganador);
                    } catch (Exception e) {
                        if (listener != null) listener.onError("GAME_OVER malformado: " + msg);
                    }
                    continue;
                }

                if (msg.startsWith("SPAWN_OK:")) {
                    String rest = msg.substring("SPAWN_OK:".length()).trim();
                    String[] parts = rest.split(":");
                    if (parts.length >= 2) {
                        int eq = Integer.parseInt(parts[0].trim());
                        TipoUnidad tipo = TipoUnidad.valueOf(parts[1].trim());
                        if (spawnHook != null) spawnHook.accept(eq, tipo);
                    } else {
                        if (listener != null) listener.onError("SPAWN_OK malformado: " + msg);
                    }
                    continue;
                }

                if (msg.startsWith("SPAWN_FAIL")) {
                    if (listener != null) listener.onError(msg);
                    continue;
                }

                if (msg.startsWith("ORO:")) {
                    String[] parts = msg.split(":");
                    if (parts.length >= 3) {
                        int eq = Integer.parseInt(parts[1].trim());
                        int oro = Integer.parseInt(parts[2].trim());
                        if (oroHook != null) oroHook.accept(eq, oro);
                    } else {
                        if (listener != null) listener.onError("ORO malformado: " + msg);
                    }
                    continue;
                }

                if (msg.startsWith("RIVAL_SPAWN:")) {
                    String t = msg.substring("RIVAL_SPAWN:".length()).trim();
                    try {
                        TipoUnidad tipo = TipoUnidad.valueOf(t);
                        if (listener != null) listener.onRivalSpawn(tipo);
                    } catch (Exception ignored) {}
                    continue;
                }

                if (msg.startsWith("ERROR")) {
                    if (listener != null) listener.onError(msg);
                }

            } catch (SocketTimeoutException ignored) {
                // normal: se usa para poder hacer heartbeat
            } catch (IOException e) {
                if (fin) break;
                e.printStackTrace();
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
            // System.out.println("[CLIENTE] -> " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cerrar() {
        try { enviar("DISCONNECT"); } catch (Exception ignored) {}
        fin = true;
        if (socket != null && !socket.isClosed()) socket.close();
        interrupt();
    }

    public void setOroHook(BiConsumer<Integer, Integer> hook) { this.oroHook = hook; }
    public void setSpawnHook(BiConsumer<Integer, TipoUnidad> hook) { this.spawnHook = hook; }
    public void setGameOverHook(IntConsumer hook) { this.gameOverHook = hook; }
}