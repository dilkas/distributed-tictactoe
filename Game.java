import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Game extends UnicastRemoteObject implements GameInt {

    /** Serial version UID to avoid warning. */
    private static final long serialVersionUID = 42L;
    /** Address of host machine. */
    private static final String HOST = "localhost";
    /** Port number of the game. */
    private static final int PORT = 1099;

    /** Holds the combined IP, port and server/client name address of this instance. */
    private String myUrl;
    private Scanner input;
    private GameState gameState;
    /** Identifies the role of this instance. */
    private Role role;


    /** Constructors */
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

    /** Ask the user to make a play and return it. */
    public int askForInput() throws RemoteException {
        int play = -1;
        while (!gameState.valid(play)) {
            System.out.print("Your sign: " + gameState.nextSign + ". Choose a number in [0, 8]: ");
            play = input.nextInt();
        }
        return play;
    }

    /** Receive a play and update the game state. Return true if the game is over. */
    public boolean makePlay(int play) throws RemoteException {
        boolean gameOver = gameState.makePlay(play);
        System.out.println(gameState);
        return gameOver;
    }

    /** End the game. */
    public void endGame() throws RemoteException {
        input.close();
        try {
            Naming.unbind(myUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        UnicastRemoteObject.unexportObject(this, true);
        System.out.println("Game over.");
    }

    /** Paste the current game on screen. */
    public void printBoard() throws RemoteException {
        System.out.println(gameState);
    }

    /** Accessor methods */
    public GameState getGameState() throws RemoteException {
        return gameState;
    }

    /** Role access classes */
    public boolean addPlayer(GameInt player) throws RemoteException {
        return role.addPlayer(player);
    }

    public void setLeader(GameInt somePlayer) throws RemoteException {
        role.setLeader(somePlayer);
    }

    public void turnStarts() throws RemoteException {
        role.turnStarts();
    }

    public void broadcastPlay(int play) throws RemoteException {
        role.broadcastPlay(play);
    }

    /** Make this instance into a leader and initialise the team list. */
    public void setLeader(List<GameInt> team) throws RemoteException {
        this.role = new Leader(team);
        System.out.println("I am a leader now.");
    }

    /** Make this instance into a leader of a brand new game. */
    public void setLeader() throws RemoteException {
        List<GameInt> initialTeam = new LinkedList<>();
        initialTeam.add(this);
        setLeader(initialTeam);
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

        try {
            GameInt server = (GameInt) Naming.lookup("//" + args[2] + ":" + PORT + "/" + args[1]);
            game = new Game(myUrl, server.getGameState());
            Naming.rebind(myUrl, game);
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
