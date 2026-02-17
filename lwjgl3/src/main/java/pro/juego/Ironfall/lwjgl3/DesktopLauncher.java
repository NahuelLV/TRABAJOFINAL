package pro.juego.Ironfall.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import pro.juego.Ironfall.IronfallJuego;

public class DesktopLauncher {

    public static void main(String[] arg) {

        Lwjgl3ApplicationConfiguration config =
                new Lwjgl3ApplicationConfiguration();

        config.setTitle("Ironfall");
        config.setWindowedMode(1280, 720);
        config.useVsync(true);

        new Lwjgl3Application(new IronfallJuego(), config);
    }
}