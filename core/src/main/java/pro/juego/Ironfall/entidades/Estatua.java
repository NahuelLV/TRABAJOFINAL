package pro.juego.Ironfall.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;

/**
 * Representa la estatua del jugador o del enemigo.
 * Tiene vida, una imagen, y puede decir si fue destruida.
 */
public class Estatua {

    private Texture textura;
    private Vector2 posicion;
    private float escala = 0.3f;

    private float vidaMax = 1000;      // Vida total
    private float vidaActual;          // Vida restante

    /**
     * Crea la estatua en una posición con la imagen especificada.
     * @param rutaTextura Ruta de la imagen (por ejemplo, "estatua.png")
     * @param x Posición X
     * @param y Posición Y
     */
    public Estatua(String rutaTextura, float x, float y) {
        this.textura = new Texture(rutaTextura);
        this.posicion = new Vector2(x, y);
        this.vidaActual = vidaMax; // Comienza con vida completa
    }

    /**
     * Dibuja la imagen de la estatua.
     */
    public void render(SpriteBatch batch) {
        batch.draw(textura, posicion.x, posicion.y,
                   textura.getWidth() * escala, textura.getHeight() * escala);
    }

    /**
     * Dibuja una barra de vida encima de la estatua.
     */
    public void renderBarraVida(ShapeRenderer sr) {
        float ancho = textura.getWidth() * escala;
        float alto = 8;
        float x = posicion.x;
        float y = posicion.y + textura.getHeight() * escala + 5;

        float porcentaje = MathUtils.clamp(vidaActual / vidaMax, 0, 1);

        // Fondo rojo (vida máxima)
        sr.setColor(1, 0, 0, 1); // Rojo
        sr.rect(x, y, ancho, alto);

        // Barra verde (vida actual)
        sr.setColor(0, 1, 0, 1); // Verde
        sr.rect(x, y, ancho * porcentaje, alto);
    }

    /**
     * Este método será llamado por otro programador para hacer daño.
     * Vos no lo usás, pero lo dejás hecho.
     */
    public void recibirDanio(float cantidad) {
        vidaActual -= cantidad;
        if (vidaActual < 0) vidaActual = 0;
    }

    /**
     * Retorna true si la vida llegó a cero.
     */
    public boolean estaDestruida() {
        return vidaActual <= 0;
    }

    /**
     * Libera la imagen cuando no se usa más.
     */
    public void dispose() {
        textura.dispose();
    }
}