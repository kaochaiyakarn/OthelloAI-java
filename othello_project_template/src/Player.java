import java.util.HashSet;

public abstract class Player {
    byte player;
    String division = "Tonkotsu Ramen";
    String team;

    public Player(byte player){
        this.team = "A Planet";
        this.player = player;
    }

    public byte getPlayer() { return this.player; }
    public String name() {
        return this.team + " " + this.division;
    }
    public abstract Move move(
        OthelloState state, HashSet<Move> legalMoves)  
        throws InterruptedException;

}