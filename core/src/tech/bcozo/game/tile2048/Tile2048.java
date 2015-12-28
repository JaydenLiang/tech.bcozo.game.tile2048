package tech.bcozo.game.tile2048;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Tile2048 extends Game {
    SpriteBatch batch;
    Texture img;

    @Override
    public void create() {
        setScreen(new GameScreen(this));
    }
}
