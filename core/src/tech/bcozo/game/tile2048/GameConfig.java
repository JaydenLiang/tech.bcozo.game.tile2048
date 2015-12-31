/**
 * 
 */
package tech.bcozo.game.tile2048;

/**
 * <p>
 * Javadoc description
 * </p>
 * 
 * @ClassName: GameConfig
 * @author Jayden Liang
 * @version 1.0
 * @date Dec 19, 2015 8:11:11 PM
 */
public class GameConfig {
    public static final float WORLD_WIDTH = 480f;
    public static final float WORLD_HEIGHT = 640f;
    public static final float FRAME_DURATION = 0.01F;
    public static final String TILE_TEXTURE_PATH = "tilefont02.png";
    // region definitions file
    public static final String TILE_TEXTURE_REGSET = "tilefont02_regset.txt";
    public static final int FOREGROUND_MASK_POSX = 35;
    public static final int FOREGROUND_MASK_POSY = 33;
    public static final int GAME_OVER_IDLE_TIME = 10;
    public static final int GAME_ADD_TILES_ON_DEMO = 4;
    // add how many tiles at maximum on each move
    public static final int GAME_ADD_TILES_ON_MOVE = 2;
    public static final int TILE_WIDTH = 90;
    public static final int TILE_HEIGHT = 90;
    public static final int TILE_GAP_WIDTH = 10;
    public static final int TILE_COLUMNS = 4;
    public static final int TILE_ROWS = 4;
    public static final int TILE_MOVE_SPEED = 600;
}
