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
    private int row;
    private int column;
    private int x;
    private int y;
    private Tile tile;
    private Tile incomingTile;

    /**
     * <p>
     * This is the constructor of Grid
     * </p>
     */
    public Grid(int row, int column, int x, int y) {
        this.row = row;
        this.column = column;
        this.x = x;
        this.y = y;
        tile = null;
        incomingTile = null;
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
        if (this.tile == null && this.incomingTile == null) {
            this.tile = tile;
            tile.setPosition(getX(), getY());
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
        if (cleaner != null && cleaner.getGrid() == this) {
            tile = null;
        }
    }

    public Tile getIncomingTile() {
        return incomingTile;
    }

    public void setIncomingTile(Tile incomingTile) {
        // make sure there is only one tile is moving to this grid at a time
        if (this.incomingTile == null) {
            this.incomingTile = incomingTile;
        }
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
        if (cleaner != null && cleaner.getToGrid() == this) {
            incomingTile = null;
        }
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void clear() {
        this.tile = null;
        this.incomingTile = null;
    }
}
