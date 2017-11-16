import java.rmi.RemoteException;
import java.util.concurrent.PriorityBlockingQueue;

public class Player extends Role {

    public Player(GameInt leader,GameInt opponentLeader,PriorityBlockingQueue<GameInt> team) {
        super(leader,opponentLeader,team);
    }

    public boolean addPlayer(GameInt player) throws RemoteException {
        leader.addPlayer(player);
        return false;
    }
}
