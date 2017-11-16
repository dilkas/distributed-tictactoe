import java.util.Comparator;

public class GameIntComparator implements Comparator<GameInt> {
    @Override
    public int compare(GameInt first, GameInt second) {
        return first.hashCode() - second.hashCode();
    }
}
