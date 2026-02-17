package pro.juego.Ironfall.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import pro.juego.Ironfall.IronfallJuego;
import pro.juego.Ironfall.entidades.Espadachin;
import pro.juego.Ironfall.entidades.Estatua;
import pro.juego.Ironfall.entidades.Unidad;
import pro.juego.Ironfall.enums.TipoUnidad;
import pro.juego.Ironfall.hud.HudJuego;
import pro.juego.Ironfall.enums.ModoJuego;
import pro.juego.Ironfall.entidades.IAEnemiga;

public class PantallaJuego implements Screen {

    private static final float VELOCIDAD_CAMARA = 600f;
    private static final int BORDE_CAMARA = 40;

    private static final int ANCHO_MUNDO = 3000;
    private static final int ALTO_MUNDO = 600;

    private IronfallJuego game;
    private SpriteBatch batch;

    private HudJuego hudJugador1;
    private HudJuego hudJugador2;

    private ShapeRenderer shapeRenderer;

    private OrthographicCamera camara;
    private Viewport viewport;

    private Array<Unidad> unidadesJugador;
    private Array<Unidad> unidadesEnemigas;

    private Estatua estatuaJugador;
    private Estatua estatuaEnemiga;

    private ModoJuego modo;
    private IAEnemiga iaEnemiga;

    float yEstatua = 220f;

    public PantallaJuego(IronfallJuego game, ModoJuego modo) {
        this.game = game;
        this.batch = game.batch;
        this.modo = modo;
    }

    @Override
    public void show() {

        shapeRenderer = new ShapeRenderer();

        camara = new OrthographicCamera();
        viewport = new FitViewport(800, 600, camara);

        camara.position.set(400, 300, 0);
        camara.update();

        unidadesJugador = new Array<>();
        unidadesEnemigas = new Array<>();

        estatuaJugador = new Estatua(150f, yEstatua, 0);
        estatuaEnemiga = new Estatua(2850f, yEstatua, 1);

        if (modo == ModoJuego.UN_JUGADOR) {
            iaEnemiga = new IAEnemiga(estatuaEnemiga);
        }

        hudJugador1 = new HudJuego(estatuaJugador, true, false);

        if (modo == ModoJuego.DOS_JUGADORES_LOCAL) {
            hudJugador2 = new HudJuego(estatuaEnemiga, false, true);
        }

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(hudJugador1.getStage());
        Gdx.input.setInputProcessor(multiplexer);

        unidadesJugador.add(new Espadachin(120f, 1, 0));
        unidadesEnemigas.add(new Espadachin(2830f, 1, 1));
    }

    private void updateCamara(float delta) {

        float mouseX = Gdx.input.getX();
        float anchoPantalla = Gdx.graphics.getWidth();

        if (mouseX <= BORDE_CAMARA)
            camara.position.x -= VELOCIDAD_CAMARA * delta;

        if (mouseX >= anchoPantalla - BORDE_CAMARA)
            camara.position.x += VELOCIDAD_CAMARA * delta;

        float mitadVista = camara.viewportWidth / 2f;

        camara.position.x = Math.max(
                mitadVista,
                Math.min(ANCHO_MUNDO - mitadVista, camara.position.x)
        );

        camara.update();
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // INPUT JUGADOR 2
        if (modo == ModoJuego.DOS_JUGADORES_LOCAL) {

            if (Gdx.input.isKeyJustPressed(Input.Keys.A))
                estatuaEnemiga.intentarProducir(TipoUnidad.ESPADACHIN);

            if (Gdx.input.isKeyJustPressed(Input.Keys.S))
                estatuaEnemiga.intentarProducir(TipoUnidad.BESTIA);

            if (Gdx.input.isKeyJustPressed(Input.Keys.D))
                estatuaEnemiga.intentarProducir(TipoUnidad.ARQUERO);
        }

        // UPDATE ESTATUAS
        estatuaJugador.update(delta, unidadesEnemigas);
        estatuaEnemiga.update(delta, unidadesJugador);

        if (modo == ModoJuego.UN_JUGADOR && iaEnemiga != null)
            iaEnemiga.update(delta);

        // PRODUCCIÓN
        Unidad nueva;

        nueva = estatuaJugador.updateProduccion(delta);
        if (nueva != null) unidadesJugador.add(nueva);

        nueva = estatuaEnemiga.updateProduccion(delta);
        if (nueva != null) unidadesEnemigas.add(nueva);

        // UPDATE UNIDADES (SIN FOR-EACH)
        for (int i = 0; i < unidadesJugador.size; i++) {
            Unidad u = unidadesJugador.get(i);
            u.update(delta, unidadesEnemigas, unidadesJugador, estatuaEnemiga);
        }

        for (int i = 0; i < unidadesEnemigas.size; i++) {
            Unidad u = unidadesEnemigas.get(i);
            u.update(delta, unidadesJugador, unidadesEnemigas, estatuaJugador);
        }

        // DAÑO A ESTATUAS
        for (int i = 0; i < unidadesJugador.size; i++) {
            Unidad u = unidadesJugador.get(i);
            if (u.estaViva() && Math.abs(u.getX() - estatuaEnemiga.getX()) < 30f)
                estatuaEnemiga.recibirDanio(20f * delta);
        }

        for (int i = 0; i < unidadesEnemigas.size; i++) {
            Unidad u = unidadesEnemigas.get(i);
            if (u.estaViva() && Math.abs(u.getX() - estatuaJugador.getX()) < 30f)
                estatuaJugador.recibirDanio(20f * delta);
        }

        // LIMPIAR MUERTOS
        for (int i = unidadesJugador.size - 1; i >= 0; i--) {
            if (!unidadesJugador.get(i).estaViva())
                unidadesJugador.removeIndex(i);
        }

        for (int i = unidadesEnemigas.size - 1; i >= 0; i--) {
            if (!unidadesEnemigas.get(i).estaViva())
                unidadesEnemigas.removeIndex(i);
        }

        // RENDER MUNDO
        updateCamara(delta);
        batch.setProjectionMatrix(camara.combined);

        batch.begin();

        estatuaJugador.render(batch);
        estatuaEnemiga.render(batch);

        for (int i = 0; i < unidadesJugador.size; i++)
            unidadesJugador.get(i).render(batch);

        for (int i = 0; i < unidadesEnemigas.size; i++)
            unidadesEnemigas.get(i).render(batch);

        batch.end();

        // BARRAS VIDA
        shapeRenderer.setProjectionMatrix(camara.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        estatuaJugador.renderBarraVida(shapeRenderer);
        estatuaEnemiga.renderBarraVida(shapeRenderer);
        shapeRenderer.end();

        // HUD
        hudJugador1.update(delta);
        hudJugador1.render();

        if (modo == ModoJuego.DOS_JUGADORES_LOCAL && hudJugador2 != null) {
            hudJugador2.update(delta);
            hudJugador2.render();
        }

        // FIN PARTIDA
        if (!estatuaEnemiga.estaViva())
            game.setScreen(new PantallaVictoria(game));

        if (!estatuaJugador.estaViva())
            game.setScreen(new PantallaDerrota(game));
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        hudJugador1.resize(width, height);
        if (hudJugador2 != null)
            hudJugador2.resize(width, height);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        hudJugador1.dispose();
        if (hudJugador2 != null)
            hudJugador2.dispose();
        shapeRenderer.dispose();
    }
}
