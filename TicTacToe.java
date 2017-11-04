import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TicTacToe {

    private static final String HOST = "localhost";
    private static final int PORT = 1099;

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Usage: java TicTacToe <myName> <otherName> <otherHost>");
            // TODO: could explain the arguments in more detail
            System.exit(-1);
        }

        String myUrl = "//" + HOST + ":" + PORT + "/" + args[0];
        GameInt game = new Game();
        Naming.rebind(myUrl, game);

        try {
            GameInt server = (GameInt) Naming.lookup("//" + args[2] + ":" + PORT + "/" + args[1]);
            game.addPlayer(server);
            server.addPlayer(game);
            game.yourTurn();
        } catch (NotBoundException e) {} // do nothing: this is player 1
    }
}
