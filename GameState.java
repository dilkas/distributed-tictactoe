import java.io.Serializable;

public class GameState implements Serializable {

    /** Serial version UID to avoid warning. */
    private static final long serialVersionUID = 53L;

    private enum Sign {
        EMPTY, X, O;
    };

    private Sign[] grid;
    public Sign nextSign;

    public GameState() {
        nextSign = Sign.X;
        grid = new Sign[9];
        for (int i = 0; i < 9; i++)
            grid[i] = Sign.EMPTY;
    }

    /** Updates the state with the new play. Returns true if the game is over. */
    public boolean makePlay(int location) {
        grid[location] = nextSign;
        if (gameOver())
            return true;
        nextSign = (nextSign == Sign.X) ? Sign.O : Sign.X;
        return false;
    }

    /** Is this a valid play? */
    public boolean valid(int play) {
        return play >= 0 && play < 9 && grid[play] == Sign.EMPTY;
    }

    private boolean winningRow(int a, int b, int c) {
        return grid[a] == grid[b] && grid[b] == grid[c] && grid[a] != Sign.EMPTY;
    }

    /** Checks if the game is over */
    private boolean gameOver() {
        for (int i = 0; i < 3; i++) {
            if (winningRow(3 * i, 3 * i + 1, 3 * i + 2))
                return true; // rows
            if (winningRow(i, i + 3, i + 6))
                return true; // columns
        }
        return winningRow(0, 4, 8) || winningRow(2, 4, 6); // diagonals
    }

    @Override
    public String toString() {
        String representation = "";
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int index = 3 * i + j;
                if (grid[index] == Sign.EMPTY)
                    representation += index + " ";
                else
                    representation += grid[index].name() + " ";
            }
            representation += "\n";
        }
        return representation;
    }
}
