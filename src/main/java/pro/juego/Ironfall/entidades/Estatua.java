package pro.juego.Ironfall.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import pro.juego.Ironfall.enums.CreacionUnidades;
import pro.juego.Ironfall.enums.TipoUnidad;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Estatua {

    // =========================
    // POSICIÓN / RENDER
    // =========================
    private static Texture textura;
    private final Vector2 posicion;
    private final float ancho = 64f;
    private final float alto = 128f;

    // =========================
    // COMBATE
    // =========================
    private final float vidaMax = 500f;
    private float vida = vidaMax;
    private boolean viva = true;

    private final float danio = 30f;
    private final float rangoAtaque = 250f;
    private final float tiempoEntreAtaques = 1.2f;
    private float tiempoDesdeUltimoAtaque = 0f;

    private final int equipo;

    // =========================
    // ORO
    // =========================
    private int oro = 0;
    private float timerOro = 0f;
    private static final float INTERVALO_ORO = 30f;
    private static final int ORO_POR_TICK = 500;

    // ✅ ONLINE: si es false, NO generamos oro localmente (lo manda el server)
    private boolean generarOroHabilitado = true;

    // ✅ ONLINE: si es false, NO usamos random en spawn (para evitar desync)
    private boolean spawnAleatorio = true;

    // =========================
    // PRODUCCIÓN
    // =========================
    private final Array<TipoUnidad> colaProduccion = new Array<>();
    private TipoUnidad produciendo = null;
    private float progresoProduccion = 0f;

    // =========================
    // CONSTRUCTOR
    // =========================
    public Estatua(float x, float y, int equipo) {
        this.posicion = new Vector2(x, y);
        this.equipo = equipo;

        if (textura == null) {
            textura = new Texture("ui/unidades/Estatua1.png");
        }
    }

    // =========================
    // SETTERS ONLINE
    // =========================
    public void setGenerarOroHabilitado(boolean habilitado) {
        this.generarOroHabilitado = habilitado;
    }

    public void setSpawnAleatorio(boolean spawnAleatorio) {
        this.spawnAleatorio = spawnAleatorio;
    }

    // =========================
    // UPDATE GENERAL
    // =========================
    public void update(float delta, Array<Unidad> enemigos) {
        if (!viva) return;

        // ✅ En online, el oro lo maneja el server
        if (generarOroHabilitado) generarOro(delta);

        tiempoDesdeUltimoAtaque += delta;
        atacarEnemigos(enemigos);
    }

    // =========================
    // ATAQUE
    // =========================
    private void atacarEnemigos(Array<Unidad> enemigos) {
        if (tiempoDesdeUltimoAtaque < tiempoEntreAtaques) return;

        for (Unidad u : enemigos) {
            if (!u.estaViva()) continue;

            float distancia = Math.abs(u.getX() - posicion.x);
            if (distancia <= rangoAtaque) {
                u.recibirDanio(danio);
                tiempoDesdeUltimoAtaque = 0f;
                return;
            }
        }
    }

    // =========================
    // ORO
    // =========================
    private void generarOro(float delta) {
        timerOro += delta;
        if (timerOro >= INTERVALO_ORO) {
            oro += ORO_POR_TICK;
            timerOro = 0f;
        }
    }

    // ⚠️ OJO: en ONLINE NO deberías llamarlo desde UI.
    // En ONLINE se usa encolarProduccionSinCosto() cuando llega SPAWN_OK.
    public boolean intentarProducir(TipoUnidad tipo) {
        if (oro < tipo.getCosto()) return false;
        oro -= tipo.getCosto();
        colaProduccion.add(tipo);
        return true;
    }

    // =========================
    // BarraVida
    // =========================
    public void renderBarraVida(ShapeRenderer sr) {
        if (!viva) return;

        float porcentaje = vida / vidaMax;

        float barraAncho = ancho;
        float barraAlto = 6f;

        float x = posicion.x + (ancho - barraAncho) / 2f;
        float y = posicion.y + alto + 10f;

        sr.setColor(0.3f, 0f, 0f, 1f);
        sr.rect(x, y, barraAncho, barraAlto);

        sr.setColor(0f, 1f, 0f, 1f);
        sr.rect(x, y, barraAncho * porcentaje, barraAlto);
    }

    // =========================
    // PRODUCCIÓN
    // =========================
    public Unidad updateProduccion(float delta) {

        if (produciendo == null) {
            if (colaProduccion.size == 0) return null;
            produciendo = colaProduccion.removeIndex(0);
            progresoProduccion = 0f;
        }

        progresoProduccion += delta;

        if (progresoProduccion < produciendo.getTiempoProduccion()) {
            return null;
        }

        // ✅ Spawn base (lado de la estatua según equipo)
        float baseX = (equipo == 0)
                ? posicion.x + ancho + 10f
                : posicion.x - 40f;

        float spawnX = baseX;
        float spawnY = posicion.y;


        Unidad nueva = CreacionUnidades.crearUnidad(
                produciendo,
                spawnX,
                spawnY,
                equipo
        );

        produciendo = null;
        progresoProduccion = 0f;

        return nueva;
    }

    // =========================
    // DAÑO
    // =========================
    public void recibirDanio(float danio) {
        vida -= danio;
        if (vida <= 0) {
            vida = 0;
            viva = false;
            System.out.println("Estatua equipo " + equipo + " destruida");
        }
    }

    public boolean estaViva() { return viva; }
    public float getX() { return posicion.x; }

    // =========================
    // RENDER
    // =========================
    public void render(SpriteBatch batch) {
        if (!viva) return;

        if (equipo == 1) {
            // Equipo 1 (derecha): espejar para que mire a la izquierda
            batch.draw(textura,
                    posicion.x + ancho, posicion.y,  // corregimos el origen
                    -ancho, alto                      // ancho negativo = flip
            );
        } else {
            // Equipo 0 (izquierda): normal (mira a la derecha)
            batch.draw(textura, posicion.x, posicion.y, ancho, alto);
        }
    }

    // ✅ ONLINE: cuando llega SPAWN_OK del server (NO se cobra oro acá)
    public void encolarProduccionSinCosto(TipoUnidad tipo) {
        colaProduccion.add(tipo);
    }

    // ✅ ONLINE: el server manda el oro y acá lo aplicás
    public void setOro(int oro) {
        this.oro = Math.max(0, oro);
    }

    public void setVida(float vida) {
        this.vida = Math.max(0, Math.min(vida, vidaMax));
        this.viva = this.vida > 0;
    }

    public int getOro() { return oro; }
    public float getVida() { return vida; }
    public float getVidaMaxima() { return vidaMax; }
    public float getPorcentajeVida() { return vida / vidaMax; }
    public int getEquipo() { return equipo; }
    public float getY() { return posicion.y; }
    public float getAncho() { return ancho; }
    public float getAlto() { return alto; }
}