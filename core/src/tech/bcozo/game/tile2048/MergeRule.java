/**
 * 
 */
package tech.bcozo.game.tile2048;

/**
 * <p>
 * Javadoc description
 * </p>
 * 
 * @ClassName: MergeRule
 * @author Jayden Liang
 * @version 1.0
 * @date Dec 31, 2015 12:43:17 AM
 */
public class MergeRule {
    public MergeRule() {

    }

    public static boolean canMergeOnSameNumber(Tile tileA, Tile tileB) {
        return tileA != null && tileB != null && tileA.getNumber()
                .getNumber() == tileB.getNumber().getNumber();
    }
}
