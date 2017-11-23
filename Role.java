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

    // Temporary for conflicting Leader call constructor
    public Role(PriorityBlockingQueue<GameInt> team){
        this.team = team;
    }

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

    /** A leader overrides this, a regular player does nothing */
    public void broadcastPlay(int play) throws RemoteException {
    }

    /** Regular players ignore this. */
    public void turnStarts() throws RemoteException {
    }

    // False indicates leader has no "greater" leaders
    // True indicates leader has started another election
    // No response assume other process crashed -- Use timeout when calling it
    public boolean startElection(GameInt myGameInt) throws RemoteException {
        // Start an election to my team
        GameInt[] myTeam = (GameInt[]) this.team.toArray();
        ArrayList<Boolean> responses = new ArrayList<>();
        responses.add(false);                                // No other leader
        int currentGameInt = 0;

        while(myGameInt.hashCode() < myTeam[currentGameInt].hashCode() && (currentGameInt<myTeam.length)){
            boolean anotherLeaderExists = responses.remove(0);
            responses.add(myTeam[currentGameInt].startElection(myTeam[currentGameInt]) && anotherLeaderExists);
            currentGameInt++;
        }
        // No more leaders set me as leader to the rest and the leader
        if (responses.get(0) == false){
            for (GameInt player: myTeam) {
                player.declareLeader(myGameInt);
            }
            opponentLeader.declareLeader(myGameInt);
        }

        return responses.get(0);
    }


    public void declareLeader(GameInt leader) throws RemoteException {
        this.leader = leader;
    }

}
