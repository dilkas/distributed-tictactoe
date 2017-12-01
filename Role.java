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

    /** Return true if the added player becomes a leader */
    public abstract boolean addPlayer(GameInt player) throws RemoteException;

  // Default empty implementations so that we don't have to check whether an instance of Role is actually
  // Player or Leader

    public boolean broadcastPlay(int play) throws RemoteException {
        return false;
    }

    public void turnStarts() throws RemoteException {
    }

    public void cancelTimer() {
    }
  
    public void schedule() {
    }

}
