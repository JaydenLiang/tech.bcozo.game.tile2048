/**
 * 
 */
package tech.bcozo.game.tile2048;

/**
 * <p>
 * Javadoc description
 * </p>
 * 
 * @ClassName: Grid
 * @author Jayden Liang
 * @version 1.0
 * @date Dec 25, 2015 11:56:35 PM
 */
public class Grid {
    private static int gridID = 0;
    private int id;
    private int row;
    private int column;
    private int x;
    private int y;
    private boolean checkIn;
    private boolean checkOut;
    private Tile tile;
    private Tile incomingTile;
    private Tile movingOutTile;
    private Tile mergeInTile;

    /**
     * <p>
     * This is the constructor of Grid
     * </p>
     */
    public Grid(int row, int column, int x, int y) {
        id = ++gridID;
        this.row = row;
        this.column = column;
        this.x = x;
        this.y = y;
        this.checkIn = false;
        this.checkOut = false;
        tile = null;
        incomingTile = null;
        movingOutTile = null;
        mergeInTile = null;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean hasTile() {
        return tile != null;
    }

    public Tile getTile() {
        return tile;
    }

    /**
     * <p>
     * Assign a tile to this grid. Only when this grid is empty and no other
     * tile is moving into this grid, it allows to assign a tile to this grid.
     * </p>
     * 
     * @param tile
     */
    public void setTile(Tile tile) {
        this.tile = tile;
        tile.setPosition(getX(), getY());
        if (incomingTile != null && this.tile == null) {
            System.out.println("[Error] Set tile when it's not empty.");
        }
    }

    /**
     * <p>
     * Clear the tile.<br>
     * Only the tile that is on this tile can perform this action.
     * </p>
     * 
     * @param cleaner The tile that tries to perform this action.
     */
    public void clearTile(Tile cleaner) {
        if (cleaner == null || tile == null
                || cleaner != null && tile == cleaner) {
            tile = null;
        } else {
            System.out.println("[Error] Clear tile by invalid cleaner.");
        }
    }

    public boolean hasIncomingTile() {
        return incomingTile != null;
    }

    public Tile getIncomingTile() {
        return incomingTile;
    }

    public void setIncomingTile(Tile incomingTile) {
        // make sure there is only one tile is moving to this grid at a time
        if (this.incomingTile != null) {
            System.out
                    .println("[Error] Set incoming tile when it's not empty.");
        }
        this.incomingTile = incomingTile;
    }

    /**
     * <p>
     * Clear the incoming tile from this grid.<br>
     * Only the tile that is coming into this grid can perform this action.
     * </p>
     * 
     * @param cleaner The tile that tries to perform this action.
     */
    public void clearIncomingTile(Tile cleaner) {
        if (cleaner == null || incomingTile == null
                || cleaner != null && incomingTile == cleaner) {
            incomingTile = null;
        } else {
            System.out.println(
                    "[Error] Clear  incoming tile by invalid cleaner.");
        }
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void clear() {
        tile = null;
        incomingTile = null;
        movingOutTile = null;
        mergeInTile = null;
    }

    public boolean hasMergingInTile() {
        return mergeInTile != null;
    }

    public Tile getMergeInTile() {
        return mergeInTile;
    }

    public void setMergeIn(Tile tile) {
        if (mergeInTile != null) {
            System.out
                    .println("[Error] Set merge in tile when it's not empty.");
        }
        mergeInTile = tile;
    }

    public void clearMerginIn(Tile cleaner) {
        if (cleaner == null || mergeInTile == null
                || cleaner != null && mergeInTile == cleaner) {
            mergeInTile = null;
        } else {
            System.out
                    .println("[Error] Clear merge in tile by invalid cleaner.");
        }
    }

    public boolean hasMovingOutTile() {
        if (movingOutTile != null) {
            return true;
        } else if (tile != null && tile.getToGrid() != null) {
            return true;
        } else
            return false;
    }

    public void moveCurrentTileOut() {
        movingOutTile = tile;
        tile = null;
    }

    public void clearMovingOutTile() {
        movingOutTile = null;
    }

    @Override
    public String toString() {
        return "Grid[ID: " + id + ", row: " + row + ", col:" + column + "]";
    }
}
