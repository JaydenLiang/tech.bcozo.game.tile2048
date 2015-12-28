package tech.bcozo.game.tile2048.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import tech.bcozo.game.tile2048.GameConfig;
import tech.bcozo.game.tile2048.Tile2048;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = (int) GameConfig.WORLD_WIDTH;
        config.height = (int) GameConfig.WORLD_HEIGHT;
        new LwjglApplication(new Tile2048(), config);
    }
}
