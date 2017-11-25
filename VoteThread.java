import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicIntegerArray;

/** Gets a vote from a player. */
class VoteThread extends Thread {
    private Leader leader;
    private GameInt player;
    private AtomicIntegerArray votes;

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
