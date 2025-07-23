package pro.juego.Ironfall.lwjgl3;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import pro.juego.Ironfall.Ironfalljuego;

public class DesktopLuncher {
	
	    public static void main (String[] arg) {
	        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
	        config.setTitle("Ironfall");
	        config.setWindowedMode(1280, 720);
	        config.setForegroundFPS(60);
	        config.setResizable(false);
	        new Lwjgl3Application(new Ironfalljuego(), config);
	    }
	}

