package pro.juego.Ironfall.hud;

import java.util.EnumMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import pro.juego.Ironfall.entidades.Estatua;
import pro.juego.Ironfall.enums.TipoUnidad;

public class HudJuego {

    private Stage stage;
    private Skin skin;
    private Estatua estatuaJugador;
    private Label labelOro;

    private Map<TipoUnidad, TextButton> botonesUnidades =
            new EnumMap<>(TipoUnidad.class);

    public HudJuego(Estatua estatuaJugador, boolean interactivo, boolean alineadoDerecha) {

        this.estatuaJugador = estatuaJugador;

        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        crearUI(interactivo, alineadoDerecha);
    }

    private void crearUI(boolean interactivo, boolean alineadoDerecha) {

        Table root = new Table();
        root.setFillParent(true);

        if (alineadoDerecha) {
            root.top().right().pad(10);
        } else {
            root.top().left().pad(10);
        }

        // ===== ORO =====
        labelOro = new Label("Oro: 0", skin);
        root.add(labelOro).left();
        root.row().padTop(10);

        // ===== BOTONES =====
        for (TipoUnidad tipo : TipoUnidad.values()) {

            String texto = tipo.name() + " (" + tipo.getCosto() + ")";

            // 🔵 Si NO es interactivo, agregamos la tecla visual
            if (!interactivo) {

                String tecla = "";

                switch (tipo) {
                    case ESPADACHIN:
                        tecla = "A";
                        break;
                    case BESTIA:
                        tecla = "S";
                        break;
                    case ARQUERO:
                        tecla = "D";
                        break;
                }

                texto += " [" + tecla + "]";
            }

            TextButton btn = new TextButton(texto, skin);

            if (interactivo) {

                btn.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        estatuaJugador.intentarProducir(tipo);
                    }
                });

            } else {
                btn.setDisabled(true); // HUD visual
            }

            root.add(btn).width(220).height(40).left();
            root.row().padTop(6);

            botonesUnidades.put(tipo, btn);
        }

        stage.addActor(root);
    }

    // =========================
    // UPDATE
    // =========================
    public void update(float delta) {

        labelOro.setText("Oro: " + estatuaJugador.getOro());

        for (Map.Entry<TipoUnidad, TextButton> entry : botonesUnidades.entrySet()) {

            TipoUnidad tipo = entry.getKey();
            TextButton btn = entry.getValue();

            if (!btn.isDisabled()) {
                btn.setDisabled(estatuaJugador.getOro() < tipo.getCosto());
            }
        }

        stage.act(delta);
    }

    // =========================
    // RENDER
    // =========================
    public void render() {
        stage.draw();
    }

    public Stage getStage() {
        return stage;
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
