
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.HashSet;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;




public class OthelloGUI extends JPanel {
    private static final long serialVersionUID = 1L;

    public OthelloPanel board;
    public StatusPanel status;
    public JPanel controlPanel;

    public OthelloGUI(OthelloState state) {
        this.setLayout(new FlowLayout());
        this.board = new OthelloPanel(
            OthelloGame.getAllLegalMoves(state.getBoard(), state.getPlayer()));
        this.add(this.board);
        this.controlPanel = new ControlPanel(Black.player.name(), White.player.name());
        this.add(this.controlPanel);
        this.status = new StatusPanel();
        this.add(this.status);
    }

    public Dimension getPreferredSize() {
        return new Dimension(600, 500);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    public class OthelloPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private Dimension dim;
        private byte[][] board;
        private OthelloPiece[][] grid;
        private Move[][] moves;
        public OthelloPanel(HashSet<Move> legalMoves) {
            byte N = OthelloGame.N;
            this.setLayout(new GridLayout(N, N, 1, 1));
            this.setBackground(new Color(70, 155, 45));
            this.dim = new Dimension(400, 400);
            this.setSize(this.dim);
            this.board = new byte[N][N];
            this.moves = new Move[N][N];
            this.grid = new OthelloPiece[OthelloGame.N][OthelloGame.N];
            for (byte i = 0; i < OthelloGame.N; i++) {
                for(byte j = 0; j < OthelloGame.N; j++) {
                    grid[i][j] = new OthelloPiece(this.board[i][j]);
                    moves[i][j] = new Move(i, j);
                    this.add(grid[i][j]);
                }
            }
            this.updateInfo(this.board, legalMoves);
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
        }

        public Dimension getPreferredSize() {
            return this.dim;
        }

        public void updateInfo(byte[][] board, HashSet<Move> legalMoves) {
            this.board = board;
            for (byte i = 0; i < OthelloGame.N; i++) {
                for(byte j = 0; j < OthelloGame.N; j++) {
                    // System.out.println(moves[i][j]);
                    // System.out.println(legalMoves);
                    // System.out.println("----------------");
                    if (this.board[i][j] == OthelloGame.E && 
                            legalMoves.contains(moves[i][j])) {
                        grid[i][j].updateInfo(OthelloGame.L);;
                    } else {
                        grid[i][j].updateInfo(this.board[i][j]);
                    }
                }
            }
        }
    }

    public class OthelloPiece extends JPanel {
        private static final long serialVersionUID = 1L;
        private Dimension dim;
        private Color color;
        public OthelloPiece(byte player) {
            this.setBackground(new Color(70, 155, 45));
            this.dim = new Dimension(49, 49);
            this.setSize(this.dim);
            updateInfo(player);
            this.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        }

        public void updateInfo(byte player) {
            if (player == OthelloGame.B) {
                this.color = Color.black;
            } else if (player == OthelloGame.W) {
                this.color = Color.white;
            } else if (player == OthelloGame.L) {
                this.color = Color.blue;
            } else {
                this.color = null;
            }
        }

        public Dimension getPreferredSize() {
            return this.dim;
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(this.color);
            if (this.color == Color.blue) {
                g.fillOval(20, 20, 10, 10);
            }
            else if (this.color != null) {
                g.fillOval(4, 4, 40, 40);
            }
        }
    }

    public class ControlPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private Dimension dim;
        JLabel blackLabel;
        JLabel whiteLabel;
        public ControlPanel(String blackName, String whiteName){
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.setBorder(BorderFactory.createLineBorder(Color.black, 1));
            this.dim = new Dimension(185, 400);
            this.add(new JLabel("<html>ITCS451 Othello AI<br/></html>"));
            blackLabel = new JLabel("<html>Black:<br/><p>"+blackName+"</p></html>");
            whiteLabel = new JLabel("<html>White:<br/><p>"+whiteName+"</p></html>");
            this.add(whiteLabel);
            this.add(blackLabel);
            
        }

