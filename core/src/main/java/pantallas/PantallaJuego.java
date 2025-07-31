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
import pro.juego.Ironfall.entidades.Estatua;
import pro.juego.Ironfall.entidades.Unidad;
import pro.juego.Ironfall.pathfinding.Nodo;
import pro.juego.Ironfall.pathfinding.Pathfinding;

public class PantallaJuego implements Screen, InputProcessor {

    private final Ironfalljuego juego;
    private SpriteBatch batch;
    private OrthographicCamera camara;
    private ShapeRenderer shapeRenderer;
    private Array<Unidad> unidades;
    private Estatua estatuaJugador;
    private Texture fondo;
    private boolean seleccionando = false;
    private Vector2 inicioSeleccion;
    private Vector2 finSeleccion;
    private final int ANCHO_DEL_MAPA = 3840;
    private final int ALTO_DEL_MAPA = 720;
    private final int ANCHO_CELDAS = 96; // 3840 / 40
    private final int ALTO_CELDAS = 18;  // 720 / 40
    private int[][] mapa = new int[ANCHO_CELDAS][ALTO_CELDAS];
    private Nodo[][] grillaNodos;
    private Pathfinding pathfinding;
    
    public PantallaJuego(Ironfalljuego juego) {
        this.juego = juego;
        this.batch = juego.batch;
    }

    private void inicializarGrilla() {
        int ancho = mapa.length;
        int alto = mapa[0].length;
        grillaNodos = new Nodo[ancho][alto];

        for (int x = 0; x < ancho; x++) {
            for (int y = 0; y < alto; y++) {
                boolean accesible = (mapa[x][y] == 0);
                grillaNodos[x][y] = new Nodo(x, y, accesible);
            }
        }

        pathfinding = new Pathfinding(grillaNodos);
    }
    
    private Nodo convertirAPosicionCelda(Vector2 posicion) {
        int x = (int)(posicion.x / 40); // 40 = tamaño de celda
        int y = (int)(posicion.y / 40);
        if (x >= 0 && x < grillaNodos.length && y >= 0 && y < grillaNodos[0].length) {
            return grillaNodos[x][y];
        }
        return null;
    }
    
    @Override
    public void show() {
        camara = new OrthographicCamera();
        camara.setToOrtho(false, ANCHO_DEL_MAPA, ALTO_DEL_MAPA);
        camara.position.set(640,360,0);
        camara.update();
        shapeRenderer = new ShapeRenderer();

        unidades = new Array<>();
        for (int i = 0; i < 5; i++) {
            unidades.add(new Unidad("jugador.png", 1100 + i * 100, 200, ANCHO_DEL_MAPA));
        }

        Gdx.input.setInputProcessor(this);
        estatuaJugador = new Estatua("estatua1.png", 100, 100);
        
        fondo = new Texture("fondo.png");
        fondo.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        
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

        // Fondo en mosaico
        int fondoAncho = fondo.getWidth();
        int fondoAlto = fondo.getHeight();
        int cantidadTiles = (int) Math.ceil(ANCHO_DEL_MAPA / (float) fondoAncho);
        for (int i = 0; i < cantidadTiles; i++) {
            batch.draw(fondo, i * fondoAncho, -200, fondoAncho, fondoAlto);
        }

        // Unidades
        for (Unidad u : unidades) {
            u.render(batch);
        }

        // Estatua del jugador
        estatuaJugador.render(batch);

        batch.end();

        // Barra de vida de la estatua
        shapeRenderer.setProjectionMatrix(camara.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        estatuaJugador.renderBarraVida(shapeRenderer);
        shapeRenderer.end();

        // Rectángulo de selección si corresponde
        if (seleccionando) {
            shapeRenderer.setProjectionMatrix(camara.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.GREEN);
            Rectangle rect = crearRectanguloSeleccion();
            shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
            shapeRenderer.end();
        }

        // Estado de derrota
        if (estatuaJugador.estaDestruida()) {
            System.out.println("¡DERROTA! La estatua fue destruida.");
            // Aquí podrías cambiar a pantalla de derrota en el futuro
        }
    }

    private void actualizar(float delta) {
        int mouseX = Gdx.input.getX();
        int borde = 20;
        float velocidad = 300;
        List<Unidad> listaUnidades = new ArrayList<>();
        for (Unidad u : unidades) {
            u.update(delta, listaUnidades);
            listaUnidades.add(u);
        }
    	if (mouseX < borde) {
    	    camara.position.x -= velocidad * delta;
    	    camara.update();
    	}
    	
    	if (mouseX > Gdx.graphics.getWidth() - borde) {
    	    camara.position.x += velocidad * delta;
    	    camara.update();
    	}
        
    	camara.position.x = MathUtils.clamp(camara.position.x, camara.viewportWidth / 2, ANCHO_DEL_MAPA - camara.viewportWidth / 2);
    	//MathUtils.clamp(el rango donde no cambia nada, si el valor es menor al minimo te devuelve el numero minimo establecido, lo mismo pero con el maximo);
    	
    	
    	
    	
    }

    private Rectangle crearRectanguloSeleccion() {
        float x = Math.min(inicioSeleccion.x, finSeleccion.x);
        float y = Math.min(inicioSeleccion.y, finSeleccion.y);
        float an = Math.abs(inicioSeleccion.x - finSeleccion.x);
        float al = Math.abs(inicioSeleccion.y - finSeleccion.y);
        return new Rectangle(x, y, an, al);
    }

    public int getAnchoDelMapa() {
    	return this.ANCHO_DEL_MAPA;
    } 
    
    public int getAltoDelMapa() {
    	return this.ALTO_DEL_MAPA;
    }
    
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 clic = camara.unproject(new Vector3(screenX, screenY, 0));
        Vector2 clicMundo = new Vector2(clic.x, clic.y);
        float espacio = 40f;

        if (button == Input.Buttons.LEFT) {
            
            boolean clickeoUnidad = false;
            for (Unidad u : unidades) {
                if (u.fueClickeado(clicMundo.x, clicMundo.y)) {
                    // Deselecciona todas
                    for (Unidad otra : unidades) {
                        otra.setSeleccionada(false);
                    }
                    // Selecciona solo esta
                    u.setSeleccionada(true);
                    clickeoUnidad = true;
                    break;
                }
            }

            //  Si no tocó ninguna unidad, inicia selección múltiple
            if (!clickeoUnidad) {
                seleccionando = true;
                inicioSeleccion = clicMundo.cpy();
                finSeleccion = clicMundo.cpy();
            }

        } else if (button == Input.Buttons.RIGHT) {
            Array<Unidad> seleccionadas = new Array<>();
            for (Unidad u : unidades) {
                if (u.estaSeleccionada()) {
                    seleccionadas.add(u);
                }
            }

            if (seleccionadas.size > 0) {
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

            for (Unidad u : unidades) {
                if (rect.contains(u.getCentro())) {
                    u.setSeleccionada(true);
                } else {
                    u.setSeleccionada(false);
                }
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
    @Override public void dispose() {
        shapeRenderer.dispose();
    }
}
