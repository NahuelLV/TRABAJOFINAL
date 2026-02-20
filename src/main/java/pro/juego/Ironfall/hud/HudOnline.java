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

public class HudOnline {

    public interface SpawnSender {
        void enviarSpawn(TipoUnidad tipo);
    }

    private final Stage stage;
    private final Skin skin;
    private final Estatua estatuaLocal;
    private final Label labelOro;

    private final SpawnSender sender;

    private final Map<TipoUnidad, TextButton> botonesUnidades =
            new EnumMap<>(TipoUnidad.class);

    public HudOnline(Estatua estatuaLocal, boolean alineadoDerecha, SpawnSender sender) {
        this.estatuaLocal = estatuaLocal;
        this.sender = sender;

        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Table root = new Table();
        root.setFillParent(true);

        if (alineadoDerecha) root.top().right().pad(10);
        else root.top().left().pad(10);

        Label oro = new Label("Oro: 0", skin);
        labelOro = oro;
        root.add(labelOro).left();
        root.row().padTop(10);

        for (TipoUnidad tipo : TipoUnidad.values()) {

            String texto = tipo.name() + " (" + tipo.getCosto() + ")";
            TextButton btn = new TextButton(texto, skin);

            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Online: SOLO pedimos al server
                    if (sender != null) sender.enviarSpawn(tipo);
                }
            });

            root.add(btn).width(220).height(40).left();
            root.row().padTop(6);

            botonesUnidades.put(tipo, btn);
        }

        stage.addActor(root);
    }

    public void update(float delta) {
        labelOro.setText("Oro: " + estatuaLocal.getOro());

        for (Map.Entry<TipoUnidad, TextButton> entry : botonesUnidades.entrySet()) {
            TipoUnidad tipo = entry.getKey();
            TextButton btn = entry.getValue();
            btn.setDisabled(estatuaLocal.getOro() < tipo.getCosto());
        }

        stage.act(delta);
    }

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