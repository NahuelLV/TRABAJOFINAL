package pro.juego.Ironfall.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public abstract class Unidad {

    protected Texture textura;
    protected Vector2 posicion;

    protected float vida;
    protected float danio;
    protected float velocidad;
    protected float rangoAtaque;
    protected float tiempoEntreAtaques;
    protected float tiempoDesdeUltimoAtaque = 0f;
    protected float hitboxAncho = 22f;
    protected float hitboxAlto = 26f;
    protected float separacionDeseadaX = 22f;
    protected float separacionDeseadaY = 18f;
    protected boolean viva = true;
    protected int equipo;
    protected int direccion; // +1 derecha, -1 izquierda

    protected float ancho = 32f;
    protected float alto = 32f;
    // =========================
    // CONSTRUCTOR
    // =========================
    public Unidad(Texture textura, float x, float y, int equipo) {
        this.textura = textura;
        this.posicion = new Vector2(x, y);
        this.equipo = equipo;
        this.direccion = (equipo == 0) ? 1 : -1;
    }

    // =========================
    // UPDATE GENERAL
    // =========================
    public void update(
            float delta,
            Array<Unidad> enemigos,
            Array<Unidad> aliados,
            Estatua estatuaEnemiga
    ) {
        if (!viva) return;

        tiempoDesdeUltimoAtaque += delta;

        // 1️⃣ atacar unidad enemiga
        Unidad enemigo = buscarEnemigoCercano(enemigos);
        if (enemigo != null) {
            atacarUnidad(enemigo);
            return;
        }

        // 2️⃣ atacar estatua
        if (estatuaEnemiga != null && estatuaEnemiga.estaViva()) {
            float dist = Math.abs(estatuaEnemiga.getX() - posicion.x);
            if (dist <= rangoAtaque) {
                atacarEstatua(estatuaEnemiga);
                return;
            }
        }

        // 3️⃣ avanzar
        avanzar(delta, aliados);
    }

    // =========================
    // MOVIMIENTO + COLISIÓN
    // =========================
    protected void avanzar(float delta, Array<Unidad> aliados) {

        float dx = velocidad * delta * direccion;
        float dy = 0f;

        for (Unidad aliada : aliados) {
            if (aliada == this || !aliada.estaViva()) continue;

            float diffX = (posicion.x + dx) - aliada.posicion.x;
            float diffY = posicion.y - aliada.posicion.y;

            float absX = Math.abs(diffX);
            float absY = Math.abs(diffY);

            // estamos demasiado cerca
            if (absX < separacionDeseadaX && absY < separacionDeseadaY) {

                // empuje lateral suave (no bloqueo)
                dy += Math.signum(diffY) * 30f * delta;

                // si están exactamente alineadas, empuja random leve
                if (diffY == 0) {
                    dy += (Math.random() > 0.5 ? 1 : -1) * 15f * delta;
                }
            }
        }

        // avanzar siempre
        posicion.x += dx;
        posicion.y += dy;
    }

    // =========================
    // COMBATE UNIDADES
    // =========================
    protected void atacarUnidad(Unidad objetivo) {
        if (tiempoDesdeUltimoAtaque < tiempoEntreAtaques) return;

        objetivo.recibirDanio(danio);
        tiempoDesdeUltimoAtaque = 0f;
    }

    protected Unidad buscarEnemigoCercano(Array<Unidad> enemigos) {
        Unidad objetivo = null;
        float mejorDist = Float.MAX_VALUE;

        for (Unidad u : enemigos) {
            if (!u.estaViva()) continue;

            float dist = Math.abs(u.posicion.x - posicion.x);
            if (dist <= rangoAtaque && dist < mejorDist) {
                mejorDist = dist;
                objetivo = u;
            }
        }
        return objetivo;
    }

    // =========================
    // COMBATE ESTATUA
    // =========================
    protected void atacarEstatua(Estatua estatua) {
        if (tiempoDesdeUltimoAtaque < tiempoEntreAtaques) return;

        estatua.recibirDanio(danio);
        tiempoDesdeUltimoAtaque = 0f;
    }

    // =========================
    // VIDA
    // =========================
    public void recibirDanio(float cantidad) {
        vida -= cantidad;
        if (vida <= 0) {
            vida = 0;
            viva = false;
        }
    }

    public boolean estaViva() {
        return viva;
    }

    public float getX() {
        return posicion.x;
    }

    // =========================
    // RENDER
    // =========================
    public void render(SpriteBatch batch) {
        if (!viva) return;
        batch.draw(textura, posicion.x, posicion.y, ancho, alto);
    }
}
