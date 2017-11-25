import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class Leader extends Role {

    public Leader(GameInt game) {
        super(game);
    }

    /** Add a new player to the game. Make it into the enemy leader if there is no enemy leader, otherwise add it to
        the team. Return true in the former case and false in the latter. */
    public boolean addPlayer(GameInt player) throws RemoteException {
        if (game.getOpponentLeader() == null) {
            game.setOpponentLeader(player);
            return true;
        }
        for (GameInt teamMember : game.getTeam())
            teamMember.addToTeam(player);
        return false;
    }

    /** Collect the votes from the team and decide on a play. */
    public void turnStarts() throws RemoteException {
        AtomicIntegerArray votes = new AtomicIntegerArray(9);
        int counter = 0;
        VoteThread[] voteCollectors;
        PriorityBlockingQueue<GameInt> team = game.getTeam();
        int teamSize = team.size();
        voteCollectors = new VoteThread[teamSize];
        for (GameInt player : team)
            voteCollectors[counter++] = new VoteThread(player, votes);

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

        // Broadcast the play. If the game is not over, start a new turn
        GameInt opponentLeader = game.getOpponentLeader();
        opponentLeader.broadcastPlay(play);
        if (!broadcastPlay(play))
            opponentLeader.turnStarts();
    }

    @Override
    public boolean broadcastPlay(int play) throws RemoteException {
        boolean gameOver = false;
        for (GameInt player : game.getTeam())
            gameOver = player.makePlay(play);
        return gameOver;
    }

	public void schedule() {
		// Schedule Timer to record last response of each regular player on team
		// If idle for more than x minutes, delete from team and inform other members to remove reference
	}
}
