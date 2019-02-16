import java.util.HashSet;

public class OthelloGame {
    public final static byte E = 0;
    public final static byte B = 1;
    public final static byte W = 2;
    public final static byte L = 3;
    public final static byte N = 8;
    
    public static OthelloState transition(OthelloState state, Move move){
        byte[][] board = state.getBoard();
        byte player = state.getPlayer();
        if (!isLegalMove(board, move.row(), move.col(), player)) {
            return null;
        }
        
        OthelloState newState = state.copy();
        byte[][] newBoard = newState.getBoard();
        int x, y;
        byte r = move.row();
        byte c = move.col();
        for (int m = -1; m <= 1; m++) {
            for (int n = -1; n <= 1; n++) {
                y = r + m; 
                x = c + n;
                if (x < 0 || x >= N || y < 0 || y >= N || 
                    board[y][x] == 0 || board[y][x] == player) {
                    continue;
                }
                int i = r, j = c;
                for(int t = 0; t < N; t++) {
                    i += m;
                    j += n;
                    if (j < 0 || j >= N || i < 0 || i >= N || board[i][j] == 0) {
                        break;
                    }
                    if (board[i][j] == player) {
                        newBoard[r][c] = player;
                        for (int k = 1; k <= t; k++){
                            newBoard[r+m*k][c+n*k] = player;
                        }
                        break;
                    } else if (board[i][j] == 0) {
                        break;
                    }
                }
            }
        }
        newState.togglePlayer();
        return newState;
    }

    public static boolean isLegalMove(byte[][] board, byte r, byte c, byte player) {
        if (board[r][c] != E) {
            return false;
        }
        int x, y;
        //check adjacent positions
        for (int m = -1; m <= 1; m++) {
            for (int n = -1; n <= 1; n++) {
                y = r + m; 
                x = c + n;
                if (x < 0 || x >= N || y < 0 || y >= N || 
                    board[y][x] == 0 || board[y][x] == player) {
                    continue;
                }
                for(int __ = 0; __ < N; __++) {
                    y += m;
                    x += n;
                    if (x < 0 || x >= N || y < 0 || y >= N || board[y][x] == 0) {
                        break;
                    }
                    if (board[y][x] == player) {
                        return true;
                    } else if (board[y][x] == 0) {
                        break;
                    }
                }
            }
        }
        return false;
    }

    public static HashSet<Move> getAllLegalMoves(byte[][] board, byte player) {
        HashSet<Move> moves = new HashSet<>();
        for (byte r = 0; r < N; r++) {
            for (byte c = 0; c < N; c++){
                if (isLegalMove(board, r, c, player)) {
                    moves.add(new Move(r, c));
                }
            }
        }
        return moves;
    }

    public static int computeScore(byte[][] board, byte player) {
        int score = 0;
        for (byte r = 0; r < N; r++) {
            for (byte c = 0; c < N; c++){
                if (board[r][c] == player) { score++; }
            }
        }
        return score;
    }
}