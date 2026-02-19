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
    private Vector2 posicion;
    private float ancho = 64f;
    private float alto = 128f;

    // =========================
    // COMBATE
    // =========================
    private float vidaMax = 500f;
    private float vida = vidaMax;
    private boolean viva = true;

    private float danio = 30f;
    private float rangoAtaque = 250f;
    private float tiempoEntreAtaques = 1.2f;
    private float tiempoDesdeUltimoAtaque = 0f;

    private int equipo;

    // =========================
    // ORO
    // =========================
    private int oro = 0;
    private float timerOro = 0f;
    private static final float INTERVALO_ORO = 30f;
    private static final int ORO_POR_TICK = 500;

    // =========================
    // PRODUCCIÓN
    // =========================
    private Array<TipoUnidad> colaProduccion = new Array<>();
    private TipoUnidad produciendo = null;
    private float progresoProduccion = 0f;


    private static final float[] CARRILES_Y = {160f, 220f, 280f};

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
    // UPDATE GENERAL
    // =========================
    public void update(float delta, Array<Unidad> enemigos) {
        if (!viva) return;

        generarOro(delta);

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
                return; // ataca solo a una
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

        // 👉 CENTRADA sobre la estatua
        float x = posicion.x + (ancho - barraAncho) / 2f;
        float y = posicion.y + alto + 10f;

        // Fondo
        sr.setColor(0.3f, 0f, 0f, 1f);
        sr.rect(x, y, barraAncho, barraAlto);

        // Vida
        sr.setColor(0f, 1f, 0f, 1f);
        sr.rect(x, y, barraAncho * porcentaje, barraAlto);
    }

    // =========================
    // PRODUCCIÓN
    // =========================
    public Unidad updateProduccion(float delta) {

        if (produciendo == null) {

            if (colaProduccion.size == 0) {
                return null;
            }

            produciendo = colaProduccion.removeIndex(0);
            progresoProduccion = 0f;
        }

        progresoProduccion += delta;

        if (progresoProduccion < produciendo.getTiempoProduccion()) {
            return null;
        }

        // =========================
        // SPAWN CON VARIACIÓN
        // =========================
        float baseX = (equipo == 0)
                ? posicion.x + ancho + 10
                : posicion.x - 40;

        float spawnX = baseX + MathUtils.random(-10f, 10f);
        float spawnY = posicion.y + MathUtils.random(-25f, 25f);

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
   
    
    public int getOro() {
        return oro;
    }

    public float getVida() {
        return vida;
    }

    public float getVidaMaxima() {
        return vidaMax;
    }
    
    public float getPorcentajeVida() {
        return vida / vidaMax;
    }

    public int getEquipo() {
        return equipo;
    }
    
    public float getY() {
        return posicion.y;
    }

    public float getAncho() {
        return ancho;
    }

    public float getAlto() {
        return alto;
    }
}
