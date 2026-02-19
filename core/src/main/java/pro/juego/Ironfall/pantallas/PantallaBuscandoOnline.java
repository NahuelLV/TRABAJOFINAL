package pro.juego.Ironfall.pantallas;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import pro.juego.Ironfall.IronfallJuego;
import pro.juego.Ironfall.network.ClienteOnline;
import pro.juego.Ironfall.enums.ModoJuego;

public class PantallaBuscandoOnline implements Screen {

    private IronfallJuego game;
    private Stage stage;
    private Skin skin;
    private ClienteOnline cliente;

    public PantallaBuscandoOnline(IronfallJuego game) {
        this.game = game;
    }

    @Override
    public void show() {

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Label label = new Label("BUSCANDO PARTIDA...", skin);
        label.setFontScale(1.5f);
        label.setPosition(
                Gdx.graphics.getWidth() / 2f - 150,
                Gdx.graphics.getHeight() / 2f
        );

        stage.addActor(label);

        cliente = new ClienteOnline();

        new Thread(() -> {
            if (cliente.buscarServidor()) {

                Gdx.app.postRunnable(() ->
                        game.setScreen(
                                new PantallaJuego(game, ModoJuego.ONLINE)
                        )
                );
            }
        }).start();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); skin.dispose(); }
}
