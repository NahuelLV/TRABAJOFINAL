package pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import pro.juego.Ironfall.Ironfalljuego;

public class PantallaJuego implements Screen {

    private Ironfalljuego juego;
    private SpriteBatch batch;
    private Texture jugador;
    private Vector2 posicion;

    public PantallaJuego(Ironfalljuego juego) {
        this.juego = juego;
        this.batch = juego.batch;
    }

    @Override
    public void show() {
    	jugador = new Texture("jugador.png"); // poné un .png en esa carpeta
        posicion = new Vector2(100, 100); // Posición inicial del jugador
    }

    @Override
    public void render(float delta) {
        // Lógica de movimiento
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) {
            posicion.x += 200 * delta;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) {
            posicion.x -= 200 * delta;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.UP)) {
            posicion.y += 200 * delta;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.DOWN)) {
            posicion.y -= 200 * delta;
        }

        // Limpiar pantalla y dibujar
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(jugador, posicion.x, posicion.y);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        jugador.dispose();
    }
}