import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameInt extends Remote {

    public boolean addPlayer(GameInt player) throws RemoteException;

    public int yourTurn() throws RemoteException;

    public boolean makePlay(int play) throws RemoteException;

    public void broadcastPlay(int play) throws RemoteException;

    public GameState getGameState() throws RemoteException;

    public void setLeader() throws RemoteException;

//    public void setLeader(GameInt leader,GameInt opponent) throws RemoteException;

    public void turnStarts() throws RemoteException;

    public void printBoard() throws RemoteException;

    public void endGame() throws RemoteException;




    /**
     *
     *  Bully Algorithm Interface
     *
     * */

    // Leaders of each team will be the oldest players joined the game
    // If player not oldest:
    //       if timeoutElection then consider itself coordinator
    //       if timeoutCoordinator then start another election
    public int timeoutCoordinator = 10;


    // Use this to start an election from oldest processes than you
    // Response is used for the timeout in the calling process
    public boolean startElection(GameInt thisGameInt) throws RemoteException;


    // Use this to broadcast to all the processes in your team & opponent leader , about the new leader
    public void declareLeader(GameInt leader) throws RemoteException;

}
