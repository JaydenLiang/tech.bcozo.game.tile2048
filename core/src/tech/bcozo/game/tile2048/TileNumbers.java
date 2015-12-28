/**
 * 
 */
package tech.bcozo.game.tile2048;

/**
 * <p>
 * Javadoc description
 * </p>
 * 
 * @ClassName: TileNumbers
 * @author Jayden Liang
 * @version 1.0
 * @date Dec 25, 2015 9:35:41 PM
 */
public enum TileNumbers {
    TWO(2), FOUR(4), EIGHT(8), SIXTEEN(16), THIRTY_TWO(32), SIXTY_FOUR(64),
    ONE_TWENTY_EIGHT(128), TWO_FIFTY_SIX(256), FIVE_TWELVE(512),
    TEN_TWENTY_FOUR(1024), TWENTY_FORTY_EIGHT(2048), FORTY_NINTY_SIX(4096),
    EIGHTY_ONE_NINTY_TWO(8192), ONE_HUNDRED_SIXTY_THREE_EIGHTY_FOUR(16384);
    private final int number;

    private TileNumbers(int number) {
        this.number = number;
    }

    public int getNumber() {
        return this.number;
    }

    public static TileNumbers getNumberByOrdinal(int ordinal) {
        TileNumbers[] values = values();
        if (ordinal < 0 || ordinal >= values.length) {
            return TWO;
        }
        return values[ordinal];
    }

    public static TileNumbers getNumber(int num) {
        TileNumbers[] values = values();
        for (TileNumbers tileNumbers : values) {
            if (tileNumbers.getNumber() == num)
                return tileNumbers;
        }
        return null;
    }
}
