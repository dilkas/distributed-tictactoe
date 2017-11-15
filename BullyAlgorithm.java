import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BullyAlgorithm extends Remorte{

    // Leaders of each team will be the oldest players joined the game
    // If player not oldest:
    //       if timeoutElection then consider itself coordinator
    //       if timeoutCoordinator then start another election
    public int timeoutElection, timeoutCoordinator;

    public void startElection() throws RemoteException;

    public void responseToElection() throws RemorteException;

    public void announceCoordinator() throws RemorteException;

}