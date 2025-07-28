package pro.juego.Ironfall.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import pro.juego.Ironfall.Ironfalljuego;

public class DesktopLauncher {
    public static void main(String[] arg) {
    	
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Ironfall");
        config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        config.setForegroundFPS(60);
        config.useVsync(true);
        new Lwjgl3Application(new Ironfalljuego(), config);
    }
}
