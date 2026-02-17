package pro.juego.Ironfall.entidades;

import com.badlogic.gdx.graphics.Texture;

public class Espadachin extends Unidad {

    private static Texture textura;

    public Espadachin(float x, float y, int equipo) {
        super(getTextura(), x, y, equipo);

        vida = 100;
        danio = 15;
        velocidad = 60f;
        rangoAtaque = 30f;
        tiempoEntreAtaques = 1f;
    }

    private static Texture getTextura() {
        if (textura == null) {
            textura = new Texture("ui/unidades/Espadachin.png");
        }
        return textura;
    }
}
