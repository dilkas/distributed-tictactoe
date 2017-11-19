import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.PriorityBlockingQueue;

/** State design pattern */
public abstract class Role{

    protected GameInt leader;                          // Current Leader
    protected GameInt opponentLeader;                  // Current Opponent Leader
    protected PriorityBlockingQueue<GameInt> team;     // My Team

    /** Used by the subclasses */
    public Role(GameInt leader,GameInt opponentLeader,PriorityBlockingQueue<GameInt> team){
        this.leader = leader;
        this.opponentLeader = opponentLeader;
        this.team = team;
    }


    public void setLeader(GameInt somePlayer) {
        leader = somePlayer;
    }

    /** Returns true if the added player is a leader */
    public abstract boolean addPlayer(GameInt player) throws RemoteException;

    /** Returns true if the added player is an opponent leader*/
    public synchronized boolean addOpponent(GameInt player) throws RemoteException{
        if (opponentLeader == null) {
            opponentLeader = player;
            return true;
        }
        team.add(player);
        return false;
    }

    /** A leader overrides this, a regular player does nothing */
    public void broadcastPlay(int play) throws RemoteException {
    }

    /** Regular players ignore this. */
    public void turnStarts() throws RemoteException {
    }

    // False indicates leader has no "greater" leaders
    // True indicates leader has started another election
    // No response assume other process crashed -- Use timeout when calling it
    public boolean startElection() throws RemoteException {
        // Start an election to my team
        GameInt[] myTeam = (GameInt[]) this.team.toArray();
        ArrayList<Boolean> responses = new ArrayList<>();
        int currentGameInt = 0;

        while(this.hashCode() < myTeam[currentGameInt].hashCode() && (currentGameInt<myTeam.length)){
            responses.add(myTeam[currentGameInt].startElection());
            currentGameInt++;
        }
        // No more leaders set me as leader
        if (responses.size() == 0){
            return false;
        }
        return true;
    }


    public void declareLeader(GameInt leader) throws RemoteException {
        this.leader = leader;
    }
}
