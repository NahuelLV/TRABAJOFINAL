package pro.juego.Ironfall;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import pro.juego.Ironfall.pantallas.MenuPrincipal;

public class IronfallJuego extends Game {

    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new MenuPrincipal(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}