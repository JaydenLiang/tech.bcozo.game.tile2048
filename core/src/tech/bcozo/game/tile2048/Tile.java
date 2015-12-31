/**
 * 
 */
package tech.bcozo.game.tile2048;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import tech.bcozo.game.tools.logic.ICallbackHandler;

/**
 * <p>
 * Javadoc description
 * </p>
 * 
 * @ClassName: Tile
 * @author Jayden Liang
 * @version 1.0
 * @date Dec 25, 2015 6:44:53 PM
 */
public class Tile extends Actor {
    private Grid grid;
    private Grid toGrid;
    private static int tileID = 0;
    private static int count = 0;
    private int id;
    private static Texture texture;
    private static TextureRegion[] textureRegions;
    private static TileNumbers[] tileNumbers;
    private TileNumbers number;
    private boolean moving;
    private boolean moveComplete;
    private Tile mergeWith;

    private ICallbackHandler onMoveCompleteCallbackHandler;
    private ICallbackHandler onMergeCompleteCallbackHandler;

    /**
     * <p>
     * This is the constructor of Tile
     * </p>
     */
    public Tile(TileNumbers number) {
        if (textureRegions == null) {
            tileNumbers = TileNumbers.values();
            textureRegions = new TextureRegion[tileNumbers.length];
            texture = new Texture(
                    Gdx.files.internal(GameConfig.TILE_TEXTURE_PATH));
            String regionSettings[] =
                    Gdx.files.internal(GameConfig.TILE_TEXTURE_REGSET)
                            .readString().split(";");
            int index = 0;
            String regset[];
            for (TileNumbers tileNumber : tileNumbers) {
                index = tileNumber.ordinal();
                regset = regionSettings[index].split(",");
                textureRegions[index] = new TextureRegion(texture);
                textureRegions[index].setRegion(
                        Integer.parseInt(regset[0].trim()),
                        Integer.parseInt(regset[1].trim()),
                        Integer.parseInt(regset[2].trim()),
                        Integer.parseInt(regset[3].trim()));
            }
            setWidth(textureRegions[0].getRegionWidth());
            setHeight(textureRegions[0].getRegionHeight());
        }
        id = ++tileID;
        count++;
        this.number = number;
        this.grid = null;
        this.toGrid = null;
        onMoveCompleteCallbackHandler = null;
        onMergeCompleteCallbackHandler = null;
        moving = false;
        mergeWith = null;
    }

    public void dispose() {
        if (--count == 0) {
            for (int i = 0; i < textureRegions.length; i++) {
                textureRegions[i] = null;
            }
            textureRegions = null;
            texture.dispose();
            texture = null;
            tileNumbers = null;
        }
        clear();
    }

    public int getId() {
        return id;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        if (grid.getTile() == null && grid.getIncomingTile() == null) {
            this.grid = grid;
            setPosition(grid.getX(), grid.getY());
        }
    }

    public void clearAllGrids() {
        grid = null;
        toGrid = null;
        mergeWith = null;
    }

    public Grid getToGrid() {
        return toGrid;
    }

    public void setToGrid(Grid toGrid) {
        // if this tile is moving,
        // or toGrid is not null and it's not empty
        // or toGrid is not null and it's empty but has incoming tile
        // don't allow to do
        if (moving)
            return;
        // if (toGrid != null) {
        // if (toGrid.getTile() != null) {
        // if (toGrid.getTile().getToGrid() == null) {
        // return;
        // }
        // } else {
        // if (toGrid.getIncomingTile() != null)
        // return;
        // }
        // toGrid.setIncomingTile(this);
        // }
        this.toGrid = toGrid;
    }

    public void mergeToGrid(Grid grid) {
        if (grid == null)
            return;
        mergeWith = grid.getTile();
        if (mergeWith != null) {
            grid.setIncomingTile(this);
            this.toGrid = grid;
        }
    }

    public void mergeToGridWithIncomingTile(Grid grid) {
        if (grid == null)
            return;
        mergeWith = grid.getIncomingTile();
        if (mergeWith != null) {
            grid.setIncomingTile(this);
            this.toGrid = grid;
        }
    }

    public boolean isMoving() {
        return moving;
    }

    public TileNumbers getNumber() {
        return number;
    }

    public void setNumber(TileNumbers number) {
        this.number = number;
    }

    public Tile getMergeWith() {
        return mergeWith;
    }

    public void clearMergeWith() {
        mergeWith = null;
    }

    @Override
    public void clear() {
        number = null;
        // if (grid != null && grid.getTile() == this) {
        // grid.clearTile(this);
        // }
        // if (toGrid != null && grid.getIncomingTile() == this) {
        // toGrid.clearIncomingTile(this);
        // }
        grid = null;
        toGrid = null;
        onMoveCompleteCallbackHandler = null;
        onMergeCompleteCallbackHandler = null;
        moving = false;
        mergeWith = null;
        super.clear();
    }

    @Override
    public void act(float delta) {
        int dirX;
        int dirY;
        float nextX;
        float nextY;
        if (toGrid != null) {
            if (!moving) {
                moving = true;
                grid.moveCurrentTileOut();
            }
            if (toGrid == grid) {
                onMoveToGrid();
                return;
            }
            dirX = toGrid.getColumn() < grid.getColumn() ? -1 : 1;
            dirY = toGrid.getRow() < grid.getRow() ? -1 : 1;
            nextX = getX() + delta * GameConfig.TILE_MOVE_SPEED * dirX;
            nextY = getY() + delta * GameConfig.TILE_MOVE_SPEED * dirY;
            if (nextX != toGrid.getX() && (toGrid.getX() - nextX) * dirX < 0) {
                // if (toGrid.getX() - nextX > 30 || toGrid.getX() - nextX <
                // -30) {
                // nextX = toGrid.getX();
                // }
                nextX = toGrid.getX();
            }
            if (nextY != toGrid.getY() && (toGrid.getY() - nextY) * dirY < 0) {
                // if (toGrid.getY() - nextY > 30 || toGrid.getY() - nextY <
                // -30) {
                // nextY = toGrid.getY();
                // }
                nextY = toGrid.getY();
            }
            if (nextX == toGrid.getX() && nextY == toGrid.getY()) {
                onMoveToGrid();
            }
            setPosition(nextX, nextY);
            super.act(delta);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (number != null) {
            batch.draw(textureRegions[number.ordinal()], getX(), getY());
            super.draw(batch, parentAlpha);
        }
    }

    private void onMoveToGrid() {
        if (moving) {
            moving = false;
            moveComplete = true;
            // merge
            if (onMergeCompleteCallbackHandler != null) {
                if (mergeWith != null && toGrid.getTile() == mergeWith) {
                    try {
                        onMergeCompleteCallbackHandler.callback(this);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            if (onMoveCompleteCallbackHandler != null) {
                try {
                    onMoveCompleteCallbackHandler.callback(this);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void onMoveComplete(ICallbackHandler callbackHandler) {
        onMoveCompleteCallbackHandler = callbackHandler;
    }

    public void onMergeComplete(ICallbackHandler callbackHandler) {
        onMergeCompleteCallbackHandler = callbackHandler;
    }

    public void setMergeWith(Tile mergeWith) {
        this.mergeWith = mergeWith;
    }

    @Override
    public String toString() {
        String msg = "Tile[id:" + id + ", number:" + number.getNumber();
        if (grid != null) {
            msg += ", at:" + grid;
        }
        if (toGrid != null) {
            msg += ", to:" + toGrid;
        }
        if (mergeWith != null) {
            msg += ", mergeWith:" + mergeWith.getId();
        }
        return msg + "]";
    }
}
