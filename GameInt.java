import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameInt extends Remote {

    public void addPlayer(GameInt player) throws RemoteException;

    public void yourTurn() throws RemoteException;

    public void makePlay(int location) throws RemoteException;
}
