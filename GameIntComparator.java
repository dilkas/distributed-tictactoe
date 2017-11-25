import java.io.Serializable;
import java.util.Comparator;

public class GameIntComparator implements Comparator<GameInt>, Serializable {

    /** Serial version UID to avoid warning. */
    private static final long serialVersionUID = 64L;

    @Override
    public int compare(GameInt first, GameInt second) {
        return first.hashCode() - second.hashCode();
    }
}
