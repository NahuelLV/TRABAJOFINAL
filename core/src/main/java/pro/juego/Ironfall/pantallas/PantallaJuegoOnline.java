package pro.juego.Ironfall.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.concurrent.ConcurrentLinkedQueue;

import pro.juego.Ironfall.IronfallJuego;
import pro.juego.Ironfall.entidades.Estatua;
import pro.juego.Ironfall.entidades.Unidad;
import pro.juego.Ironfall.enums.TipoUnidad;
import pro.juego.Ironfall.hud.HudJuego;
import pro.juego.Ironfall.hud.HudOnline;
import pro.juego.Ironfall.network.ClienteOnline;

public class PantallaJuegoOnline implements Screen {

    private static final float VELOCIDAD_CAMARA = 600f;
    private static final int BORDE_CAMARA = 40;

    private static final int ANCHO_MUNDO = 3000;
    private static final int ALTO_MUNDO = 600;

    private final IronfallJuego game;
    private final SpriteBatch batch;

    private final ClienteOnline cliente;
    private final int equipoLocal; // 0 o 1

    private HudOnline hudLocal;
    private HudJuego hudRemotoVisual;

    private ShapeRenderer shapeRenderer;

    private OrthographicCamera camara;
    private Viewport viewport;

    private Array<Unidad> unidadesEquipo0;
    private Array<Unidad> unidadesEquipo1;

    private Estatua estatua0;
    private Estatua estatua1;

    private Texture fondo;

    private static class SpawnEvt {
        int equipo;
        TipoUnidad tipo;
        SpawnEvt(int equipo, TipoUnidad tipo) { this.equipo = equipo; this.tipo = tipo; }
    }
    private final ConcurrentLinkedQueue<SpawnEvt> spawnsPendientes = new ConcurrentLinkedQueue<>();

    // ✅ ganador pendiente (lo setea el hook)
    private volatile Integer ganadorPendiente = null;

    public PantallaJuegoOnline(IronfallJuego game, ClienteOnline cliente, int equipoLocal) {
        this.game = game;
        this.batch = game.batch;
        this.cliente = cliente;
        this.equipoLocal = equipoLocal;
    }

    @Override
    public void show() {

        shapeRenderer = new ShapeRenderer();

        camara = new OrthographicCamera();
        viewport = new FitViewport(800, 600, camara);

        if (equipoLocal == 0) camara.position.set(400, 300, 0);
        else camara.position.set(2600, 300, 0);
        camara.update();

        fondo = new Texture("ui/fondo.png");

        unidadesEquipo0 = new Array<>();
        unidadesEquipo1 = new Array<>();

        float yEstatua = 75f;
        estatua0 = new Estatua(150f, yEstatua, 0);
        estatua1 = new Estatua(2850f, yEstatua, 1);

        // ONLINE: oro lo manda server
        estatua0.setGenerarOroHabilitado(false);
        estatua1.setGenerarOroHabilitado(false);

        // ONLINE: spawn determinístico (si lo tenés implementado)
        estatua0.setSpawnAleatorio(false);
        estatua1.setSpawnAleatorio(false);

        Estatua estatuaLocal = (equipoLocal == 0) ? estatua0 : estatua1;
        Estatua estatuaRival = (equipoLocal == 0) ? estatua1 : estatua0;

        hudLocal = new HudOnline(
                estatuaLocal,
                equipoLocal == 1,
                tipo -> cliente.enviarSpawn(tipo)
        );

        hudRemotoVisual = new HudJuego(estatuaRival, false, equipoLocal == 0);

        // Spawn server -> ambos
        cliente.setSpawnHook((eq, tipo) -> Gdx.app.postRunnable(() -> {
            spawnsPendientes.add(new SpawnEvt(eq, tipo));
            System.out.println("[ONLINE] Spawn recibido: eq=" + eq + " tipo=" + tipo);
        }));

        // Oro server -> ambos
        cliente.setOroHook((eq, oro) -> Gdx.app.postRunnable(() -> {
            if (eq == 0) estatua0.setOro(oro);
            else estatua1.setOro(oro);
        }));

        // ✅ FIN PARTIDA (server manda ganador)
        cliente.setGameOverHook(ganador -> Gdx.app.postRunnable(() -> {
            if (ganadorPendiente == null) {
                ganadorPendiente = ganador;
                System.out.println("[ONLINE] GAME_OVER recibido. Ganador=" + ganador);
            }
        }));

        InputMultiplexer mux = new InputMultiplexer();
        mux.addProcessor(hudLocal.getStage());
        Gdx.input.setInputProcessor(mux);

        System.out.println("[ONLINE] PantallaJuegoOnline iniciada. EquipoLocal=" + equipoLocal);
    }

