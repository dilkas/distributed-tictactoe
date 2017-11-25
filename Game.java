import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.PriorityBlockingQueue;

public class Game extends UnicastRemoteObject implements GameInt {

    /** Serial version UID to avoid warning. */
    private static final long serialVersionUID = 42L;
    /** Address of host machine. */
    private static final String HOST = "localhost";
    /** Port number of the game. */
    private static final int PORT = 1099;

    private GameInt leader;
    private GameInt opponentLeader;
    private PriorityBlockingQueue<GameInt> team;
    /** Holds the combined IP, port and server/client name address of this instance. */
    private String myUrl;
    private Scanner input;
    private GameState gameState;
    /** Identifies the role of this instance. */
    private Role role;

    /** Constructor for the first player */
    public Game(String url) throws RemoteException {
        this(url, new GameState());
        initialiseTeam();
    }

    /** Constructor for everyone else */
    public Game(String url, GameState gameState) throws RemoteException {
        input = new Scanner(System.in);
        myUrl = url;
        this.gameState = gameState;
    }

    /** Ask the user to make a play and return it. */
    public int askForInput() throws RemoteException {
        if(role instanceof Player)
            role.schedule();
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
        if (gameOver) {
            input.close();
            role.cancelTimer();
            try {
                Naming.unbind(myUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
            UnicastRemoteObject.unexportObject(this, true);
            System.out.println("Game over.");
        }
        return gameOver;
    }

    /** Paste the current game on screen. */
    public void printBoard() throws RemoteException {
        System.out.println(gameState);
    }

    /** Accessor methods */
    public GameState getGameState() throws RemoteException {
        return gameState;
    }

    public GameInt getOpponentLeader() throws RemoteException {
        return opponentLeader;
    }

    public void setOpponentLeader(GameInt leader) throws RemoteException {
        opponentLeader = leader;
    }

    public GameInt getLeader() throws RemoteException {
        return leader;
    }

    public void setLeader(GameInt leader) throws RemoteException {
        this.leader = leader;
    }

    public void setLeader() throws RemoteException {
        role = new Leader(this);
        leader = this;
        System.out.println("I am a leader now.");
    }

    public PriorityBlockingQueue<GameInt> getTeam() throws RemoteException {
        return team;
    }

    public void initialiseTeam() throws RemoteException {
        team = new PriorityBlockingQueue<GameInt>(11, new GameIntComparator());
        team.add(this);
    }

    public void setTeam(PriorityBlockingQueue<GameInt> team) throws RemoteException {
        this.team = team;
    }

    public void addToTeam(GameInt player) throws RemoteException {
        team.add(player);
    }

    /** Role access classes */
    public boolean addPlayer(GameInt player) throws RemoteException {
        return role.addPlayer(player);
    }

    public void turnStarts() throws RemoteException {
        role.turnStarts();
    }

    // False indicates leader has no "greater" leaders
    // True indicates leader has started another election
    // No response assume other process crashed -- Use timeout when calling it
    public boolean startElection(GameInt myGameInt) throws RemoteException {
        // Start an election to my team
        GameInt[] myTeam = (GameInt[]) team.toArray();
        ArrayList<Boolean> responses = new ArrayList<>();
        responses.add(false); // No other leader
        int currentGameInt = 0;

        while (myGameInt.hashCode() < myTeam[currentGameInt].hashCode() && currentGameInt < myTeam.length) {
            boolean anotherLeaderExists = responses.remove(0);
            responses.add(myTeam[currentGameInt].startElection(myTeam[currentGameInt]) && anotherLeaderExists);
            currentGameInt++;
        }

        // No more leaders set me as leader to the rest and the leader
        if (responses.get(0) == false) {
            for (GameInt player : myTeam)
                player.setLeader(myGameInt);
            opponentLeader.setLeader(myGameInt);
        }

        return responses.get(0);
    }

    public void broadcastPlay(int play) throws RemoteException {
        role.broadcastPlay(play);
    }

    /** Make this instance into a regular player and initialise the leader reference. */
    public void setAsPlayer() throws RemoteException {
        role = new Player(this);
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
            boolean meLeader = server.addPlayer(game); // am I a leader?
            GameInt leader = server.getLeader();
            System.out.println("Successfully connected to the server.");
            if (meLeader) {
                game.setLeader();
                game.setOpponentLeader(server.getLeader());
                game.initialiseTeam();
                game.printBoard();
                game.turnStarts();
            } else {
                game.setAsPlayer();
                game.setLeader(leader);
                game.setOpponentLeader(leader.getOpponentLeader());
                game.setTeam(leader.getTeam());
            }
        } catch (NotBoundException e) {
            // Failed to connect. Must be player 1.
            System.out.println("I am the first player.");
            game = new Game(myUrl);
            game.setLeader();
            game.initialiseTeam();
            Naming.rebind(myUrl, game);
        }
    }
}
