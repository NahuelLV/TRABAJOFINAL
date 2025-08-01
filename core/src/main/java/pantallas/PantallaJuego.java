// PantallaJuego.java
package pantallas;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import pro.juego.Ironfall.Ironfalljuego;
import pro.juego.Ironfall.entidades.Espadachin;
import pro.juego.Ironfall.entidades.Estatua;
import pro.juego.Ironfall.entidades.Unidad;
import pro.juego.Ironfall.pathfinding.Nodo;
import pro.juego.Ironfall.pathfinding.Pathfinding;

public class PantallaJuego implements Screen, InputProcessor {

    private final Ironfalljuego juego;
    private SpriteBatch batch;
    private OrthographicCamera camara;
    private ShapeRenderer shapeRenderer;
    private Array<Unidad> aliados;
    private Array<Unidad> enemigos;
    private Estatua estatuaJugador;
    private Estatua estatuaEnemiga;
    private Texture fondo;
    private boolean seleccionando = false;
    private Vector2 inicioSeleccion;
    private Vector2 finSeleccion;
    private final int ANCHO_DEL_MAPA = 3840;
    private final int ALTO_DEL_MAPA = 720;
    private final int ANCHO_CELDAS = 96;
    private final int ALTO_CELDAS = 18;
    private int[][] mapa = new int[ANCHO_CELDAS][ALTO_CELDAS];
    private Nodo[][] grillaNodos;
    private Pathfinding pathfinding;

    private float tiempoDesdeUltimaUnidadEnemiga = 0;
    private float intervaloGeneracionEnemiga = 10f;

    public PantallaJuego(Ironfalljuego juego) {
        this.juego = juego;
        this.batch = juego.batch;
    }

    private void inicializarGrilla() {
        grillaNodos = new Nodo[mapa.length][mapa[0].length];
        for (int x = 0; x < mapa.length; x++) {
            for (int y = 0; y < mapa[0].length; y++) {
                grillaNodos[x][y] = new Nodo(x, y, mapa[x][y] == 0);
            }
        }
        pathfinding = new Pathfinding(grillaNodos);
    }

    private Nodo convertirAPosicionCelda(Vector2 posicion) {
        int x = (int) (posicion.x / 40);
        int y = (int) (posicion.y / 40);
        if (x >= 0 && x < grillaNodos.length && y >= 0 && y < grillaNodos[0].length) {
            return grillaNodos[x][y];
        }
        return null;
    }

    @Override
    public void show() {
        camara = new OrthographicCamera();
        camara.setToOrtho(false, ANCHO_DEL_MAPA, ALTO_DEL_MAPA);
        camara.position.set(640, 360, 0);
        camara.update();
        shapeRenderer = new ShapeRenderer();

        aliados = new Array<>();
        enemigos = new Array<>();

        for (int i = 0; i < 5; i++) {
            aliados.add(new Espadachin(1100 + i * 100, 200, ANCHO_DEL_MAPA, 0));
        }

        for (int i = 0; i < 3; i++) {
            enemigos.add(new Espadachin(ANCHO_DEL_MAPA - 300 - i * 100, 200, ANCHO_DEL_MAPA, 1));
        }

        estatuaJugador = new Estatua("estatua1.png", 100, 100);
        estatuaEnemiga = new Estatua("estatua2.png", ANCHO_DEL_MAPA - 200, 100);

        fondo = new Texture("fondo.png");
        fondo.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        Gdx.input.setInputProcessor(this);
        inicializarGrilla();
    }

    @Override
    public void render(float delta) {
        actualizar(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camara.update();
        batch.setProjectionMatrix(camara.combined);
        batch.begin();

        int fondoAncho = fondo.getWidth();
        int cantidadTiles = (int) Math.ceil(ANCHO_DEL_MAPA / (float) fondoAncho);
        for (int i = 0; i < cantidadTiles; i++) {
            batch.draw(fondo, i * fondoAncho, -200, fondoAncho, fondo.getHeight());
        }

        for (Unidad u : aliados) u.render(batch);
        for (Unidad e : enemigos) e.render(batch);

        estatuaJugador.render(batch);
        estatuaEnemiga.render(batch);

        batch.end();

        shapeRenderer.setProjectionMatrix(camara.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        estatuaJugador.renderBarraVida(shapeRenderer);
        estatuaEnemiga.renderBarraVida(shapeRenderer);
        shapeRenderer.end();

        if (seleccionando) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.GREEN);
            Rectangle rect = crearRectanguloSeleccion();
            shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
            shapeRenderer.end();
        }

        if (estatuaJugador.estaDestruida()) {
            System.out.println("¡DERROTA!");
        }
    }