    private void updateCamara(float delta) {
        float mouseX = Gdx.input.getX();
        float anchoPantalla = Gdx.graphics.getWidth();

        if (mouseX <= BORDE_CAMARA) camara.position.x -= VELOCIDAD_CAMARA * delta;
        if (mouseX >= anchoPantalla - BORDE_CAMARA) camara.position.x += VELOCIDAD_CAMARA * delta;

        float mitadVista = camara.viewportWidth / 2f;
        camara.position.x = Math.max(
                mitadVista,
                Math.min(ANCHO_MUNDO - mitadVista, camara.position.x)
        );

        camara.update();
    }

    @Override
    public void render(float delta) {

        // ✅ si ya terminó, cambiamos pantalla y listo (no más sim)
        if (ganadorPendiente != null) {
            int ganador = ganadorPendiente;
            ganadorPendiente = null;

            if (ganador == equipoLocal) game.setScreen(new PantallaVictoria(game));
            else game.setScreen(new PantallaDerrota(game));
            return;
        }

        // 1) aplicar spawns confirmados por server (NO cobrar oro)
        SpawnEvt evt;
        while ((evt = spawnsPendientes.poll()) != null) {
            Estatua est = (evt.equipo == 0) ? estatua0 : estatua1;
            est.encolarProduccionSinCosto(evt.tipo);
        }

        // 2) sim local
        estatua0.update(delta, unidadesEquipo1);
        estatua1.update(delta, unidadesEquipo0);

        Unidad nueva;

        nueva = estatua0.updateProduccion(delta);
        if (nueva != null) unidadesEquipo0.add(nueva);

        nueva = estatua1.updateProduccion(delta);
        if (nueva != null) unidadesEquipo1.add(nueva);

        for (int i = 0; i < unidadesEquipo0.size; i++)
            unidadesEquipo0.get(i).update(delta, unidadesEquipo1, unidadesEquipo0, estatua1);

        for (int i = 0; i < unidadesEquipo1.size; i++)
            unidadesEquipo1.get(i).update(delta, unidadesEquipo0, unidadesEquipo1, estatua0);

        for (int i = unidadesEquipo0.size - 1; i >= 0; i--)
            if (!unidadesEquipo0.get(i).estaViva()) unidadesEquipo0.removeIndex(i);

        for (int i = unidadesEquipo1.size - 1; i >= 0; i--)
            if (!unidadesEquipo1.get(i).estaViva()) unidadesEquipo1.removeIndex(i);

        // 3) render
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(fondo, 0, 0, ANCHO_MUNDO, ALTO_MUNDO);
        batch.end();

        updateCamara(delta);
        batch.setProjectionMatrix(camara.combined);

        batch.begin();
        estatua0.render(batch);
        estatua1.render(batch);
        for (int i = 0; i < unidadesEquipo0.size; i++) unidadesEquipo0.get(i).render(batch);
        for (int i = 0; i < unidadesEquipo1.size; i++) unidadesEquipo1.get(i).render(batch);
        batch.end();

        shapeRenderer.setProjectionMatrix(camara.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        estatua0.renderBarraVida(shapeRenderer);
        estatua1.renderBarraVida(shapeRenderer);
        shapeRenderer.end();

        hudLocal.update(delta);
        hudLocal.render();

        hudRemotoVisual.update(delta);
        hudRemotoVisual.render();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        hudLocal.resize(width, height);
        hudRemotoVisual.resize(width, height);
    }

    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void hide() {
        if (cliente != null) cliente.cerrar();
    }

    @Override
    public void dispose() {
        if (cliente != null) cliente.cerrar();
        hudLocal.dispose();
        hudRemotoVisual.dispose();
        shapeRenderer.dispose();
        fondo.dispose();
    }
}