/**
 * 
 */
package tech.bcozo.game.tile2048;

import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import tech.bcozo.game.tools.console.Controller;
import tech.bcozo.game.tools.logic.ICallbackHandler;

/**
 * <p>
 * Javadoc description
 * </p>
 * 
 * @ClassName: GameScreen
 * @author Jayden Liang
 * @version 1.0
 * @date Dec 25, 2015 3:35:13 PM
 */
public class GameScreen extends ScreenAdapter {
    private static final int RAND_MOVE_UP = 0;
    private static final int RAND_MOVE_DOWN = 1;
    private static final int RAND_MOVE_LEFT = 2;
    private static final int RAND_MOVE_RIGHT = 3;
    private Game game;
    private Stage stage;
    private float renderTimer;
    private float gameOverIdleTimer;
    private GameState gameState;
    private Controller moveDirection;
    private Group backgroundGroup;
    private Group textGroup;
    private Group tileGroup;
    private Group foregroundGroup;
    private Texture backgroundTexture;
    private Texture foregroundTexture;
    private Image backgroundImage;
    private Image foregroundImage;
    private TileGrids tileGrids;
    private ArrayList<Tile> demoTiles;
    private ArrayList<Tile> tiles;
    private ArrayList<Tile> freeTiles;

    private TileMoveCallbackHandler tileMoveCallbackHandler;
    private TileMergeCallbackHandler tileMergeCallbackHandler;

    /**
     * <p>
     * This is the constructor of GameScreen
     * </p>
     */
    public GameScreen(Game game) {
        this.game = game;
        renderTimer = 0;
        gameOverIdleTimer = 0;
        gameState = GameState.STOP;
        moveDirection = null;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        backgroundGroup = new Group();
        tileGroup = new Group();
        textGroup = new Group();
        foregroundGroup = new Group();

        backgroundTexture = new Texture(Gdx.files.internal("background01.png"));
        foregroundTexture = new Texture(Gdx.files.internal("foreground01.png"));

        backgroundImage = new Image(backgroundTexture);
        foregroundImage = new Image(foregroundTexture);

        tileGrids = new TileGrids(GameConfig.TILE_WIDTH, GameConfig.TILE_HEIGHT,
                GameConfig.TILE_GAP_WIDTH, GameConfig.TILE_COLUMNS,
                GameConfig.TILE_ROWS);

        demoTiles = new ArrayList<Tile>();
        tiles = new ArrayList<Tile>();
        freeTiles = new ArrayList<Tile>();

        tileMoveCallbackHandler = new TileMoveCallbackHandler();
        tileMergeCallbackHandler = new TileMergeCallbackHandler();
    }

    @Override
    public void show() {
        stage.getViewport().setScreenSize((int) GameConfig.WORLD_WIDTH,
                (int) GameConfig.WORLD_HEIGHT);
        stage.addActor(backgroundGroup);
        stage.addActor(textGroup);
        stage.addActor(tileGroup);
        stage.addActor(foregroundGroup);

        backgroundGroup.addActor(backgroundImage);
        foregroundGroup.addActor(foregroundImage);

        foregroundImage.setPosition(GameConfig.FOREGROUND_MASK_POSX,
                GameConfig.FOREGROUND_MASK_POSY);
        tileGrids.setPosition(GameConfig.FOREGROUND_MASK_POSX,
                GameConfig.FOREGROUND_MASK_POSY);
        showDemo();
        super.show();
    }

