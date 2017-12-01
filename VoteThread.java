import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicIntegerArray;

/** Gets a vote from a player. */
class VoteThread extends Thread {
    private Leader leader; // The calling class. Also the class we contact if the player fails
    private GameInt player; // The player to get a vote from
    private AtomicIntegerArray votes; // A reference to data structure that saves votes from all players

    public VoteThread(Leader leader, GameInt player, AtomicIntegerArray votes) {
        this.leader = leader;
        this.player = player;
        this.votes = votes;
    }

    public void run() {
        try {
            votes.getAndIncrement(player.askForInput());
        } catch (RemoteException e) {
            try {
                leader.removeFromTeam(player);
            } catch (RemoteException f) {
                f.printStackTrace(); // the first exception is handled, this one is not
            }
        }
    }
}
