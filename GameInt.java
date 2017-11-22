import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameInt extends Remote {

    public GameInt addPlayer(GameInt player) throws RemoteException;

    public int askForInput() throws RemoteException;

    public boolean makePlay(int play) throws RemoteException;

    public void broadcastPlay(int play) throws RemoteException;

    public GameState getGameState() throws RemoteException;

    public void setLeader() throws RemoteException;

    public void setAsPlayer(GameInt leader) throws RemoteException;

    public void turnStarts() throws RemoteException;

    public void printBoard() throws RemoteException;

}