    @Override
    public void dispose() {
        Gdx.input.setInputProcessor(null);
        stage.dispose();
        backgroundGroup.clear();
        textGroup.clear();
        tileGroup.clear();
        foregroundGroup.clear();
        backgroundImage.clear();
        foregroundImage.clear();
        backgroundTexture.dispose();
        foregroundTexture.dispose();
        Tile tile;
        while (demoTiles.size() > 0) {
            tile = demoTiles.remove(demoTiles.size() - 1);
            tile.dispose();
        }
        demoTiles = null;
        while (tiles.size() > 0) {
            tile = tiles.remove(tiles.size() - 1);
            tile.dispose();
        }
        tiles = null;
        while (freeTiles.size() > 0) {
            tile = freeTiles.remove(freeTiles.size() - 1);
            tile.dispose();
        }
        freeTiles = null;
        tile = null;

        this.game = null;
        gameState = null;
        stage = null;
        backgroundGroup = null;
        textGroup = null;
        tileGroup = null;
        foregroundGroup = null;
        backgroundImage = null;
        foregroundImage = null;
        backgroundTexture = null;
        foregroundTexture = null;
        tileMergeCallbackHandler = null;
        super.dispose();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float delta) {
        boolean renderTimeout = false;
        Controller nextDirection = null;
        renderTimer += delta;
        if (renderTimer > 1) {
            renderTimer = 0;
            renderTimeout = true;
        }
        switch (gameState) {
        case STOP:
            nextDirection = checkInputDirection();
            if (nextDirection != null) {
                moveDirection = nextDirection;
                startGame();
            } else {
                if (renderTimeout) {
                    updateDemoLogic();
                }
            }
            break;
        case PLAYING:
            nextDirection = checkInputDirection();
            if (nextDirection != null) {
                moveDirection = nextDirection;
                updateGameLogic();
            }
            break;
        case MOVING:
            checkMoveComplete();
            break;
        case GAME_OVER:
            if (renderTimeout) {
                if (++gameOverIdleTimer >= GameConfig.GAME_OVER_IDLE_TIME) {
                    stopGame();
                }
            }
            break;
        default:
            break;
        }
        stage.act(delta);
        stage.draw();
    }

