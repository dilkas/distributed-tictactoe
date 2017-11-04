import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class Game extends UnicastRemoteObject implements GameInt {

    private GameState gameState;
    private GameInt otherPlayer;
    private Scanner input; // TODO: should we close it at some point?

    public Game() throws RemoteException {
        this(new GameState());
    }

    public Game(GameState gameState) throws RemoteException {
        this.gameState = gameState;
        input = new Scanner(System.in);
    }

    public void addPlayer(GameInt player) throws RemoteException {
        otherPlayer = player;
    }

    /** Asks the user to make a play, records it, and sends it to the other player. Only called locally. */
    public void yourTurn() throws RemoteException {
        int play = -1;
        while (!gameState.valid(play)) {
            System.out.println(gameState);
            System.out.print("Choose a number in [0, 8]: ");
            play = input.nextInt();
        }
        boolean gameOver = gameState.makePlay(play);
        if (gameOver) { // if the game is over
            System.out.println("Congratulations! You won!");
            otherPlayer.makePlay(play); // we still have to send our play to the opponent
            return;
        }
        System.out.println("Waiting for the opponent...");
        otherPlayer.makePlay(play);
    }

    /** Receives a play from the opponent */
    public void makePlay(int location) throws RemoteException {
        // the correctness of the play is already checked by the other process
        // but (TODO) need to check if it's their turn
        if (gameState.makePlay(location)) { // if the game is over
            System.out.println("Oh no! You lost.");
            return;
        }
        yourTurn();
    }
}
