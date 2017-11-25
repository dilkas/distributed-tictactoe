import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.PriorityBlockingQueue;

public interface GameInt extends Remote {

    public boolean addPlayer(GameInt player) throws RemoteException;

    public int askForInput() throws RemoteException;

    public boolean makePlay(int play) throws RemoteException;

    public void broadcastPlay(int play) throws RemoteException;

    public GameState getGameState() throws RemoteException;

    public GameInt getOpponentLeader() throws RemoteException;

    public void setOpponentLeader(GameInt leader) throws RemoteException;

    public GameInt getLeader() throws RemoteException;

    public void setLeader(GameInt leader) throws RemoteException;

    public void setLeader() throws RemoteException;

    public PriorityBlockingQueue<GameInt> getTeam() throws RemoteException;

    public void initialiseTeam() throws RemoteException;

    public void setTeam(PriorityBlockingQueue<GameInt> team) throws RemoteException;

    public void addToTeam(GameInt player) throws RemoteException;

    public void setAsPlayer() throws RemoteException;

    public void turnStarts() throws RemoteException;

    public void printBoard() throws RemoteException;

    public boolean startElection(GameInt thisGameInt) throws RemoteException;
}
