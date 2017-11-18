import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicIntegerArray;

/** Gets a vote from a player. */
class VoteThread extends Thread {
    private GameInt player;
    private AtomicIntegerArray votes;

    public VoteThread(GameInt player, AtomicIntegerArray votes) {
        this.player = player;
        this.votes = votes;
    }

    public void run() {
        try {
            votes.getAndIncrement(player.askForInput());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
