import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;


public class Leader extends Role {

    // The local Game instance is here as well.
    // Synchronize whenever using this list.
    // private List<GameInt> myTeam; --> moved to Role

    public Leader(PriorityBlockingQueue<GameInt> team) {
        this.team = team;
    }

    public Leader(GameInt leader,GameInt opponentLeader,PriorityBlockingQueue<GameInt> team) {
        super(leader,opponentLeader,team);
    }

    public synchronized boolean addPlayer(GameInt player) throws RemoteException {
        if (leader == null) {
            leader = player;
            return true;
        }
//        team.add(player);
        return false;
    }

    public void turnStarts() throws RemoteException {
        // Gather the votes
        int[] votes = new int[9];
        synchronized (this) {
            // TODO: make this asynchronous (important)
            for (GameInt player : team)
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
        for (GameInt player : team)
            gameOver = player.makePlay(play);
        if (gameOver) {
            leader = null;
            if (team.size() > 1)
//                for (GameInt player : team.subList(1, team.size() - 1))
                for (GameInt player : team)
                    player.endGame();

            // End the local game last. TODO: I don't thing this matters. Test and simplify if possible.
            team.poll().endGame();
            team = null;
        }
    }
}
