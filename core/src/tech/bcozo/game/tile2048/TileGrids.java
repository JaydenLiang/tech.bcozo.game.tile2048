/**
 * 
 */
package tech.bcozo.game.tile2048;

import tech.bcozo.game.tools.console.Controller;

/**
 * <p>
 * Javadoc description
 * </p>
 * 
 * @ClassName: TileGrids
 * @author Jayden Liang
 * @version 1.0
 * @date Dec 25, 2015 10:11:50 PM
 */
public class TileGrids {
    private int x;
    private int y;
    private int gridWidth;
    private int gridHeight;
    private int gapWidth;
    private int columns;
    private int rows;
    private int size;
    private int top;
    private int bottom;
    private int left;
    private int right;

    private Grid[][] grids;

    /**
     * <p>
     * This is the constructor of TileGrids
     * </p>
     */
    public TileGrids(int gridWidth, int gridHeight, int gapWidth, int columns,
            int rows) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.gapWidth = gapWidth;
        this.columns = columns;
        this.rows = rows;
        this.x = 0;
        this.y = 0;
        this.top =
                y + (this.gridHeight + this.gapWidth) * columns + this.gapWidth;
        this.bottom = y;
        this.left = x;
        this.right =
                x + (this.gridWidth + this.gapWidth) * rows + this.gapWidth;
        grids = new Grid[rows][columns];
        for (int i = 0; i < grids.length; i++) {
            for (int j = 0; j < grids[i].length; j++) {
                grids[i][j] = new Grid(i, j, 0, 0);
            }
        }
        size = columns * rows;
    }

    private void updateGridsPosition() {
        for (int i = 0; i < grids.length; i++) {
            for (int j = 0; j < grids[i].length; j++) {
                grids[i][j].setPosition(
                        x + this.gapWidth
                                + (this.gridWidth + this.gapWidth) * j,
                        y + this.gapWidth
                                + (this.gridHeight + this.gapWidth) * i);
            }
        }
    }

    public int getTop() {
        return top;
    }

    public int getBottom() {
        return bottom;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public int getSize() {
        return size;
    }

    public Grid[] getGridsAtRow(int row) {
        if (row >= 0 && row < rows) {
            return grids[row];
        } else
            return null;
    }

    public Grid[] getGridsAtColumn(int col) {
        if (col >= 0 && col < columns) {
            Grid[] atCol = new Grid[rows];
            for (int i = 0; i < rows; i++) {
                atCol[i] = grids[i][col];
            }
            return atCol;
        } else
            return null;
    }

    public void clear() {
        for (int i = 0; i < grids.length; i++) {
            for (int j = 0; j < grids[i].length; j++) {
                grids[i][j].clear();
            }
        }
    }

    public void dispose() {
        for (int i = 0; i < grids.length; i++) {
            for (int j = 0; j < grids[i].length; j++) {
                grids[i][j].clear();
                grids[i][j] = null;
            }
        }
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        updateGridsPosition();
    }

    public Controller checkOnEdge(Tile tile) {
        if (tile.getGrid().getColumn() == 0) {
            if (tile.getGrid().getColumn() == 0)
                return Controller.UPLEFT;
            else if (tile.getGrid().getRow() == rows - 1)
                return Controller.DOWNLEFT;
            else
                return Controller.LEFT;
        } else if (tile.getGrid().getColumn() == columns - 1) {
            if (tile.getGrid().getRow() == 0)
                return Controller.UPRIGHT;
            else if (tile.getGrid().getRow() == rows - 1)
                return Controller.DOWNRIGHT;
            else
                return Controller.RIGHT;
        } else if (tile.getGrid().getRow() == 0) {
            return Controller.UP;
        } else if (tile.getGrid().getRow() == rows - 1) {
            return Controller.DOWN;
        } else
            return null;
    }

    public Grid getGrid(int row, int column) {
        return grids[row][column];
    }

    public void addTileToGrid(Tile tile, int row, int column) {
        tile.setGrid(grids[row][column]);
        grids[row][column].setTile(tile);
    }

    /**
     * <p>
     * Get next grid above.<br>
     * Return:<br>
     * <b>next grid</b> - if there is a next one.<br>
     * <b>null</b> - if the grid is on the edge to the direction.
     * </p>
     * 
     * @param grid
     * @return Grid
     */
    public Grid getTopNextAvailableGrid(Grid grid) {
        if (grid.getRow() == rows - 1)
            return null;
        return getGrid(grid.getRow() + 1, grid.getColumn());
    }

    /**
     * <p>
     * Get next grid below.<br>
     * Return:<br>
     * <b>next grid</b> - if there is a next one.<br>
     * <b>null</b> - if the grid is on the edge to the direction.
     * </p>
     * 
     * @param grid
     * @return Grid
     */
    public Grid getBottomNextAvailableGrid(Grid grid) {
        if (grid.getRow() == 0)
            return null;
        return getGrid(grid.getRow() - 1, grid.getColumn());
    }

    /**
     * <p>
     * Get next grid on the left.<br>
     * Return:<br>
     * <b>next grid</b> - if there is a next one.<br>
     * <b>null</b> - if the grid is on the edge to the direction.
     * </p>
     * 
     * @param grid
     * @return Grid
     */
    public Grid getLeftNextAvailableGrid(Grid grid) {
        if (grid.getColumn() == 0)
            return null;
        return getGrid(grid.getRow(), grid.getColumn() - 1);
    }

    /**
     * <p>
     * <p>
     * Get next grid on the right.<br>
     * Return:<br>
     * <b>next grid</b> - if there is a next one.<br>
     * <b>null</b> - if the grid is on the edge to the direction.
     * </p>
     * </p>
     * 
     * @param grid
     * @return Grid
     */
    public Grid getRightNextAvailableGrid(Grid grid) {
        if (grid.getColumn() == columns - 1)
            return null;
        return getGrid(grid.getRow(), grid.getColumn() + 1);
    }
}
