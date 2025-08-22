package pro.juego.Ironfall.entidades;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import java.util.List;
import pro.juego.Ironfall.pathfinding.Nodo;
import pro.juego.Ironfall.pathfinding.Pathfinding;

public class Unidad {
    private Texture textura;
    private Vector2 posicion;
    private float escala = 0.1f;
    private boolean seleccionada = false;
    private float LIMITE_DERECHO;
    private float LIMITE_SUPERIOR = 0;
    private float LIMITE_INFERIOR;

    private List<Nodo> path;
    private int indiceActual = 0;
    private float velocidad = 100f;
    private int vida = 100;
    private int danio = 25;
    private float rangoAtaque = 50f;
    private float tiempoEntreAtaques = 1f;
    private float tiempoDesdeUltimoAtaque = 0f;
    public boolean viva = true;
    private int equipo;
    private float rangoDeteccion = 500f;
    private Unidad objetivoDirecto = null;

    public Unidad(String ruta, float x, float y, float anchoMapa, int equipo) {
        textura = new Texture(ruta);
        posicion = new Vector2(x, y);
        this.equipo = equipo;

        float ancho = textura.getWidth() * escala;
        float alto = textura.getHeight() * escala;

        LIMITE_DERECHO = anchoMapa - ancho;
        LIMITE_INFERIOR = -175;
    }

    public void render(SpriteBatch batch) {
        if (!viva) return;

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

    // ========================
    // Actualización de aliados (modo neutral)
    // ========================
    public void update(float delta, List<Unidad> enemigos, Estatua torreEnemiga) {
        if (!viva) return;
        tiempoDesdeUltimoAtaque += delta;

        boolean enMovimiento = (path != null && indiceActual < path.size());

        // 1️⃣ Si está en movimiento por path, solo moverse
        if (enMovimiento) {
            Nodo objetivoNodo = path.get(indiceActual);
            Vector2 destino = new Vector2(objetivoNodo.x * 40, objetivoNodo.y * 40);
            Vector2 direccion = destino.cpy().sub(posicion);
            float distancia = direccion.len();

            if (distancia < 2f) {
                indiceActual++;
            } else {
                direccion.nor();
                posicion.add(direccion.scl(velocidad * delta));
            }
            return; // no atacamos enemigos mientras hay path
        }

        // 2️⃣ Si hay objetivo directo (clic derecho)
        if (objetivoDirecto != null && objetivoDirecto.viva) {
            float distancia = getCentro().dst(objetivoDirecto.getCentro());
            if (distancia > rangoAtaque) {
                Vector2 direccion = objetivoDirecto.getCentro().cpy().sub(posicion).nor();
                posicion.add(direccion.scl(velocidad * delta));
            } else {
                atacar(objetivoDirecto);
            }
            return;
        }

        // 3️⃣ Si está quieta, buscar enemigo cercano y atacar
        Unidad enemigoCercano = buscarEnemigoEnRango(enemigos);
        if (enemigoCercano != null) {
            Vector2 direccion = enemigoCercano.getCentro().cpy().sub(posicion).nor();
            posicion.add(direccion.scl(velocidad * delta));
            atacar(enemigoCercano);
        }
    }

    // ========================
    // Actualización de enemigos (IA)
    // ========================
    public void updateEnemigo(float delta, List<Unidad> aliados, Estatua torreJugador) {
        if (!viva) return;
        tiempoDesdeUltimoAtaque += delta;

        boolean enMovimiento = (path != null && indiceActual < path.size());

        // 1️⃣ Prioridad: atacar unidades aliadas
        Unidad aliadoCercano = buscarEnemigoEnRango(aliados);
        if (aliadoCercano != null) {
            // Seguir a la unidad aliada mientras esté viva
            Vector2 direccion = aliadoCercano.getCentro().cpy().sub(posicion).nor();
            posicion.add(direccion.scl(velocidad * delta));
            atacar(aliadoCercano);
            return; // mientras hay aliado en rango no se mueve a la torre
        }

        // 2️⃣ Si no hay aliados en rango, avanzar hacia la torre
        if (enMovimiento) {
            Nodo objetivoNodo = path.get(indiceActual);
            Vector2 destino = new Vector2(objetivoNodo.x * 40, objetivoNodo.y * 40);
            Vector2 direccion = destino.cpy().sub(posicion);
            float distancia = direccion.len();

            if (distancia < 2f) {
                indiceActual++;
            } else {
                direccion.nor();
                posicion.add(direccion.scl(velocidad * delta));
            }
            return;
        }

        // 3️⃣ Si no hay path hacia torre, atacar torre si está en rango
        float distanciaTorre = getCentro().dst(torreJugador.getCentro());
        if (distanciaTorre <= rangoAtaque) {
            atacar(torreJugador);
        }
    }

    // ========================
    // Métodos auxiliares
    // ========================
    private Unidad buscarEnemigoEnRango(List<Unidad> enemigos) {
        Unidad masCercano = null;
        float menorDistancia = Float.MAX_VALUE;

        for (Unidad enemigo : enemigos) {
            if (!enemigo.viva) continue;

            float distancia = getCentro().dst(enemigo.getCentro());
            if (distancia <= rangoDeteccion && distancia < menorDistancia) {
                menorDistancia = distancia;
                masCercano = enemigo;
            }
        }

        return masCercano;
    }

    private void atacar(Unidad objetivo) {
        if (tiempoDesdeUltimoAtaque >= tiempoEntreAtaques) {
            objetivo.recibirDanio(danio);
            tiempoDesdeUltimoAtaque = 0f;
        }
    }

    private void atacar(Estatua torre) {
        if (tiempoDesdeUltimoAtaque >= tiempoEntreAtaques) {
            torre.recibirDanio(danio);
            tiempoDesdeUltimoAtaque = 0f;
        }
    }

    public void recibirDanio(int cantidad) {
        vida -= cantidad;
        if (vida <= 0) {
            vida = 0;
            viva = false;
        }
    }

    public void setPath(List<Nodo> path) {
        this.path = path;
        this.indiceActual = 0;
        this.objetivoDirecto = null; // quitar objetivo directo si se da path manual
    }

    public void setObjetivo(Unidad u, Pathfinding pathfinding) {
        this.objetivoDirecto = u;
        Nodo inicio = convertirAPosicionCelda(getCentro());
        Nodo fin = convertirAPosicionCelda(u.getCentro());
        if (inicio != null && fin != null) {
            this.path = pathfinding.suavizarCamino(pathfinding.encontrarCamino(inicio, fin));
            this.indiceActual = 0;
        }
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
        return new Vector2(posicion.x + getAncho() / 2, posicion.y + getAlto() / 2);
    }

    public float getAncho() {
        return textura.getWidth() * escala;
    }

    public float getAlto() {
        return textura.getHeight() * escala;
    }

    public float getX() { return posicion.x; }
    public float getY() { return posicion.y; }

    public void setVida(int vida) { this.vida = vida; }
    public void setDaño(int danio) { this.danio = danio; }
    public void setRangoAtaque(float rango) { this.rangoAtaque = rango; }
    public void setTiempoEntreAtaques(float tiempo) { this.tiempoEntreAtaques = tiempo; }

    public int getEquipo() { return equipo; }
    public boolean estaViva() { return viva; }

    private Nodo convertirAPosicionCelda(Vector2 pos) {
        int x = (int) (pos.x / 40);
        int y = (int) (pos.y / 40);
        return new Nodo(x, y, true);
    }
}
