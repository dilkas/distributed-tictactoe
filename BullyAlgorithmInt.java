import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BullyAlgorithmInt extends Remote{

    // Leaders of each team will be the oldest players joined the game
    // If player not oldest:
    //       if timeoutElection then consider itself coordinator
    //       if timeoutCoordinator then start another election
    public int timeoutElection = 10, timeoutCoordinator = 10;

    public void startElection() throws RemoteException;

    public void responseToElection() throws RemoteException;

    public void announceCoordinator() throws RemoteException;

}