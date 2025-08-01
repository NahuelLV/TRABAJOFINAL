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
    
    public Vector2 getCentro() {
        return new Vector2(posicion.x + getAncho() / 2, posicion.y + getAlto() / 2);
    }
    
    public void recibirDanio(float cantidad) {
        vidaActual -= cantidad;
        if (vidaActual < 0) vidaActual = 0;
    }

   
    public boolean estaDestruida() {
        return vidaActual <= 0;
    }

    
    public void dispose() {
        textura.dispose();
    }
    public float getX() {
        return posicion.x;
    }

    public float getY() {
        return posicion.y;
    }
    
    public float getAncho() {
        return textura.getWidth() * escala;
    }

    public float getAlto() {
        return textura.getHeight() * escala;
    }
}