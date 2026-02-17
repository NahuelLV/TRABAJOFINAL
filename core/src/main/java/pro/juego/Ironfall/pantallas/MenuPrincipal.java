package pro.juego.Ironfall.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import pro.juego.Ironfall.IronfallJuego;
import pro.juego.Ironfall.enums.ModoJuego;

public class MenuPrincipal implements Screen {

    private IronfallJuego game;

    private Stage stage;
    private Skin skin;

    // 🔒 FLAG CRÍTICO: evita múltiples cambios de pantalla
    private boolean cambiandoPantalla = false;

    public MenuPrincipal(IronfallJuego game) {
        this.game = game;
    }

    @Override
    public void show() {

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // =========================
        // BOTÓN 1 JUGADOR
        // =========================
        TextButton btn1Jugador = new TextButton("1 JUGADOR", skin);
        btn1Jugador.setSize(240, 60);
        btn1Jugador.setPosition(
                Gdx.graphics.getWidth() / 2f - 120,
                Gdx.graphics.getHeight() / 2f + 40
        );

        btn1Jugador.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                if (cambiandoPantalla) return;
                cambiandoPantalla = true;

                Gdx.input.setInputProcessor(null);

                game.setScreen(new PantallaJuego(game, ModoJuego.UN_JUGADOR));
            }
        });

        // =========================
        // BOTÓN 2 JUGADORES LOCAL
        // =========================
        TextButton btn2Jugadores = new TextButton("2 JUGADORES LOCAL", skin);
        btn2Jugadores.setSize(240, 60);
        btn2Jugadores.setPosition(
                Gdx.graphics.getWidth() / 2f - 120,
                Gdx.graphics.getHeight() / 2f - 40
        );

        btn2Jugadores.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                if (cambiandoPantalla) return;
                cambiandoPantalla = true;

                Gdx.input.setInputProcessor(null);

                game.setScreen(new PantallaJuego(game, ModoJuego.DOS_JUGADORES_LOCAL));
            }
        });

        stage.addActor(btn1Jugador);
        stage.addActor(btn2Jugadores);
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
