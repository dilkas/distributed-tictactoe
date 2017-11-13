import java.rmi.RemoteException;

public class Player extends Role {

    public Player(GameInt leader) {
        this.leader = leader;
    }

    public boolean addPlayer(GameInt player) throws RemoteException {
        leader.addPlayer(player);
        return false;
    }
}
