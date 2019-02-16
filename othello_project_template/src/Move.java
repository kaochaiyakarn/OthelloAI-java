public class Move{
    private byte r;
    private byte c;
    public Move(byte r, byte c){
        this.r = r;
        this.c = c;
    }
    public Move(int r, int c){
        this.r = (byte)r;
        this.c = (byte)c;
    }
    public byte row() { return this.r; }
    public byte col() { return this.c; }

    @Override
    public int hashCode(){
        // return this.toString().hashCode();
        int result = 17;
        return 31 * result + r * OthelloGame.N + c;
    }

    public boolean equals(Object o){
        if (o == this) { return true; }
        if (!(o instanceof Move)) { return false; }
        Move om = (Move) o;
        return this.r == om.r && this.c == om.c;
    }

    public String toString(){
        return "(" + r + ", " + c + ")";
    }
}