package pro.juego.Ironfall.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

import java.net.InetAddress;

import pro.juego.Ironfall.IronfallJuego;
import pro.juego.Ironfall.enums.TipoUnidad;
import pro.juego.Ironfall.network.ClienteOnline;

public class PantallaBuscandoOnline implements Screen {

    private final IronfallJuego game;

    private volatile boolean buscando = true;
    private volatile boolean yaCambiePantalla = false;

    private ClienteOnline cliente;
    private int equipo = -1;

    public PantallaBuscandoOnline(IronfallJuego game) {
        this.game = game;
    }

    @Override
    public void show() {
        System.out.println("[UI] Entré a PantallaBuscandoOnline");

        Thread t = new Thread(() -> {
            try {
                cliente = new ClienteOnline(new ClienteOnline.Listener() {

                    @Override
                    public void onEquipoAsignado(int eq) {
                        equipo = eq;
                        System.out.println("[UI] Me asignaron equipo " + eq);
                    }

                    @Override
                    public void onStart() {
                        System.out.println("[UI] START recibido, entrando a juego online");

                        if (!buscando || yaCambiePantalla) return;
                        yaCambiePantalla = true;

                        // ✅ SIEMPRE cambiar pantallas desde el hilo de LibGDX:
                        Gdx.app.postRunnable(() -> {
                            if (!buscando) return;
                            game.setScreen(new PantallaJuegoOnline(game, cliente, equipo));
                        });
                    }

                    @Override
                    public void onEsperando() {
                        System.out.println("[UI] Esperando rival...");
                    }

                    @Override
                    public void onRivalSpawn(TipoUnidad tipo) {
                        System.out.println("[UI] Rival spawneó " + tipo);
                    }

                    @Override
                    public void onError(String msg) {
                        System.out.println("[UI] Error: " + msg);
                    }
                });

                InetAddress ip = cliente.buscarServidor();
                if (!buscando) return;

                if (ip == null) {
                    System.out.println("[UI] No se encontró servidor.");
                    return;
                }

                System.out.println("[UI] Servidor encontrado en " + ip.getHostAddress());
                cliente.conectarA(ip);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "BuscarYConectarThread");

        t.setDaemon(true);
        t.start();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void hide() {
        buscando = false;
        System.out.println("[UI] Salí de PantallaBuscandoOnline");

        // OJO: si pasamos a PantallaJuegoOnline, NO cierres el cliente acá.
        // PantallaJuegoOnline lo va a cerrar cuando se salga del modo online.
        if (!yaCambiePantalla && cliente != null) cliente.cerrar();
    }

    @Override
    public void dispose() {
        buscando = false;
        if (!yaCambiePantalla && cliente != null) cliente.cerrar();
    }
}