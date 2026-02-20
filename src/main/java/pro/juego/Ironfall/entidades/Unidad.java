package pro.juego.Ironfall.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    protected boolean viva = true;
    protected int equipo;
    protected int direccion;

    protected float ancho = 32f;
    protected float alto = 32f;

    // =========================
    // ANIMACIONES
    // =========================
    protected Animation<TextureRegion> animIdle;
    protected Animation<TextureRegion> animCaminar;
    protected Animation<TextureRegion> animAtaque;
    protected Animation<TextureRegion> animHurt;
    protected Animation<TextureRegion> animMuerte;

    protected float stateTime = 0f;

    protected Estado estado = Estado.IDLE;

    protected enum Estado {
        IDLE,
        CAMINANDO,
        ATACANDO,
        RECIBIENDO_DAÑO,
        MUERTO
    }

    // =========================
    public Unidad(Texture textura, float x, float y, int equipo) {
        this.textura = textura;
        this.posicion = new Vector2(x, y);
        this.equipo = equipo;
        this.direccion = (equipo == 0) ? 1 : -1;
    }

    // =========================
    public void update(
            float delta,
            Array<Unidad> enemigos,
            Array<Unidad> aliados,
            Estatua estatuaEnemiga
    ) {
        stateTime += delta;

        if (!viva) {
            estado = Estado.MUERTO;
            return;
        }

        tiempoDesdeUltimoAtaque += delta;

        Unidad enemigo = buscarEnemigoCercano(enemigos);

        if (enemigo != null) {
            atacarUnidad(enemigo);
            return;
        }

        if (estatuaEnemiga != null && estatuaEnemiga.estaViva()) {
            float dist = Math.abs(estatuaEnemiga.getX() - posicion.x);
            if (dist <= rangoAtaque) {
                atacarEstatua(estatuaEnemiga);
                return;
            }
        }

        avanzar(delta);
    }

    protected void avanzar(float delta) {
        estado = Estado.CAMINANDO;
        posicion.x += velocidad * delta * direccion;
    }

    protected void atacarUnidad(Unidad objetivo) {
        if (tiempoDesdeUltimoAtaque < tiempoEntreAtaques) return;

        estado = Estado.ATACANDO;
        stateTime = 0f;
        objetivo.recibirDanio(danio);
        tiempoDesdeUltimoAtaque = 0f;
    }

    protected Unidad buscarEnemigoCercano(Array<Unidad> enemigos) {
        for (Unidad u : enemigos) {
            if (!u.estaViva()) continue;
            float dist = Math.abs(u.posicion.x - posicion.x);
            if (dist <= rangoAtaque)
                return u;
        }
        return null;
    }

    protected void atacarEstatua(Estatua estatua) {
        if (tiempoDesdeUltimoAtaque < tiempoEntreAtaques) return;

        estado = Estado.ATACANDO;
        stateTime = 0f;
        estatua.recibirDanio(danio);
        tiempoDesdeUltimoAtaque = 0f;
    }

    public void recibirDanio(float cantidad) {
        vida -= cantidad;

        if (vida > 0) {
            estado = Estado.RECIBIENDO_DAÑO;
            stateTime = 0f;
        } else {
            vida = 0;
            viva = false;
            estado = Estado.MUERTO;
            stateTime = 0f;
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

        if (animIdle == null) {
            batch.draw(textura, posicion.x, posicion.y, ancho, alto);
            return;
        }

        TextureRegion frame;

        switch (estado) {
            case CAMINANDO:
                frame = animCaminar.getKeyFrame(stateTime, true);
                break;

            case ATACANDO:
                frame = animAtaque.getKeyFrame(stateTime, false);
                break;

            case RECIBIENDO_DAÑO:
                frame = animHurt.getKeyFrame(stateTime, false);
                break;

            case MUERTO:
                frame = animMuerte.getKeyFrame(stateTime, false);
                break;

            default:
                frame = animIdle.getKeyFrame(stateTime, true);
        }

        // Flip automático según dirección
        if ((direccion == -1 && !frame.isFlipX()) ||
                (direccion == 1 && frame.isFlipX())) {
            frame.flip(true, false);
        }

        batch.draw(frame, posicion.x, posicion.y, ancho, alto);
    }
}
