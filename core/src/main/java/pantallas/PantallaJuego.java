package pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Texture;
import pro.juego.Ironfall.Ironfalljuego;
import pro.juego.Ironfall.entidades.Estatua; 
import pro.juego.Ironfall.entidades.Unidad;

public class PantallaJuego implements Screen, InputProcessor {

    
	private final Ironfalljuego juego;
    private SpriteBatch batch;
    private OrthographicCamera camara;
    private ShapeRenderer shapeRenderer;
    
    private Texture fondo;
   
    private Array<Unidad> unidades;
    private Estatua estatuaJugador;

    private boolean seleccionando = false;
    private Vector2 inicioSeleccion;
    private Vector2 finSeleccion;
    private final int ANCHO_DEL_MAPA = 3840;
    public PantallaJuego(Ironfalljuego juego) {
        this.juego = juego;
        this.batch = juego.batch;
    }

    @Override
    public void show() {
        camara = new OrthographicCamera();
        camara.setToOrtho(false, ANCHO_DEL_MAPA, 720);
        camara.position.set(640,360,0);
        camara.update();
        shapeRenderer = new ShapeRenderer();

        unidades = new Array<>();
        for (int i = 0; i < 5; i++) {
            unidades.add(new Unidad("Jugadorr.png", 1100 + i * 100, 200, ANCHO_DEL_MAPA));
        }

        Gdx.input.setInputProcessor(this);
     // Agregamos la estatua del jugador (por ahora usamos la imagen del jugador como placeholder) //Agregado Mati
        estatuaJugador = new Estatua("estatua1.png", 100, 100);


        //Fondo de pantalla
        fondo = new Texture("fondo.png");
        fondo.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

    }

    @Override
    public void render(float delta) {
        actualizar(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camara.combined);
        batch.begin();

        // Dibuja el fondo en mosaico para cubrir todo el mapa
        int fondoAncho = fondo.getWidth();
        int fondoAlto = fondo.getHeight();
        int cantidadTiles = (int) Math.ceil(ANCHO_DEL_MAPA / (float) fondoAncho);

        for (int i = 0; i < cantidadTiles; i++) {
            batch.draw(fondo, i * fondoAncho, -200, fondoAncho, fondoAlto);
        }


        
        
        
        
        camara.update();
        batch.setProjectionMatrix(camara.combined);

        //batch.begin();
        for (Unidad u : unidades) {
            u.render(batch);
        }
        
      //Agregado Mati
        estatuaJugador.render(batch); // Mostramos la estatua
        batch.end();

        shapeRenderer.setProjectionMatrix(camara.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

       

        // Barra de vida de la estatua //Agregado Mati
        estatuaJugador.renderBarraVida(shapeRenderer);
        shapeRenderer.end();

        if (seleccionando) {
            shapeRenderer.setProjectionMatrix(camara.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.GREEN);
            Rectangle rect = crearRectanguloSeleccion();
            shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
            shapeRenderer.end();
        }
        
        
        if (estatuaJugador.estaDestruida()) {
            System.out.println("¡DERROTA! La estatua fue destruida.");
            // Más adelante podés cambiar de pantalla o mostrar menú de derrota
        }

    }

    private void actualizar(float delta) {
        int mouseX = Gdx.input.getX();
        int borde = 20;
        float velocidad = 300;
    	for (Unidad u : unidades) {
            u.update(delta);
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
    
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 clic = camara.unproject(new Vector3(screenX, screenY, 0));
        Vector2 clicMundo = new Vector2(clic.x, clic.y);

        if (button == Input.Buttons.LEFT) {
            seleccionando = true;
            inicioSeleccion = clicMundo.cpy();
            finSeleccion = clicMundo.cpy();

        } else if (button == Input.Buttons.RIGHT) {
            Array<Unidad> seleccionadas = new Array<>();
            for (Unidad u : unidades) {
                if (u.estaSeleccionada()) {
                    seleccionadas.add(u);
                }
            }

            if (seleccionadas.size > 0) {
                float radio = 40f;
                float anguloPaso = 360f / seleccionadas.size;

                for (int i = 0; i < seleccionadas.size; i++) {
                    float angulo = i * anguloPaso;
                    float offsetX = (float)Math.cos(Math.toRadians(angulo)) * radio;
                    float offsetY = (float)Math.sin(Math.toRadians(angulo)) * radio;
                    Vector2 destino = new Vector2(clicMundo.x + offsetX, clicMundo.y + offsetY);
                    seleccionadas.get(i).setDestino(destino);
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

    
    
    
    @Override public boolean keyDown(int keycode) { return false;  }
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
        estatuaJugador.dispose(); //Agregado Mati
        fondo.dispose();


    }
}
