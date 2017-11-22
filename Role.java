import java.rmi.RemoteException;

/** State design pattern */
public abstract class Role {

    // Leaders use this variable to store the enemy leader,
    // regular players use this to store their leader
    protected GameInt leader;

    /** Returns true if the added player is a leader */
    public abstract GameInt addPlayer(GameInt player) throws RemoteException;

    /** A leader overrides this, a regular player does nothing */
    public void broadcastPlay(int play) throws RemoteException {
    }

    public void setLeader(GameInt somePlayer) {
        leader = somePlayer;
    }

    public abstract void schedule();

    /** Regular players ignore this. */
    public void turnStarts() throws RemoteException {
    }
}
