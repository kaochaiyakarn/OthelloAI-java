public class OthelloState {

    private byte board[][];
    private byte player;

    public OthelloState(){}

    public OthelloState(byte N, byte E, byte B, byte W) {
        this.board = new byte[N][N];
        this.board[N/2-1][N/2-1] = B;
        this.board[N/2][N/2] = B;
        this.board[N/2-1][N/2] = W;
        this.board[N/2][N/2-1] = W;
        this.player = B;
    }

    public OthelloState(int N, int E, int B, int W) {
        this((byte)N, (byte)E, (byte)B, (byte)W);
    }

    public byte[][] getBoard() { return this.board; }
    public byte getPlayer() { return this.player; }

    public OthelloState copy() {
        int N = this.board.length;
        OthelloState othelloState = new OthelloState();
        othelloState.board = new byte[N][N];
        for (int i = 0; i < N; i++){
            for (int j = 0; j < N; j++){
                othelloState.board[i][j] = this.board[i][j];
            }
        }
        othelloState.player = this.player;
        return othelloState;
    }

    public void togglePlayer(){
        player = (byte)((--player ^ 1 ) + 1);
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        int N = this.board.length;
        for (int i = 0; i < N; i++){
            for (int j = 0; j < N; j++){
                out.append(this.board[i][j]);
                out.append(' ');
            }
            out.append('\n');
        }
        return out.substring(0, out.length());
    }
}