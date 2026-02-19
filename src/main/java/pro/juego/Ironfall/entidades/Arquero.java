package pro.juego.Ironfall.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;

public class Arquero extends Unidad {

        public Arquero(float x, float y, int equipo) {
                super(new Texture("ui/unidades/GandalfHardcore_Archer_sheet.png"), x, y, equipo);

                vida = 70;
                danio = 18;
                velocidad = 60f;
                rangoAtaque = 160f;
                tiempoEntreAtaques = 1.1f;

                ancho = 48f;
                alto = 48f;

                cargarAnimaciones();
        }

        private void cargarAnimaciones() {

                int totalWidth = textura.getWidth();   // 704
                int totalHeight = textura.getHeight(); // 320

                int filas = 5;
                int frameHeight = totalHeight / filas; // 64

                // IDLE (5 frames)
                animIdle = crearAnimFila(0, 5, frameHeight);

                // ATAQUE (11 frames)
                animAtaque = crearAnimFila(1, 11, frameHeight);

                // CAMINAR (8 frames)
                animCaminar = crearAnimFila(2, 8, frameHeight);

                // HURT (5 frames)
                animHurt = crearAnimFila(3, 5, frameHeight);

                // MUERTE (6 frames)
                animMuerte = crearAnimFila(4, 6, frameHeight);
        }

        private Animation<TextureRegion> crearAnimFila(int fila, int frames, int frameHeight) {

                int frameWidth = 64; // ancho real del frame del arquero

                TextureRegion[] regiones = new TextureRegion[frames];

                for (int i = 0; i < frames; i++) {

                        regiones[i] = new TextureRegion(
                                textura,
                                i * frameWidth,
                                fila * frameHeight,
                                frameWidth,
                                frameHeight
                        );
                }

                return new Animation<>(0.1f, regiones);
        }
}