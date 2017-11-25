import java.rmi.RemoteException;
import java.util.TimerTask;

/** Task that is scheduled by the timer */
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
