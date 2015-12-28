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
        renderTimer += delta;
        Controller nextDirection = null;
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            nextDirection = Controller.UP;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            nextDirection = Controller.RIGHT;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            nextDirection = Controller.DOWN;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            nextDirection = Controller.LEFT;
        }
        switch (gameState) {
        case STOP:
            if (nextDirection != null) {
                moveDirection = nextDirection;
                startGame();
            } else {
                if (renderTimer > 1) {
                    renderTimer = 0;
                    updateDemoLogic();
                }
            }
            break;
        case GAME_OVER:
            if (renderTimer > 1) {
                renderTimer = 0;
                if (++gameOverIdleTimer >= GameConfig.GAME_OVER_IDLE_TIME) {
                    stopGame();
                }
            }
            break;
        case PLAYING:
            if (nextDirection != null) {
                moveDirection = nextDirection;
                updateGameLogic();
            }
            break;
        case MOVING:
            checkMoveComplete();
            break;
        default:
            break;
        }
        stage.act(delta);
        stage.draw();
    }

    private void stopGame() {
        for (Tile tile : tiles) {
            tile.clear();
        }
        Tile removedTile;
        while (tiles.size() > 0) {
            removedTile = tiles.remove(tiles.size() - 1);
            removedTile.dispose();
        }
        showDemo();
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
        //
        // tileGroup.addActor(randTile2);
        // tileGrids.addTileToGrid(randTile2, GameConfig.TILE_ROWS - 1,
        // GameConfig.TILE_COLUMNS - 1);
    }

    private void updateDemoLogic() {
        for (Tile tile : demoTiles) {
            if (tile.getToGrid() == null) {
                randomMove(tile);
            }
        }
    }

    private void resetGame() {

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
        // updateGameLogic();
    }

    private void endGame() {
        gameState = GameState.GAME_OVER;
        gameOverIdleTimer = 0;
    }

    private void updateGameLogic() {
        int row;
        int col;
        boolean canMove = false;
        Grid fromGrid;
        Grid toGrid;
        switch (moveDirection) {
        case UP:
            // scan from top to bottom
            for (row = tileGrids.getRows() - 1; row >= 0; row--) {
                // scan from left to right
                for (col = 0; col < tileGrids.getColumns(); col++) {
                    fromGrid = tileGrids.getGrid(row, col);
                    if (fromGrid.getTile() != null) {
                        toGrid = tileGrids.getTopMostAvailableGrid(fromGrid);
                        toGrid = updateMoveTileLogic(fromGrid, toGrid);
                        if (!canMove && (fromGrid.getTile() == toGrid
                                .getIncomingTile()
                                || fromGrid.getTile().getMergeWith() == toGrid
                                        .getTile())) {
                            canMove = true;
                        }
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
                    if (fromGrid.getTile() != null) {
                        toGrid = tileGrids.getRightMostAvailableGrid(fromGrid);
                        toGrid = updateMoveTileLogic(fromGrid, toGrid);
                        if (!canMove && fromGrid.getTile() == toGrid
                                .getIncomingTile()) {
                            canMove = true;
                        }
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
                    if (fromGrid.getTile() != null) {
                        toGrid = tileGrids.getBottomMostAvailableGrid(fromGrid);
                        toGrid = updateMoveTileLogic(fromGrid, toGrid);
                        if (!canMove && fromGrid.getTile() == toGrid
                                .getIncomingTile()) {
                            canMove = true;
                        }
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
                    if (fromGrid.getTile() != null) {
                        toGrid = tileGrids.getLeftMostAvailableGrid(fromGrid);
                        toGrid = updateMoveTileLogic(fromGrid, toGrid);
                        if (!canMove && fromGrid.getTile() == toGrid
                                .getIncomingTile()) {
                            canMove = true;
                        }
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

    /**
     * <p>
     * update the logic of moving a tile from <b>fromGrid</b> to <b>toGrid</b>
     * described as follow:<br>
     * 1.don't move if they are the same grid.<br>
     * 2.perform a simple movement if <b>toGrid</b> is valid to move into.<br>
     * 3.perform a movement with merge if <b>toGrid</b> has the same number of
     * <b>fromGrid</b>. <br>
     * 4.perform a simple movement to the grid next to <b>toGrid</b> on the same
     * direction.<br>
     * </p>
     * 
     * @param fromGrid
     * @param toGrid
     * @return Grid
     */
    private Grid updateMoveTileLogic(Grid fromGrid, Grid toGrid) {
        // if fromGrid is on the edge, do nothing to it.
        if (toGrid == fromGrid) {
            return toGrid;
        }
        // if toGrid doesn't contain a tile
        if (toGrid.getTile() == null) {
            // if no other tile will
            // move into it, move the tile from fromGrid to toGrid.
            if (toGrid.getIncomingTile() == null) {
                fromGrid.getTile().setToGrid(toGrid);
                return toGrid;
            } else {
                // if other tile will move into it
                if (canMerge(fromGrid.getTile(), toGrid.getIncomingTile())) {
                    fromGrid.getTile().mergeToGridWithIncomingTile(toGrid);
                    return toGrid;
                }

            }
        } else {
            // if toGrid contains a tile
            // but if no other tile will move(or merge) into the toGrid
            if (toGrid.getIncomingTile() == null) {
                // if the tile in toGrid is moving out, move the tile from
                // fromGrid to toGrid.
                if (toGrid.getTile().getToGrid() != null) {
                    fromGrid.getTile().setToGrid(toGrid);
                    return toGrid;
                } else if (canMerge(fromGrid, toGrid)) {
                    // if toGrid can be merged into
                    fromGrid.getTile().mergeToGrid(toGrid);
                    return toGrid;
                }
            } else {// if other tile is moving into the toGrid

            }
        }
        // if fromGrid cannot move into toGrid in any case above, try to
        // move to the grid before toGrid.
        // if the grid before toGrid is fromGrid itself, do nothing to it.
        // if (toGrid.getTile() == null || toGrid.getIncomingTile() == null) {
        int row = 0;
        int col = 0;
        switch (moveDirection) {
        case UP:
            row = toGrid.getRow() - 1;
            col = toGrid.getColumn();
            break;
        case RIGHT:
            row = toGrid.getRow();
            col = toGrid.getColumn() - 1;
            break;
        case DOWN:
            row = toGrid.getRow() + 1;
            col = toGrid.getColumn();
            break;
        case LEFT:
            row = toGrid.getRow();
            col = toGrid.getColumn() + 1;
            break;
        default:
            break;
        }
        Grid grid = tileGrids.getGrid(row, col);
        fromGrid.getTile().setToGrid(grid);
        return grid;
        // } else {
        // return toGrid;
        // }
    }

    private boolean canMerge(Grid fromGrid, Grid toGrid) {
        if (fromGrid == null || toGrid == null || fromGrid.getTile() == null
                || toGrid.getTile() == null)
            return false;
        if (toGrid.getIncomingTile() != null && toGrid.getTile() != null
                && toGrid.getIncomingTile().getMergeWith() == toGrid
                        .getTile()) {
            return false;
        }
        return canMerge(fromGrid.getTile(), toGrid.getTile());
    }

    public static boolean canMerge(Tile tileA, Tile tileB) {
        return tileA != null && tileB != null
                && tileA.getNumber() == tileB.getNumber();
    }

    private void addNewTiles() {
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
        if (!canAdd)
            endGame();
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
        if (!moving)
            onMoveComplete();
    }

    private void onMoveComplete() {
        // // check game over
        // if (tiles.size() == tileGrids.getSize()) {
        // gameState = GameState.GAME_OVER;
        // return;
        // }
        addNewTiles();
        gameState = GameState.PLAYING;
    }

    // private void move(Controller direction) {
    // Controller edge;
    // switch (direction) {
    // case UP:
    // for (Tile tile : tiles) {
    // edge = tileGrids.checkOnEdge(tile);
    // if (edge == Controller.UP || edge == Controller.UPLEFT
    // || edge == Controller.UPRIGHT)
    // continue;
    // else {
    // moveTile(tile, Controller.UP);
    // }
    // }
    // break;
    // case RIGHT:
    // for (Tile tile : tiles) {
    // edge = tileGrids.checkOnEdge(tile);
    // if (edge == Controller.RIGHT || edge == Controller.UPRIGHT
    // || edge == Controller.DOWNRIGHT)
    // continue;
    // else {
    // moveTile(tile, Controller.RIGHT);
    // }
    // }
    // break;
    // case DOWN:
    // for (Tile tile : tiles) {
    // edge = tileGrids.checkOnEdge(tile);
    // if (edge == Controller.DOWN || edge == Controller.DOWNRIGHT
    // || edge == Controller.DOWNLEFT)
    // continue;
    // else {
    // moveTile(tile, Controller.DOWN);
    // }
    // }
    // break;
    // case LEFT:
    // for (Tile tile : tiles) {
    // edge = tileGrids.checkOnEdge(tile);
    // if (edge == Controller.LEFT || edge == Controller.UPLEFT
    // || edge == Controller.DOWNLEFT)
    // continue;
    // else {
    // moveTile(tile, Controller.LEFT);
    // }
    // }
    // break;
    // default:
    // break;
    // }
    // }

    private void randomMove(Tile tile) {
        boolean canMove;
        int rand;
        Grid next = tile.getGrid();
        boolean canMoveUp = true;
        boolean canMoveRight = true;
        boolean canMoveDown = true;
        boolean canMoveLeft = true;
        do {
            canMove = true;
            rand = MathUtils.random(RAND_MOVE_RIGHT);
            switch (rand) {
            case RAND_MOVE_UP:
                next = tileGrids.getTopNextAvailableGrid(tile.getGrid());
                if (next == null || next == tile.getGrid()
                        || next.getTile() != null
                        || next.getIncomingTile() != null) {
                    canMoveUp = canMove = false;
                }
                break;
            case RAND_MOVE_RIGHT:
                next = tileGrids.getRightNextAvailableGrid(tile.getGrid());
                if (next == null || next == tile.getGrid()
                        || next.getTile() != null
                        || next.getIncomingTile() != null) {
                    canMoveRight = canMove = false;
                }
                break;
            case RAND_MOVE_DOWN:
                next = tileGrids.getBottomNextAvailableGrid(tile.getGrid());
                if (next == null || next == tile.getGrid()
                        || next.getTile() != null
                        || next.getIncomingTile() != null) {
                    canMoveDown = canMove = false;
                }
                break;
            case RAND_MOVE_LEFT:
                next = tileGrids.getLeftNextAvailableGrid(tile.getGrid());
                if (next == null || next == tile.getGrid()
                        || next.getTile() != null
                        || next.getIncomingTile() != null) {
                    canMoveLeft = canMove = false;
                }
                break;
            }
            if (!(canMoveUp || canMoveRight || canMoveDown || canMoveLeft))
                return;
        } while (!canMove);
        moveTile(tile, next);
    }

    private void moveTile(Tile tile, Grid grid) {
        tile.setToGrid(grid);
    }

    private void onTileMoveComplete(Tile tile) {
        Grid grid = tile.getGrid();
        Grid toGrid = tile.getToGrid();
        // unlink the grid and this tile
        if (grid != null && grid.getTile() == tile) {
            grid.clearTile(tile);
        }
        // reset the incoming tile for the toGrid
        if (toGrid != null && toGrid.getIncomingTile() == tile) {
            toGrid.clearIncomingTile(tile);
        }
        // link this tile and the toGrid
        if (toGrid != null) {
            // update the tile
            tile.setGrid(toGrid);
            tile.setToGrid(null);
            // update the grid
            toGrid.setTile(tile);
        }
    }

    private class DemoTileMoveCallbackHandler implements ICallbackHandler {

        @Override
        public void callback(Object callObject) {
            if (!(callObject instanceof Tile)) {
                return;
            }
            Tile tile = (Tile) callObject;
            onTileMoveComplete(tile);
            tile.setNumber(TileNumbers.getNumberByOrdinal(
                    MathUtils.random(TileNumbers.values().length - 1)));
        }

    }

    private class TileMoveCallbackHandler implements ICallbackHandler {

        @Override
        public void callback(Object callObject) {
            if (!(callObject instanceof Tile)) {
                return;
            }
            Tile tile = (Tile) callObject;
            onTileMoveComplete(tile);
        }

    }

    private class TileMergeCallbackHandler implements ICallbackHandler {

        @Override
        public void callback(Object callObject) {
            if (!(callObject instanceof Tile)) {
                return;
            }
            Tile tile = (Tile) callObject;
            Tile mergeWith = tile.getMergeWith();
            TileNumbers addNumber;
            if (mergeWith == null) {
                // tile.setMergeWith(null);
                return;
            }
            addNumber = TileNumbers.getNumber(tile.getNumber().getNumber()
                    + mergeWith.getNumber().getNumber());
            // clear and recycle the merged tile
            mergeWith.clear();
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

    private enum GameState {
        STOP, START, PLAYING, GAME_OVER, MOVING
    }
}
