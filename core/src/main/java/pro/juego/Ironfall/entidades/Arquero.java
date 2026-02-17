package pro.juego.Ironfall.entidades;

import com.badlogic.gdx.graphics.Texture;

public class Arquero extends Unidad {

	public Arquero(float x, float y, int equipo) {
        super(new Texture("ui/unidades/Arquero.png"), x, y, equipo);

        vida = 70;
        danio = 18;
        velocidad = 60f;
        rangoAtaque = 160f;
        tiempoEntreAtaques = 1.1f;

        ancho = 32f;
        alto = 32f;
    }
}
