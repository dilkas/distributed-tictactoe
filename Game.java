import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Game extends UnicastRemoteObject implements GameInt {
    // TODO: Reorder the methods. Right now the order is chronological/arbitrary.
    // TODO: Make role public
    // TODO: More (and more accurate) comments

    private static final String HOST = "localhost";
    private static final int PORT = 1099;

    private String myUrl;
    private Scanner input;
    private GameState gameState;
    private Role role;

    public Game(String url) throws RemoteException {
        myUrl = url;
        input = new Scanner(System.in);
        gameState = new GameState();
        setLeader();
    }

    public Game(String url, GameState gameState) throws RemoteException {
        myUrl = url;
        input = new Scanner(System.in);
        this.gameState = gameState;
    }

    public boolean addPlayer(GameInt player) throws RemoteException {
        return role.addPlayer(player);
    }

    /** Asks the user to make a play and returns it. */
    public int yourTurn() throws RemoteException {
        // TODO: perhaps rename this function to something more appropriate
        int play = -1;
        while (!gameState.valid(play)) {
            System.out.print("Your sign: " + gameState.nextSign + ". Choose a number in [0, 8]: ");
            play = input.nextInt();
        }
        return play;
    }

    /** Receives a play and updates the game state. Returns true if the game is over. */
    public boolean makePlay(int play) throws RemoteException {
        boolean gameOver = gameState.makePlay(play);
        System.out.println(gameState);
        return gameOver;
    }

    public void endGame() throws RemoteException {
        input.close();
        try {
            Naming.unbind(myUrl);
        } catch (Exception e) {
            // Mainly for NotBoundException. Right now this clause is executed for every instance except the first one.
            // TODO: Rewrite it so that it's never executed.
        }
        UnicastRemoteObject.unexportObject(this, true);
        System.out.println("Game over.");
    }

    public void broadcastPlay(int play) throws RemoteException {
        role.broadcastPlay(play);
    }

    public GameState getGameState() throws RemoteException {
        return gameState;
    }

    /** Make this instance into a leader and initialise the team list */
    public void setLeader(List<GameInt> team) throws RemoteException {
        this.role = new Leader(team);
        System.out.println("I am a leader now.");
    }

    public void setLeader() throws RemoteException {
        List<GameInt> initialTeam = new LinkedList<>();
        initialTeam.add(this);
        setLeader(initialTeam);
    }

    public void setLeader(GameInt somePlayer) throws RemoteException {
        role.setLeader(somePlayer);
    }

    public void turnStarts() throws RemoteException {
        role.turnStarts();
    }

    public void printBoard() throws RemoteException {
        System.out.println(gameState);
    }

    public static void main(String[] args)throws IOException {
        if (args.length != 3) {
            System.out.println("Usage: java TicTacToe <myName> <otherName> <otherHost>\n");
            System.out.println("<myName> is my unique identifier");
            System.out.println("<otherName> is the unique identifier of the other game instance to connect to. If this");
            System.out.println("is the first game instance with nothing to connect to, what entered here doesn't matter.");
            System.out.println("<otherHost> is the hostname of the other game instance to connect to");
            System.exit(-1);
        }

        String myUrl = "//" + HOST + ":" + PORT + "/" + args[0];
        GameInt game;

        // TODO: If this instance successfully connects to a server, it cannot add new players. Perhaps it's not
        // necessary to fix this, but then addPlayer() functionality should be updated.
        try {
            GameInt server = (GameInt) Naming.lookup("//" + args[2] + ":" + PORT + "/" + args[1]);
            game = new Game(myUrl, server.getGameState());
            boolean leader = server.addPlayer(game); // am I a leader?
            System.out.println("Successfully connected to the server.");
            if (leader) {
                game.setLeader();
                game.addPlayer(server);
                game.printBoard();
                game.turnStarts();
            }
        } catch (NotBoundException e) {
            // Failed to connect. Must be player 1.
            System.out.println("I am the first player.");
            game = new Game(myUrl);
            Naming.rebind(myUrl, game);
        }
    }
}
