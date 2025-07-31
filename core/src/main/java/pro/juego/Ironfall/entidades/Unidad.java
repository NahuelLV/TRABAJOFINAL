package pro.juego.Ironfall.entidades;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import java.util.List;
import com.badlogic.gdx.math.Vector2;
import pro.juego.Ironfall.pathfinding.Nodo;

public class Unidad {
	  private Texture textura;
	    private Vector2 posicion;
	    private float escala = 0.1f;
	    private boolean seleccionada = false;
	    private float LIMITE_IZQUIERDO = 0;
	    private float LIMITE_DERECHO;
	    private float LIMITE_SUPERIOR = 0;
	    private float LIMITE_INFERIOR;
	    // Pathfinding
	    private List<Nodo> path;
	    private int indiceActual = 0;
	    private float velocidad = 100f;
	
	public Unidad(String ruta, float x, float y, float anchoMapa) {
        textura = new Texture(ruta);
        posicion = new Vector2(x, y);
       

        
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

    public void update(float delta, List<Unidad> todasLasUnidades) {
    	float radioSeparacion = 25f;
    	float fuerzaSeparacion = 50f;
    	
    	if (path != null && indiceActual < path.size()) {
            Nodo objetivo = path.get(indiceActual);
            Vector2 destino = new Vector2(objetivo.x * 40, objetivo.y * 40);
            Vector2 direccion = destino.cpy().sub(posicion);
            float distancia =  direccion.len();

            if (distancia < 2f) {
                indiceActual++;
            }else {
            		direccion.nor();
            		Vector2 nuevoMovimiento = direccion.scl(velocidad * delta);
            		
            		if(!hayColision(posicion.cpy().add(nuevoMovimiento), todasLasUnidades)) {
            			posicion.add(nuevoMovimiento);
            		}
            }
            	
              
        }
    	for (Unidad otra : todasLasUnidades) {
    	    if (otra != this && this.posicion.dst(otra.posicion) < radioSeparacion) {
    	        Vector2 repulsion = this.posicion.cpy().sub(otra.posicion).nor().scl(fuerzaSeparacion * delta);
    	        posicion.add(repulsion);
    	    }
    	}
    }
    private boolean hayColision(Vector2 nuevaPos, List<Unidad> otras) {
        float radio = 20; // radio de colisión
        for (Unidad u : otras) {
            if (u == this) continue;
            if (u.posicion.dst(nuevaPos) < radio) {
                return true;
            }
        }
        return false;
    }

    public void setPath(List<Nodo> path) {
        this.path = path;
        this.indiceActual = 0;
    }

    public void setSeleccionada(boolean seleccionada) {
        this.seleccionada = seleccionada;
    }

    public boolean estaSeleccionada() {
        return seleccionada;
    }

    public boolean fueClickeado(float x, float y) {
        return x >= posicion.x && x <= posicion.x + getAncho()
            && y >= posicion.y && y <= posicion.y + getAlto();
    }

    public Vector2 getCentro() {
        return new Vector2(
            posicion.x + getAncho() / 2,
            posicion.y + getAlto() / 2
        );
    }

    public float getAncho() {
        return textura.getWidth() * escala;
    }

    public float getAlto() {
        return textura.getHeight() * escala;
    }

    public float getX() {
        return posicion.x;
    }

    public float getY() {
        return posicion.y;
    }

    public void setPosicion(float x, float y) {
        posicion.set(x, y);
    }
}
