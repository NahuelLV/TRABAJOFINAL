package pro.juego.Ironfall.entidades;

import com.badlogic.gdx.graphics.Texture;

public class Bestia extends Unidad {

    public Bestia(float x, float y, int equipo) {
        super(new Texture("ui/unidades/Bestia.png"), x, y, equipo);

        vida = 220;
        danio = 35;
        velocidad = 45f;
        rangoAtaque = 26f;
        tiempoEntreAtaques = 1.4f;

        ancho = 40f;
        alto = 40f;
    }
}
