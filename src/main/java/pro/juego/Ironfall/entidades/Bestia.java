package pro.juego.Ironfall.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;

public class Bestia extends Unidad {

    public Bestia(float x, float y, int equipo) {
        super(new Texture("ui/unidades/bestia-Idle.png"), x, y, equipo);

        vida = 220;
        danio = 35;
        velocidad = 45f;
        rangoAtaque = 26f;
        tiempoEntreAtaques = 1.4f;

        ancho = 64f;
        alto = 64f;

        cargarAnimaciones();
    }

    private void cargarAnimaciones() {

        animIdle = crearAnim("ui/unidades/bestia-Idle.png", 8);
        animAtaque = crearAnim("ui/unidades/bestia-Attack.png", 4);
        animCaminar = crearAnim("ui/unidades/bestia-walk.png", 11);
        animHurt = crearAnim("ui/unidades/bestia-Hurt.png", 2);
        animMuerte = crearAnim("ui/unidades/bestia-Dead.png", 2);
    }

    private Animation<TextureRegion> crearAnim(String path, int frames) {

        Texture tex = new Texture(path);
        int frameWidth = tex.getWidth() / frames;

        TextureRegion[] regiones = new TextureRegion[frames];

        for (int i = 0; i < frames; i++) {
            regiones[i] = new TextureRegion(
                    tex,
                    i * frameWidth,
                    0,
                    frameWidth,
                    tex.getHeight()
            );
        }

        return new Animation<>(0.15f, regiones);
    }
}