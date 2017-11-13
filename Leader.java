import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Leader extends Role {

    // The local Game instance is here as well.
    // Synchronize whenever using this list.
    private List<GameInt> myTeam;

    public Leader(List<GameInt> team) {
        myTeam = team;
    }

    public synchronized boolean addPlayer(GameInt player) throws RemoteException {
        if (leader == null) {
            leader = player;
            return true;
        }
        myTeam.add(player);
        return false;
    }

    public void turnStarts() throws RemoteException {
        // Gather the votes
        int[] votes = new int[9];
        synchronized (this) {
            // TODO: make this asynchronous (important)
            for (GameInt player : myTeam)
                votes[player.yourTurn()]++;
        }

        // Choose the winning play, breaking ties randomly
        List<Integer> maxIndices = new ArrayList<>();
        int maxVotes = 0;
        for (int i = 0; i < 9; i++) {
            if (votes[i] > maxVotes) {
                maxIndices = new ArrayList<>();
                maxVotes = votes[i];
            }
            if (votes[i] == maxVotes)
                maxIndices.add(i);
        }
        int play = maxIndices.get(new Random().nextInt(maxIndices.size()));

        leader.broadcastPlay(play);
        broadcastPlay(play);

        // Necessary because of the current implementation for ending the game.
        // Might be unnecessary in the future.
        if (leader != null)
            leader.turnStarts();
    }

    @Override
    public synchronized void broadcastPlay(int play) throws RemoteException {
        // TODO: treat winning and losing differently
        // TODO: (optional) start a new game or something?
        // FIXME: regular players don't exit
        boolean gameOver = false;
        for (GameInt player : myTeam)
            gameOver = player.makePlay(play);
        if (gameOver) {
            leader = null;
            if (myTeam.size() > 1)
                for (GameInt player : myTeam.subList(1, myTeam.size() - 1))
                    player.endGame();

            // End the local game last. TODO: I don't thing this matters. Test and simplify if possible.
            myTeam.get(0).endGame();
            myTeam = null;
        }
    }
}