    private void actualizar(float delta) {
        float velocidad = 300;
        int mouseX = Gdx.input.getX();
        int borde = 20;

        if (mouseX < borde) camara.position.x -= velocidad * delta;
        if (mouseX > Gdx.graphics.getWidth() - borde) camara.position.x += velocidad * delta;

        camara.position.x = MathUtils.clamp(camara.position.x, camara.viewportWidth / 2, ANCHO_DEL_MAPA - camara.viewportWidth / 2);

        List<Unidad> enemigosLista = new ArrayList<>();
        for (Unidad u : enemigos) {
            enemigosLista.add(u);
        }

        List<Unidad> aliadosLista = new ArrayList<>();
        for (Unidad u : aliados) {
            aliadosLista.add(u);
        }

        for (Unidad aliada : aliados) {
            aliada.update(delta, enemigosLista, estatuaEnemiga);
        }

        for (Unidad enemiga : enemigos) {
            enemiga.update(delta, aliadosLista, estatuaJugador);
        }

        // IA: crear unidad enemiga automática
        tiempoDesdeUltimaUnidadEnemiga += delta;
        if (tiempoDesdeUltimaUnidadEnemiga >= intervaloGeneracionEnemiga) {
            tiempoDesdeUltimaUnidadEnemiga = 0;
            Espadachin nuevo = new Espadachin(ANCHO_DEL_MAPA - 100, 200, ANCHO_DEL_MAPA, 1);
            enemigos.add(nuevo);
            Nodo inicio = convertirAPosicionCelda(nuevo.getCentro());
            Nodo fin = convertirAPosicionCelda(new Vector2(estatuaJugador.getX(), estatuaJugador.getY()));
            if (inicio != null && fin != null) {
                List<Nodo> camino = pathfinding.suavizarCamino(pathfinding.encontrarCamino(inicio, fin));
                nuevo.setPath(camino);
            }
        }
    }

    private Rectangle crearRectanguloSeleccion() {
        float x = Math.min(inicioSeleccion.x, finSeleccion.x);
        float y = Math.min(inicioSeleccion.y, finSeleccion.y);
        float an = Math.abs(inicioSeleccion.x - finSeleccion.x);
        float al = Math.abs(inicioSeleccion.y - finSeleccion.y);
        return new Rectangle(x, y, an, al);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 clic = camara.unproject(new Vector3(screenX, screenY, 0));
        Vector2 clicMundo = new Vector2(clic.x, clic.y);
        float espacio = 40f;

        if (button == Input.Buttons.LEFT) {
            boolean clickeoUnidad = false;
            for (Unidad u : aliados) {
                if (u.fueClickeado(clicMundo.x, clicMundo.y)) {
                    for (Unidad otra : aliados) otra.setSeleccionada(false);
                    u.setSeleccionada(true);
                    clickeoUnidad = true;
                    break;
                }
            }
            if (!clickeoUnidad) {
                seleccionando = true;
                inicioSeleccion = clicMundo.cpy();
                finSeleccion = clicMundo.cpy();
            }
        } else if (button == Input.Buttons.RIGHT) {
            Array<Unidad> seleccionadas = new Array<>();
            for (Unidad u : aliados) if (u.estaSeleccionada()) seleccionadas.add(u);

            boolean clickeoEnemigo = false;
            for (Unidad enemigo : enemigos) {
                if (enemigo.fueClickeado(clicMundo.x, clicMundo.y)) {
                    for (Unidad aliada : seleccionadas) {
                        aliada.setObjetivo(enemigo);
                    }
                    clickeoEnemigo = true;
                    break;
                }
            }

            if (!clickeoEnemigo && seleccionadas.size > 0) {
                for (int i = 0; i < seleccionadas.size; i++) {
                    float offsetX = 0;
                    float offsetY = (i - seleccionadas.size / 2f) * espacio;
                    Vector2 destino = new Vector2(clicMundo.x + offsetX, clicMundo.y + offsetY);
                    Unidad unidad = seleccionadas.get(i);
                    Nodo inicio = convertirAPosicionCelda(unidad.getCentro());
                    Nodo fin = convertirAPosicionCelda(destino);
                    if (inicio != null && fin != null) {
                        List<Nodo> crudo = pathfinding.encontrarCamino(inicio, fin);
                        List<Nodo> suavizado = pathfinding.suavizarCamino(crudo);
                        unidad.setPath(suavizado);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT && seleccionando) {
            seleccionando = false;
            Rectangle rect = crearRectanguloSeleccion();
            for (Unidad u : aliados) {
                u.setSeleccionada(rect.contains(u.getCentro()));
            }
        }
        return true;
    }

    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }

    @Override public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (seleccionando) {
            Vector3 drag = camara.unproject(new Vector3(screenX, screenY, 0));
            finSeleccion.set(drag.x, drag.y);
        }
        return true;
    }

    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }

    @Override public void resize(int width, int height) {
        camara.viewportWidth = width;
        camara.viewportHeight = height;
        camara.update();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        estatuaJugador.dispose();
        estatuaEnemiga.dispose();
    }
}