        public Dimension getPreferredSize(){
            return this.dim;
        }
    }

    public class StatusPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private final Font playerFont = new Font("SanSerifs", Font.PLAIN, 20);
        private final Font scoreFont = new Font("SanSerifs", Font.BOLD, 40);
        private int blackScore = 2;
        private int whiteScore = 2;
        private int second = 0;
        private byte player = OthelloGame.W;
        private boolean over = false;
        private Dimension dim;
        public StatusPanel(){
            this.setBorder(BorderFactory.createLineBorder(Color.black, 1));
            this.dim = new Dimension(590, 65);
        }

        public Dimension getPreferredSize(){
            return this.dim;
        }

        public void updateInfo(
            byte player, int blackScore, int whiteScore, int second, boolean over) {
            this.player = player;
            this.blackScore = blackScore;
            this.whiteScore = whiteScore;
            this.over = over;
            this.second = second;
        }

        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g.setColor(Color.black);
            g.setFont(playerFont);
            g.drawString("BLACK", 10, 30);
            g.drawString("WHITE", 515, 30);
            if (!this.over) {
                String countDown = String.format(":%02d", this.second);
                if (this.player == OthelloGame.B) {
                    g.drawRect(5, 5, 155, 55);
                    g.drawString("turn", 20, 50);
                    g.drawString(countDown, 100, 40);
                } else if (this.player == OthelloGame.W) {
                    g.drawRect(428, 5, 155, 55);
                    g.drawString("turn", 525, 50);
                    g.drawString(countDown, 455, 40);
                }
            }
            g.setFont(scoreFont);
            String score = String.format(
                "%02d - %02d", this.blackScore, this.whiteScore).toString();
            g.drawString(score, 220, 45);
            if (this.over) {
                if (this.blackScore > this.whiteScore) {
                    g.setColor(new Color(70, 155, 45));
                    g.drawString("WIN", 120, 45);
                    g.setColor(Color.red);
                    g.drawString("LOSE", 390, 45);
                } else if (this.blackScore < this.whiteScore) {
                    g.setColor(Color.red);
                    g.drawString("LOSE", 100, 45);
                    g.setColor(new Color(70, 155, 45));
                    g.drawString("WIN", 400, 45);
                } else {
                    g.setColor(Color.gray);
                    g.drawString("DRAW", 90, 45);
                    g.drawString("DRAW", 380, 45);
                }
            }
        }
    }

    public static void main(String[] args) {
        Random rand = new Random();
        OthelloState state = new OthelloState(
            OthelloGame.N, OthelloGame.E, OthelloGame.B, OthelloGame.W);
        HashSet<Move> legalMoves = OthelloGame.getAllLegalMoves(
            state.getBoard(), state.getPlayer());
        OthelloGUI content = new OthelloGUI(state);
        JFrame window = new JFrame("Othello");
        window.setContentPane(content);
        window.setSize(600, 500);
        // window.setLocation(100,100);
        window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        window.setVisible(true);

        boolean over = false;

        for (int turn = 0; turn < OthelloGame.N * OthelloGame.N; turn++){
            byte[][] curBoard = state.getBoard();
            byte curPlayer = state.getPlayer();
            int blackScore = OthelloGame.computeScore(curBoard, OthelloGame.B);
            int whiteScore = OthelloGame.computeScore(curBoard, OthelloGame.W);
            HashSet<Move> disLegalMoves = new HashSet<>(legalMoves);
            boolean disOver = over;
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<Move> future = executor.submit(new TimedPlayer(state, legalMoves));
            Move move = null;
            Timer timer = null;
            if (!over) {
                StringBuilder sb = new StringBuilder();
                sb.append("-------------------------------------\n");
                sb.append("Move Number: ");
                sb.append(turn + 1);
                sb.append('\n');
                sb.append(state);
                sb.append("Player: ");
                if (curPlayer == OthelloGame.B) {
                    sb.append(Black.player.name());
                    sb.append(" (Black)\n");
                } else {
                    sb.append(White.player.name());
                    sb.append(" (White)\n");
                }
                sb.append("...thinking...");
                System.out.println(sb.toString());
            }
            try {
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask(){
                    int time = 10;
                    @Override
                    public void run(){
                        content.board.updateInfo(curBoard, disLegalMoves);
                        content.status.updateInfo(
                            curPlayer, blackScore, whiteScore, time--, disOver);
                        window.repaint();
                    }
                }, 0, 1000);
                if (!over) {
                    move = future.get(10, TimeUnit.SECONDS);
                }
            } catch (TimeoutException e){
                future.cancel(true);
                System.out.println("Timeout, the game will random a move.");
            } catch (Exception e){
                System.out.println("Error");
                e.printStackTrace();
            } finally {
                if (over) { break; }
                if (timer != null) { timer.cancel(); }
                if (move == null) {
                    move = (Move) legalMoves.toArray()[rand.nextInt(legalMoves.size())];
                }
                System.out.println("Move: " + move);
                state = OthelloGame.transition(state, move);
                legalMoves = OthelloGame.getAllLegalMoves(state.getBoard(), state.getPlayer());
                if (legalMoves.size() == 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("-------------------------------------\n");
                    if (state.getPlayer() == OthelloGame.B) {
                        sb.append(Black.player.name());
                        sb.append(" (Black)");
                    } else {
                        sb.append(White.player.name());
                        sb.append(" (White)");
                    }
                    sb.append(" is out of move!\n");
                    state.togglePlayer();
                    legalMoves = OthelloGame.getAllLegalMoves(state.getBoard(), state.getPlayer());
                    if (legalMoves.size() == 0) {
                        if (state.getPlayer() == OthelloGame.B) {
                            sb.append(Black.player.name());
                            sb.append(" (Black)");
                        } else {
                            sb.append(White.player.name());
                            sb.append(" (White)");
                        }
                        sb.append(" is out of move!\n");
                        sb.append("Game Over");
                        over = true;
                    }
                    System.out.println(sb.toString());
                }
            }
            executor.shutdown();
        }
    }
}

class TimedPlayer implements Callable<Move> {
    OthelloState state; 
    HashSet<Move> legalMoves;
    public TimedPlayer(OthelloState state, HashSet<Move> legalMoves) {
        this.state = state;
        this.legalMoves = legalMoves;
    }
    @Override
    public Move call() throws Exception {
        if (state.getPlayer() == OthelloGame.B){
            return Black.player.move(state, legalMoves);
        } else {
            return White.player.move(state, legalMoves);
        }
    }
}