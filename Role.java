import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.PriorityBlockingQueue;

/** State design pattern */
public abstract class Role {

    protected GameInt game;

    public Role(GameInt game) {
        this.game = game;
    }

    public abstract void schedule();

    /** Returns a reference to the leader of the team or null if the added player is the new leader */
    public abstract boolean addPlayer(GameInt player) throws RemoteException;

    // Default implementations for regular players - avoids testing whether an instance is a leader or not

    public boolean broadcastPlay(int play) throws RemoteException {
        return false;
    }

    public void turnStarts() throws RemoteException {
    }

    public void cancelTimer() {
    }

}
