package pro.juego.Ironfall.entidades;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;


public class Unidad {
    private Texture textura;
    private Vector2 posicion;
    private Vector2 destino;
    private float velocidad = 150;
    private boolean seleccionada = false;
    private  float LIMITE_IZQUIERDO = 0;
	private  float LIMITE_DERECHO;
	private  float LIMITE_SUPERIOR = 0;
	private  float LIMITE_INFERIOR;
	private  float escala = 0.1f;
	
	public Unidad(String ruta, float x, float y, float anchoMapa) {
        textura = new Texture(ruta);
        posicion = new Vector2(x, y);
        destino = new Vector2(x, y);

        
        float ancho = textura.getWidth() * escala;
        float alto = textura.getHeight() * escala;

        
        LIMITE_DERECHO = anchoMapa - ancho;
        LIMITE_SUPERIOR = 256 - alto;
        LIMITE_INFERIOR = -175;
    }

    public void render(SpriteBatch batch) {
        
        batch.draw(textura, posicion.x, posicion.y,
                   textura.getWidth() * escala, textura.getHeight() * escala);

        if (seleccionada) {
            batch.end();
            ShapeRenderer sr = new ShapeRenderer();
            sr.setProjectionMatrix(batch.getProjectionMatrix());
            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.setColor(Color.RED);
            sr.rect(posicion.x, posicion.y,
                    textura.getWidth() * escala, textura.getHeight() * escala);
            sr.end();
            sr.dispose();
            batch.begin();
        }
    }

    public void update(float delta) {
    	
        if (!posicion.epsilonEquals(destino, 1f)) {
            Vector2 dir = destino.cpy().sub(posicion).nor();
            posicion.add(dir.scl(velocidad * delta));
        }
        posicion.x = MathUtils.clamp(posicion.x, LIMITE_IZQUIERDO, LIMITE_DERECHO);
        posicion.y = MathUtils.clamp(posicion.y, LIMITE_INFERIOR, LIMITE_SUPERIOR);
        
    }

    public boolean fueClickeado(float x, float y) {
        this.escala = 0.1f;
        return x >= posicion.x && x <= posicion.x + textura.getWidth() * escala
            && y >= posicion.y && y <= posicion.y + textura.getHeight() * escala;
    }

    public Vector2 getCentro() {
         this.escala = 0.1f;
        return new Vector2(
            posicion.x + textura.getWidth() * escala / 2,
            posicion.y + textura.getHeight() * escala / 2
        );
    }

    public void setDestino(Vector2 destino) {
        this.destino = destino;
    }

    public void setSeleccionada(boolean seleccionada) {
        this.seleccionada = seleccionada;
    }

    public boolean estaSeleccionada() {
        return seleccionada;
    }
}
