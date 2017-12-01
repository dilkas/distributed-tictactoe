import java.rmi.RemoteException;
import java.util.TimerTask;

/** Task scheduled by the timer to start an election */
public class ElectionRaiser extends TimerTask {

    private GameInt client;

    public ElectionRaiser(GameInt client) {
        this.client = client;
    }

    public void run() {
        try {
            client.startElection();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
