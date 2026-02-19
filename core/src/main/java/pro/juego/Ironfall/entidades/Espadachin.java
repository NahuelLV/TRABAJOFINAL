package pro.juego.Ironfall.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;

public class Espadachin extends Unidad {

    public Espadachin(float x, float y, int equipo) {

        // Le pasamos cualquier textura base al padre (no se usa para animaciones)
        super(new Texture("ui/unidades/espadachin-Idle.png"), x, y, equipo);

        vida = 100;
        danio = 15;
        velocidad = 60f;
        rangoAtaque = 30f;
        tiempoEntreAtaques = 1f;

        ancho = 48f;
        alto = 48f;

        cargarAnimaciones();
    }

    private void cargarAnimaciones() {

        animIdle = crearAnim("ui/unidades/espadachin-Idle.png", 6);
        animCaminar = crearAnim("ui/unidades/espadachin-Running.png", 6);
        animAtaque = crearAnim("ui/unidades/espadachin-Attacking.png", 13);
        animHurt = crearAnim("ui/unidades/espadachin-Hurt.png", 3);
        animMuerte = crearAnim("ui/unidades/espadachin-Death.png", 10);
    }

    private Animation<TextureRegion> crearAnim(String path, int frames) {

        Texture tex = new Texture(path);

        int frameWidth = tex.getWidth() / frames;
        int frameHeight = tex.getHeight();

        TextureRegion[] regiones = new TextureRegion[frames];

        for (int i = 0; i < frames; i++) {
            regiones[i] = new TextureRegion(
                    tex,
                    i * frameWidth,
                    0,
                    frameWidth,
                    frameHeight
            );
        }

        return new Animation<>(0.12f, regiones);
    }
}