    private Controller checkInputDirection() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            return Controller.UP;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            return Controller.RIGHT;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            return Controller.DOWN;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            return Controller.LEFT;
        } else {
            return null;
        }
    }

    private void showDemo() {
        gameState = GameState.STOP;
        Tile tile;
        while (demoTiles.size() < GameConfig.GAME_ADD_TILES_ON_DEMO) {
            tile = new Tile(TileNumbers.TWO);
            tile.onMoveComplete(new DemoTileMoveCallbackHandler());
            demoTiles.add(tile);
        }
        for (Tile demoTile : demoTiles) {
            tileGroup.addActor(demoTile);
            while (true) {
                Grid grid = tileGrids.getGrid(
                        MathUtils.random(GameConfig.TILE_ROWS - 1),
                        MathUtils.random(GameConfig.TILE_COLUMNS - 1));
                if (grid.getTile() != null)
                    continue;
                tileGrids.addTileToGrid(demoTile, grid.getRow(),
                        grid.getColumn());
                break;
            }
        }
    }

    private void updateDemoLogic() {
        for (Tile tile : demoTiles) {
            if (tile.getToGrid() == null) {
                randomMove(tile);
            }
        }
    }

    private void randomMove(Tile tile) {
        boolean canMove;
        int rand;
        Grid next = tile.getGrid();
        boolean canMoveUp = true;
        boolean canMoveRight = true;
        boolean canMoveDown = true;
        boolean canMoveLeft = true;
        if (next == null)
            return;
        do {
            canMove = true;
            rand = MathUtils.random(RAND_MOVE_RIGHT);
            switch (rand) {
            case RAND_MOVE_UP:
                next = tileGrids.getTopNextAvailableGrid(tile.getGrid());
                if (next == null || next == tile.getGrid()
                        || (next.hasTile() && !next.hasMovingOutTile())
                        || next.hasIncomingTile()) {
                    canMoveUp = canMove = false;
                }
                break;
            case RAND_MOVE_RIGHT:
                next = tileGrids.getRightNextAvailableGrid(tile.getGrid());
                if (next == null || next == tile.getGrid()
                        || (next.hasTile() && !next.hasMovingOutTile())
                        || next.hasIncomingTile()) {
                    canMoveRight = canMove = false;
                }
                break;
            case RAND_MOVE_DOWN:
                next = tileGrids.getBottomNextAvailableGrid(tile.getGrid());
                if (next == null || next == tile.getGrid()
                        || (next.hasTile() && !next.hasMovingOutTile())
                        || next.hasIncomingTile()) {
                    canMoveDown = canMove = false;
                }
                break;
            case RAND_MOVE_LEFT:
                next = tileGrids.getLeftNextAvailableGrid(tile.getGrid());
                if (next == null || next == tile.getGrid()
                        || (next.hasTile() && !next.hasMovingOutTile())
                        || next.hasIncomingTile()) {
                    canMoveLeft = canMove = false;
                }
                break;
            }
            if (!(canMoveUp || canMoveRight || canMoveDown || canMoveLeft))
                return;
        } while (!canMove);
        tile.setToGrid(next);
        next.setIncomingTile(tile);
    }

    private void startGame() {
        for (Tile tile : demoTiles) {
            tileGroup.removeActor(tile);
            tile.clear();
        }
        demoTiles.clear();
        tileGrids.clear();
        gameState = GameState.PLAYING;
        addNewTiles();
    }

    private void stopGame() {
        Tile removedTile;
        while (tiles.size() > 0) {
            removedTile = tiles.remove(tiles.size() - 1);
            tileGroup.removeActor(removedTile);
            if (removedTile.getGrid() != null) {
                removedTile.getGrid().clear();
            }
            removedTile.dispose();
        }
        while (freeTiles.size() > 0) {
            removedTile = freeTiles.remove(freeTiles.size() - 1);
            if (removedTile.getGrid() != null) {
                removedTile.getGrid().clear();
            }
            removedTile.dispose();
        }
        showDemo();
    }

    private void resetGame() {

    }

    private void updateGameLogic() {
        int row;
        int col;
        boolean canMove = false;
        Grid fromGrid;
        switch (moveDirection) {
        case UP:
            // scan from top to bottom
            for (row = tileGrids.getRows() - 1; row >= 0; row--) {
                // scan from left to right
                for (col = 0; col < tileGrids.getColumns(); col++) {
                    fromGrid = tileGrids.getGrid(row, col);
                    if (fromGrid.getTile() != null
                            && updateMoveTileLogic(fromGrid) && !canMove) {
                        canMove = true;
                    }
                }
            }
            break;
        case RIGHT:
            // scan from right to left
            for (col = tileGrids.getColumns() - 1; col >= 0; col--) {
                // scan from bottom to top
                for (row = 0; row < tileGrids.getRows(); row++) {
                    fromGrid = tileGrids.getGrid(row, col);
                    if (fromGrid.getTile() != null
                            && updateMoveTileLogic(fromGrid) && !canMove) {
                        canMove = true;
                    }
                }
            }
            break;
        case DOWN:
            // scan from bottom to top
            for (row = 0; row < tileGrids.getRows(); row++) {
                // scan from left to right
                for (col = 0; col < tileGrids.getColumns(); col++) {
                    fromGrid = tileGrids.getGrid(row, col);
                    if (fromGrid.getTile() != null
                            && updateMoveTileLogic(fromGrid) && !canMove) {
                        canMove = true;
                    }
                }
            }
            break;
        case LEFT:
            // scan from left to right
            for (col = 0; col < tileGrids.getColumns(); col++) {
                // scan from bottom to top
                for (row = 0; row < tileGrids.getRows(); row++) {
                    fromGrid = tileGrids.getGrid(row, col);
                    if (fromGrid.getTile() != null
                            && updateMoveTileLogic(fromGrid) && !canMove) {
                        canMove = true;
                    }
                }
            }
            break;
        case DOWNLEFT:
            break;
        case DOWNRIGHT:
            break;
        case UPLEFT:
            break;
        case UPRIGHT:
            break;
        default:
            break;
        }
        if (canMove) {
            gameState = GameState.MOVING;
        } else {
            addNewTiles();
        }
    }

    public static boolean canMerge(Tile tileA, Tile tileB) {
        return tileA != null && tileB != null
                && tileA.getNumber() == tileB.getNumber();
    }

    private boolean addNewTiles() {
        int count = 0;
        int row = 0;
        int col = 0;
        int newPos = 0;
        int maxPos;
        ArrayList<Integer> positions = new ArrayList<Integer>();
        if (moveDirection == Controller.UP
                || moveDirection == Controller.DOWN) {
            maxPos = GameConfig.TILE_COLUMNS;
        } else {
            maxPos = GameConfig.TILE_ROWS;
        }
        for (int i = 0; i < maxPos; i++) {
            positions.add(i);
        }
        Grid grid;
        Tile tile;
        boolean canAdd = false;
        while (count < GameConfig.GAME_ADD_TILES_ON_MOVE
                && positions.size() > 0) {
            switch (moveDirection) {
            case UP:
                row = 0;
                col = MathUtils.random(GameConfig.TILE_COLUMNS - 1);
                newPos = col;
                break;
            case RIGHT:
                row = MathUtils.random(GameConfig.TILE_ROWS - 1);
                col = 0;
                newPos = row;
                break;
            case DOWN:
                row = GameConfig.TILE_ROWS - 1;
                col = MathUtils.random(GameConfig.TILE_COLUMNS - 1);
                newPos = col;
                break;
            case LEFT:
                row = MathUtils.random(GameConfig.TILE_ROWS - 1);
                col = GameConfig.TILE_COLUMNS - 1;
                newPos = row;
                break;
            case DOWNLEFT:
                break;
            case DOWNRIGHT:
                break;
            case UPLEFT:
                break;
            case UPRIGHT:
                break;
            default:
                break;
            }
            if (positions.contains(newPos)) {
                positions.remove(positions.indexOf(newPos));
                grid = tileGrids.getGrid(row, col);
                if (grid.getTile() == null) {
                    tile = allocTile();
                    // initially set the number of the new tile
                    tile.setNumber(TileNumbers.TWO);
                    tile.onMoveComplete(tileMoveCallbackHandler);
                    tile.onMergeComplete(tileMergeCallbackHandler);
                    tiles.add(tile);
                    tileGrids.addTileToGrid(tile, row, col);
                    tileGroup.addActor(tile);
                    count++;
                    canAdd = true;
                }
            }
        }
        return canAdd;
    }

    private Tile allocTile() {
        if (freeTiles.size() > 0) {
            return freeTiles.remove(freeTiles.size() - 1);
        } else {
            return new Tile(TileNumbers.TWO);
        }
    }

    private void checkMoveComplete() {
        boolean moving = false;
        for (Tile tile : tiles) {
            if (tile.isMoving()) {
                moving = true;
                break;
            }
        }
        // on tiles move complete
        if (!moving) {
            // try add new tiles
            if (!addNewTiles()) {
                gameOverIdleTimer = 0;
                gameState = GameState.GAME_OVER;
            } else {
                gameState = GameState.PLAYING;
            }
        }
    }

    /**
     * <p>
     * update the logic of moving a tile from <b>grid</b> to the next grid in
     * the game move direction where possible.
     * </p>
     * 
     * @param grid
     * @return boolean
     */
    public boolean updateMoveTileLogic(Grid grid) {
        Grid prev = grid;
        Grid current;
        Grid next;
        boolean canMove = false;
        switch (moveDirection) {
        case UP:
            current = tileGrids.getTopNextAvailableGrid(grid);
            break;
        case RIGHT:
            current = tileGrids.getRightNextAvailableGrid(grid);
            break;
        case DOWN:
            current = tileGrids.getBottomNextAvailableGrid(grid);
            break;
        case LEFT:
            current = tileGrids.getLeftNextAvailableGrid(grid);
            break;
        default:
            return canMove;
        }
        if (current == null)
            return false;
        while (true) {
            // is there a tile in this grid and not about to move out?
            if (current.hasTile() && !current.hasMovingOutTile()) {
                break;
                // is other tile moving in this grid?
            } else if (current.hasIncomingTile()) {
                break;
            }
            switch (moveDirection) {
            case UP:
                next = tileGrids.getTopNextAvailableGrid(current);
                break;
            case RIGHT:
                next = tileGrids.getRightNextAvailableGrid(current);
                break;
            case DOWN:
                next = tileGrids.getBottomNextAvailableGrid(current);
                break;
            case LEFT:
                next = tileGrids.getLeftNextAvailableGrid(current);
                break;
            default:
                return canMove;
            }
            if (next == null) {
                break;
            } else {
                prev = current;
                current = next;
                next = null;
            }
        }
        // after the while loop above, there must be a tile in the 'current'
        // grid in two cases:
        // another tile is merging in/another tile is coming in/not moving out

        // if 'current' grid has a merging in tile
        if (current.hasMergingInTile()) {
            // not move in it but move in 'prev'
            // 'prev' is empty (because it is checked as 'can get through'
            grid.getTile().setToGrid(prev);
            prev.setIncomingTile(grid.getTile());
            canMove = true;
        }
        // if 'current' grid has an incoming tile
        else if (current.hasIncomingTile()) {
            // can merge with the incoming tile in the target grid?
            if (MergeRule.canMergeOnSameNumber(grid.getTile(),
                    current.getIncomingTile())) {
                grid.getTile().setToGrid(current);
                current.setMergeIn(grid.getTile());
                grid.getTile().setMergeWith(current.getIncomingTile());
                canMove = true;
            } else {
                // not move in it but move in 'prev'
                // 'prev' is empty (because it is checked as 'can get through'
                grid.getTile().setToGrid(prev);
                prev.setIncomingTile(grid.getTile());
                canMove = true;
            }
        } else {
            if (current.hasTile()) {
                // can merge with the tile in the target grid?
                if (MergeRule.canMergeOnSameNumber(grid.getTile(),
                        current.getTile())) {
                    grid.getTile().setToGrid(current);
                    current.setMergeIn(grid.getTile());
                    grid.getTile().setMergeWith(current.getTile());
                    canMove = true;
                } else {
                    // move into the 'prev' grid
                    grid.getTile().setToGrid(prev);
                    prev.setIncomingTile(grid.getTile());
                    canMove = true;
                }
            } else {
                // move into the current grid
                grid.getTile().setToGrid(current);
                current.setIncomingTile(grid.getTile());
                canMove = true;
            }
        }
        return canMove;
    }

    private class DemoTileMoveCallbackHandler implements ICallbackHandler {

        @Override
        public void callback(Object callObject) throws Exception {
            if (!(callObject instanceof Tile)) {
                return;
            }
            Tile tile = (Tile) callObject;
            Grid grid = tile.getGrid();
            Grid toGrid = tile.getToGrid();
            // unlink the grid and this tile
            // if (grid != null && grid.getTile() != tile) {
            // throw new GridTileNotMatchException(toGrid, tile,
            // "Cannot Clear In-Grid State");
            // }
            // grid.clearTile(tile);
            grid.clearMovingOutTile();
            // reset the incoming tile for the toGrid
            if (toGrid != null && toGrid.hasIncomingTile()
                    && toGrid.getIncomingTile() != tile) {
                throw new GridTileNotMatchException(toGrid, tile,
                        "Cannot Clear Incoming-Grid State");
            }
            toGrid.clearIncomingTile(tile);
            // link this tile and the toGrid
            // update the tile
            tile.setGrid(toGrid);
            tile.setToGrid(null);
            // update the grid
            toGrid.setTile(tile);
            tile.setNumber(TileNumbers.getNumberByOrdinal(
                    MathUtils.random(TileNumbers.values().length - 1)));
        }

    }

    private class TileMoveCallbackHandler implements ICallbackHandler {

        @Override
        public void callback(Object callObject) throws Exception {
            if (!(callObject instanceof Tile)) {
                return;
            }
            Tile tile = (Tile) callObject;
            Grid grid = tile.getGrid();
            Grid toGrid = tile.getToGrid();
            // unlink the grid and this tile
            // if (grid != null && grid.getTile() != tile) {
            // throw new GridTileNotMatchException(toGrid, tile,
            // "Cannot Clear In-Grid State");
            // }
            // grid.clearTile(tile);
            grid.clearMovingOutTile();
            // reset the incoming tile for the toGrid
            if (toGrid != null) {
                if (toGrid.hasIncomingTile()
                        && toGrid.getIncomingTile() != tile) {
                    throw new GridTileNotMatchException(toGrid, tile,
                            "Cannot Clear Incoming-Grid State");
                } else {
                    toGrid.clearIncomingTile(tile);
                }
            }
            // link this tile and the toGrid
            // update the tile
            tile.setGrid(toGrid);
            tile.setToGrid(null);
            // update the grid
            toGrid.setTile(tile);
        }

    }

    private class TileMergeCallbackHandler implements ICallbackHandler {

        @Override
        public void callback(Object callObject) throws Exception {
            if (!(callObject instanceof Tile)) {
                return;
            }
            Tile tile = (Tile) callObject;
            Tile mergeWith = tile.getMergeWith();
            Grid toGrid = tile.getToGrid();
            TileNumbers addNumber;
            if (mergeWith == null) {
                // tile.setMergeWith(null);
                return;
            }
            addNumber = TileNumbers.getNumber(tile.getNumber().getNumber()
                    + mergeWith.getNumber().getNumber());
            // clear and recycle the merged tile
            mergeWith.clear();
            // reset the merging in tile for the toGrid
            if (toGrid != null && toGrid.getMergeInTile() != tile) {
                throw new GridTileNotMatchException(toGrid, tile,
                        "Cannot Clear Merge-In-Grid State");
            }
            toGrid.clear();
            tileGroup.removeActor(mergeWith);
            tiles.remove(mergeWith);
            freeTiles.add(mergeWith);
            // add the number to the tile
            if (addNumber != null) {
                tile.setNumber(addNumber);
            }
            tile.clearMergeWith();
        }

    }

    private class GridTileNotMatchException extends Exception {
        private static final long serialVersionUID = -1424249667250825460L;
        private Grid grid;
        private Tile tile;

        public GridTileNotMatchException(Grid grid, Tile tile, String msg) {
            super(msg);
            this.grid = grid;
            this.tile = tile;
        }

        @Override
        public String getMessage() {
            // TODO Auto-generated method stub
            return super.getMessage() + " at " + grid + ", with " + tile;
        }
    }

    private enum GameState {
        STOP, START, PLAYING, GAME_OVER, MOVING
    }
}
