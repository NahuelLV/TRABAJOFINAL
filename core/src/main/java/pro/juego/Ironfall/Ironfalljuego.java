package pro.juego.Ironfall;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import pantallas.MenuPrincipal;

public class Ironfalljuego  extends Game{
	public SpriteBatch batch;

	@Override
	public void create() {
		batch = new SpriteBatch();
		this.setScreen(new MenuPrincipal(this));
		
	}
	
	@Override
    public void dispose() {
        batch.dispose();
    }
	
}
