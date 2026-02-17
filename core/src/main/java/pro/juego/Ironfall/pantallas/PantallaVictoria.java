package pro.juego.Ironfall.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import pro.juego.Ironfall.IronfallJuego;

public class PantallaVictoria implements Screen {

    private IronfallJuego game;
    private Stage stage;
    private Skin skin;

    public PantallaVictoria(IronfallJuego game) {
        this.game = game;
    }

    @Override
    public void show() {

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Label titulo = new Label("VICTORIA", skin);
        titulo.setFontScale(2f);
        titulo.setPosition(
                Gdx.graphics.getWidth() / 2f - titulo.getWidth(),
                Gdx.graphics.getHeight() / 2f + 80
        );

        TextButton btnMenu = new TextButton("VOLVER AL MENÚ", skin);
        btnMenu.setSize(220, 60);
        btnMenu.setPosition(
                Gdx.graphics.getWidth() / 2f - 110,
                Gdx.graphics.getHeight() / 2f - 40
        );

        btnMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.setInputProcessor(null);
                game.setScreen(new MenuPrincipal(game));
            }
        });

        stage.addActor(titulo);
        stage.addActor(btnMenu);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0.2f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
