import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class Leader extends Role {

    // The local Game instance is here as well.
    // Synchronise whenever using this list.
    // TODO: use a data structure that support concurrency and remove all the 'synchronized' keywords
    private List<GameInt> myTeam;

    public Leader(List<GameInt> team) {
        myTeam = team;
    }

    /** Adds a new player to the game. Returns null if the new player becomes the enemy leader and the  */
    public synchronized GameInt addPlayer(GameInt player) throws RemoteException {
        if (leader == null) {
            leader = player;
            return null;
        }
        myTeam.add(player);
        return myTeam.get(0);
    }

    /** Collect the votes from the team and decide on a play. */
    public void turnStarts() throws RemoteException {
        AtomicIntegerArray votes = new AtomicIntegerArray(9);
        int counter = 0;
        VoteThread[] voteCollectors;
        synchronized (this) {
            int teamSize = myTeam.size();
            voteCollectors = new VoteThread[teamSize];
            for (GameInt player : myTeam)
                voteCollectors[counter++] = new VoteThread(player, votes);
        }

        for (VoteThread thread : voteCollectors)
            thread.start();

        for (VoteThread thread : voteCollectors) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Choose the winning play, breaking ties randomly
        List<Integer> maxIndices = new ArrayList<>();
        int maxVotes = 0;
        for (int i = 0; i < 9; i++) {
            if (votes.get(i) > maxVotes) {
                maxIndices = new ArrayList<>();
                maxVotes = votes.get(i);
            }
            if (votes.get(i) == maxVotes)
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

	public void schedule() {
		// Schedule Timer to record last response of each regular player on team
		// If idle for more than x minutes, delete from team and inform other members to remove reference
	}
}
