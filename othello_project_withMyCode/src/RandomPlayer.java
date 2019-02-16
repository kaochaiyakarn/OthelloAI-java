import java.util.HashSet;
import java.util.Random;

public class RandomPlayer extends Player {
    Random rand;
    public RandomPlayer(byte player) {
        super(player);
        this.team = "Pluto";
        this.rand = new Random();
    }

    @Override
    public Move move(OthelloState state, HashSet<Move> legalMoves) throws InterruptedException {
        Thread.sleep(500);
        return (Move) legalMoves.toArray()[rand.nextInt(legalMoves.size())];
	}
}