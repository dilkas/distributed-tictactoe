import java.rmi.RemoteException;
import java.util.concurrent.PriorityBlockingQueue;

/** State design pattern */
public abstract class Role {

    protected GameInt leader;                          // Current Leader
    protected GameInt opponentLeader;                  // Current Opponent Leader
    protected PriorityBlockingQueue<GameInt> team;     // My Team

    /** Used by the subclasses */
    public Role(GameInt leader,GameInt opponentLeader,PriorityBlockingQueue<GameInt> team){
        this.leader = leader;
        this.opponentLeader = opponentLeader;
        this.team = team;
    }

    /** Returns true if the added player is a leader */
    public abstract boolean addPlayer(GameInt player) throws RemoteException;

    /** A leader overrides this, a regular player does nothing */
    public void broadcastPlay(int play) throws RemoteException {
    }

    public void setLeader(GameInt somePlayer) {
        leader = somePlayer;
    }

    /** Regular players ignore this. */
    public void turnStarts() throws RemoteException {
    }
}
