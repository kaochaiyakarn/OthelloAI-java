import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;
import javax.swing.plaf.basic.BasicSliderUI.ActionScroller;

public class kaochaiyakarn extends Player{
	private static final int LIMIT = 6;
	public kaochaiyakarn(byte player) {
		super(player);
		this.team = "Mar";
		setTotalPoint();
		cornerSet();
		setweightDC();
		// TODO Auto-generated constructor stub
	}
//	private double alpha;
//	private double beta;
	@Override
	public Move move(OthelloState state, HashSet<Move> legalMoves) throws InterruptedException {
		// TODO Auto-generated method stub
		double alpha;
		double beta;
        double max = Double.MIN_VALUE;
        alpha = Double.MIN_VALUE;
        beta = Double.MAX_VALUE;
        Move maxMove = null;
        HashMap<Move, Integer> actionOrder = minToMaxScore(actioScore(state, legalMoves));
        for (Move move : legalMoves) {
            System.out.println(move.toString());
            double tempMax = minValue(OthelloGame.transition(state, move), 1,max,beta);
            if (max < tempMax) {
                max = tempMax;
                maxMove = move;
            }
        }
        return maxMove;
	}
	
    public double minValue(OthelloState state, int depth,double alpha,double beta) {
//       System.out.println(depth);
        HashSet<Move> legalMoves = OthelloGame.getAllLegalMoves(state.getBoard(), state.getPlayer()); //à¸šà¸­à¸�à¸„à¸§à¸²à¸¡à¹€à¸›à¸™à¹„à¸›à¹„à¸”à¹‰
        if (legalMoves.size() == 0 || depth > LIMIT) {
//            return OthelloGame.computeScore(state.getBoard(), this.player);
        	return computeScore(state,this.player);
        }
        double min = Double.MAX_VALUE;
//        HashMap<Move, Integer> t = MintoMax(legalMoves);  
//        HashMap<Move, Integer> actionOrderMin = minToMaxScore(actioScore(state, legalMoves));
        for (Move move : legalMoves) {
            OthelloState newState = OthelloGame.transition(state, move);// 
            min = Math.min(min, maxValue(newState, depth + 1,alpha,beta));
            if(min<=alpha){
            	return min;
            }
            beta = Math.min(beta,min);
//            if(min<=this.alpha){
//            	return min;
//            }
        }
        return min;
    }
    
    public double maxValue(OthelloState state, int depth,double alpha,double beta) {
//        System.out.println(depth);
        HashSet<Move> legalMoves = OthelloGame.getAllLegalMoves(state.getBoard(), state.getPlayer());
        if (legalMoves.size() == 0 || depth > LIMIT) {
//            return OthelloGame.computeScore(state.getBoard(), this.player);
        	return computeScore(state,this.player);
        }
        double max = Double.MIN_VALUE;
//        HashMap<Move, Integer> actionOrder = maxToMinScore(actioScore(state, legalMoves));
        for (Move move : legalMoves) {
            OthelloState newState = OthelloGame.transition(state, move);
            max = Math.max(max, minValue(newState, depth + 1,alpha, beta) );
            if(max>=beta){
            	return max;
            }
            
            alpha = Math.max(alpha,max); 
            
//            if(max>=beta){
//            	return max;
//            }
            
        }
        return max;
    }
	private static Map<Move,Integer> scorePoint = new HashMap<>();
	private static HashMap<Move,Integer> scorePointSort = new HashMap<>();

	static byte[][] SQUARE_SCORE = {
            {100 , -10 , 8  ,  6 ,  6 , 8  , -10 ,  100},
            {-10 , -25 ,  -4, -4 , -4 , -4 , -25 , -10 },
            {8   ,  -4 ,   6,   4,   4,   6,  -4 ,  8  },
            {6   ,  -4 ,   4,   0,   0,   4,  -4 ,  6  },
            {6   ,  -4 ,   4,   0,   0,   4,  -4 ,  6  },
            {8   ,  -4 ,   6,   4,   4,   6,  -4 ,  8  },
            {-10 , -25 ,  -4, -4 , -4 , -4 , -25 , -10 },
            {100 , -10 , 8  ,  6 ,  6 , 8  , -10 ,  100}};
	static int[][] weightSet = { {8, 85, -40, 10, 210, 520},
            {8, 85, -40, 10, 210, 520},
            {33, -50, -15, 4, 416, 2153},
            {46, -50, -1, 3, 612, 4141},
            {51, -50, 62, 3, 595, 3184},
            {33, -5,  66, 2, 384, 2777},
            {44, 50, 163, 0, 443, 2568},
            {13, 50, 66, 0, 121, 986},
            {4, 50, 31, 0, 27, 192},
            {8, 500, 77, 0, 36, 299}
			
	};
	static int[] weightTime = { 0, 55, 56, 57, 58, 59, 60, 61, 62, 63		
	};
	
	// set weight discount//
	private static int [][] weightDC;
	public static void setweightDC(){
		weightDC = new int [65][weightSet[0].length];
		
		//dc : Disk Count
        for(int dc = 0; dc <= 64; dc++) {
            // determine which set of weights to use
            int w = 0;
            for(int i = 0; i < weightTime.length; i++) {
                if(dc <= weightTime[i]) {
                    w = i;
                    break;
                }
            }

            // first set of weights: just return them
            if(w == 0) {
            	weightDC[dc] = weightSet[0];
                continue;
            }

            // linearly interpolate between the set of weights given for the
            // current number of moves and the previous set of weights
            double factor = ((double)dc - weightTime[w - 1]) / (weightTime[w] - weightTime[w - 1]);
            for(int i = 0; i < weightSet[w].length; i++) {
            	weightDC[dc][i] = (int)Math.rint(factor * weightSet[w][i] + (1 - factor) * weightSet[w - 1][i]);
            }
        }
	}
	// end set weight discount//
	public static int sizeofUnfreeSpace(OthelloState state){
		int count=0;
		for(int i=0;i<state.getBoard().length;i++){
			for(int j=0;j<state.getBoard()[i].length;j++){
				if(state.getBoard()[i][j]!=0) count++;
			}
		}
		return count;
	}
	public int computeScore(OthelloState state,byte player){
		int score = moblility(state,player)+placement(state,player)+piece(state, player)+corner(state, player)
		+stability(state, player)+frontier(state, player);
		
//		int score =0;
//		int[] weights = weightDC[sizeofUnfreeSpace(state)];
//		if(weights[0] != 0) {
//            score += weights[0] * moblility(state,player);
//        }
//        if(weights[1] != 0) {
//            score += weights[1] * frontier(state,player);
//        }
//        if(weights[2] != 0) {
//            score += weights[2] * piece(state,player);
//        }
//        if(weights[3] != 0) {
//            score += weights[3] * placement(state,player);
//        }
//        if(weights[4] != 0) {
//            score += weights[4] * stability(state,player);
//        }
//        if(weights[5] != 0) {
//            score += weights[5] * corner(state,player);
//        }
//		
		
		return score;
	}
	
	//frontier impplement tation from github//
	public static ArrayList<Move> getFrontierSquares(byte[][] board,byte player){

        ArrayList<Move> frontiers = new ArrayList<>();

        int oplayer = ((player == 1) ? 2 : 1);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(board[i][j]==oplayer){

                    ArrayList<Move> possiblefrontiers = new ArrayList<>();

                    //check 8 directions

                    //up
                    if(i>0 && board[i-1][j]==0) possiblefrontiers.add(new Move(i-1,j));
                    //down
                    if(i<7 && board[i+1][j]==0) possiblefrontiers.add(new Move(i+1,j));
                    //right
                    if(j<7 && board[i][j+1]==0) possiblefrontiers.add(new Move(i,j+1));
                    //left
                    if(j>0 && board[i][j-1]==0) possiblefrontiers.add(new Move(i,j-1));
                    //up-left
                    if(i>0 && j>0 && board[i-1][j-1]==0) possiblefrontiers.add(new Move(i-1,j-1));
                    //up-right
                    if(i>0 && j<7 && board[i-1][j+1]==0) possiblefrontiers.add(new Move(i-1,j+1));
                    //down-left
                    if(i<7 && j>0 && board[i+1][j-1]==0) possiblefrontiers.add(new Move(i+1,j-1));
                    //down-right
                    if(i<7 && j<7 && board[i+1][j+1]==0) possiblefrontiers.add(new Move(i+1,j+1));

                    //remove duplicates
                    for(Move pf : possiblefrontiers){
                        boolean redundant = false;
                        for(Move f : frontiers){
                            if(f.equals(pf)){
                                redundant = true;
                                break;
                            }
                        }
                        if(!redundant) frontiers.add(pf);
                    }
                }
            }
        }

        return frontiers;
    }

	public static int frontier(OthelloState state, byte player){
		int oplayer = (player==1) ? 2 : 1;
		
		int playerMove = getFrontierSquares(state.getBoard(), player).size();
		int oPlayerMove = getFrontierSquares(state.getBoard(), (byte)oplayer).size();
		
		return 100*(playerMove-oPlayerMove)/(playerMove+oPlayerMove+1);
	}
	//end frontier//
	
	//Stability//
	public static int stability(OthelloState state, byte player){
		int oplayer = (player==1) ? 2 : 1;
		int playerScore=0;
		int oPlayerScore=0;
		//=======player
		if(state.getBoard()[0][0] == player) playerScore += getStableDisks(state.getBoard(), player, 0, 0).size();
		if(state.getBoard()[0][7] == player) playerScore += getStableDisks(state.getBoard(), player, 0, 7).size();
		if(state.getBoard()[7][0] == player) playerScore += getStableDisks(state.getBoard(), player, 7, 0).size();
		if(state.getBoard()[7][7] == player) playerScore += getStableDisks(state.getBoard(), player, 7, 7).size();
		//end plater
		
		//oPlayer
		if(state.getBoard()[0][0]==oplayer) oPlayerScore += getStableDisks(state.getBoard(), oplayer,0, 0).size();
		if(state.getBoard()[0][7]==oplayer) oPlayerScore += getStableDisks(state.getBoard(), oplayer,0, 7).size();
		if(state.getBoard()[7][0]==oplayer) oPlayerScore += getStableDisks(state.getBoard(), oplayer,7, 0).size();
		if(state.getBoard()[7][7]==oplayer) oPlayerScore += getStableDisks(state.getBoard(), oplayer,7, 7).size();
		//end oplayer
		
		return 100*(playerScore-oPlayerScore)/(playerScore+oPlayerScore+1);
	}
	//end stability//
	
	//pieces//
	public static int piece(OthelloState state, byte player){
		byte oplayer = (player==(byte)1) ? (byte)2 : (byte)1;
		int playerPointCount =0,oPlayerPointCount=0;
		
		byte[][] board = state.getBoard();
		for(byte i=0;i<board.length;i++){
			for(byte j=0;j<board[i].length;j++){
				if(board[i][j]==player) playerPointCount++;
				if(board[i][j]==oplayer) oPlayerPointCount++;
			}
		}
		
		return 100*(playerPointCount-oPlayerPointCount)/(playerPointCount+oPlayerPointCount+1);
	}
	//pieces//
	
	//placement//
	public static int placement(OthelloState state, byte player){
		byte oplayer = (player==(byte)1) ? (byte)2 : (byte)1;
		
		int playerPoint=0;
		int opponerPoint=0;
		byte[][] board = state.getBoard();
		for(int i=0;i<board.length;i++ ){
			for(int j=0;j<board[i].length;j++){
				if(board[i][j]==player){playerPoint+=SQUARE_SCORE[i][j];}
				if(board[i][j]==oplayer){opponerPoint+=SQUARE_SCORE[i][j];}
			}
		}
		
		
		return playerPoint-opponerPoint;
	}
	//placement//
	
	//corner grap//
	private static HashSet<Move> corner = new HashSet<>();
	public void cornerSet(){
		corner.add(new Move(0,0));
		corner.add(new Move(0,7));
		corner.add(new Move(7,0));
		corner.add(new Move(7,7));

	}
	public static int corner(OthelloState state,byte player ){
		HashSet<Move> move = OthelloGame.getAllLegalMoves(state.getBoard(),player);
		for(Move move1 : corner){
			if(move.contains(move1)) return 100;
		}
		return  0;
	}
	//end corer grap//
	
	
	//mobility//
	public static int moblility(OthelloState state, byte player){
		byte oplayer = (player==(byte)1) ? (byte)2 : (byte)1;
		
		int playerSize = OthelloGame.getAllLegalMoves(state.getBoard(),player).size();
		int opPlayerSize = OthelloGame.getAllLegalMoves(state.getBoard(),(byte)oplayer ).size();
		return 100*(playerSize-opPlayerSize)/(playerSize+opPlayerSize+1);
		
	}
	//ed mobility//
	
	
	//actionscore//
	public HashMap<Move,Integer> actioScore(OthelloState state,HashSet<Move> legalMoves){
		HashMap<Move,Integer> score = new HashMap<>();
		for(Move move : legalMoves){
            OthelloState newState = OthelloGame.transition(state, move);
            score.put(move, computeScore(newState,state.getPlayer()));
		}
		maxToMinScore(score);
		return score;
	}
	//endactioscore//
	
	//MaxtoMin computescore//
	public HashMap<Move,Integer> maxToMinScore(HashMap<Move,Integer> score){
		
		HashMap<Move,Integer> revertScore = new HashMap<>();
    	revertScore = score.entrySet().stream()
    			.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
    			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
    					(oldValue, newValue) -> oldValue, LinkedHashMap::new)); 
		return revertScore;
	}
	//MaxtoMin computescore//
	
	//MintoMax compute Score//
	public HashMap<Move,Integer> minToMaxScore(HashMap<Move,Integer> score){
		
		HashMap<Move,Integer> naturalScore = new HashMap<>();
		naturalScore = score.entrySet().stream()
    			.sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
    			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
    					(oldValue, newValue) -> oldValue, LinkedHashMap::new)); 	
		return naturalScore;
	}
	//Mintomax compute score//
	
	
	//implement this from https://github.com/arminkz/Reversi/blob/master/src/game/BoardHelper.java //
	 public static ArrayList<Move> getStableDisks(byte[][] board,int player,int i,int j){

	        ArrayList<Move> stableDiscs = new ArrayList<>();

	        int mi , mj;
	        int oplayer = ((player == 1) ? 2 : 1);

	        //move up
	        ArrayList<Move> mupts = new ArrayList<>();
	        mi = i - 1;
	        mj = j;
	        while(mi>0 && board[mi][mj] == player){
	            mupts.add(new Move(mi,mj));
	            mi--;
	        }
	        for(Move sd : mupts){
	            boolean redundant = false;
	            for(Move stableDisc : stableDiscs){
	                if(sd.equals(stableDisc)){
	                    redundant = true;
	                    break;
	                }
	            }
	            if(!redundant) stableDiscs.add(sd);
	        }

	        //move down
	        ArrayList<Move> mdpts = new ArrayList<>();
	        mi = i + 1;
	        mj = j;
	        while(mi<7 && board[mi][mj] == oplayer){
	            mdpts.add(new Move(mi,mj));
	            mi++;
	        }
	        for(Move sd : mdpts){
	            boolean redundant = false;
	            for(Move stableDisc : stableDiscs){
	                if(sd.equals(stableDisc)){
	                    redundant = true;
	                    break;
	                }
	            }
	            if(!redundant) stableDiscs.add(sd);
	        }

	        //move left
	        ArrayList<Move> mlpts = new ArrayList<>();
	        mi = i;
	        mj = j - 1;
	        while(mj>0 && board[mi][mj] == oplayer){
	            mlpts.add(new Move(mi,mj));
	            mj--;
	        }
	        for(Move sd : mlpts){
	            boolean redundant = false;
	            for(Move stableDisc : stableDiscs){
	                if(sd.equals(stableDisc)){
	                    redundant = true;
	                    break;
	                }
	            }
	            if(!redundant) stableDiscs.add(sd);
	        }

	        //move right
	        ArrayList<Move> mrpts = new ArrayList<>();
	        mi = i;
	        mj = j + 1;
	        while(mj<7 && board[mi][mj] == oplayer){
	            mrpts.add(new Move(mi,mj));
	            mj++;
	        }
	        for(Move sd : mrpts){
	            boolean redundant = false;
	            for(Move stableDisc : stableDiscs){
	                if(sd.equals(stableDisc)){
	                    redundant = true;
	                    break;
	                }
	            }
	            if(!redundant) stableDiscs.add(sd);
	        }

	        //move up left
	        ArrayList<Move> mulpts = new ArrayList<>();
	        mi = i - 1;
	        mj = j - 1;
	        while(mi>0 && mj>0 && board[mi][mj] == oplayer){
	            mulpts.add(new Move(mi,mj));
	            mi--;
	            mj--;
	        }
	        for(Move sd : mulpts){
	            boolean redundant = false;
	            for(Move stableDisc : stableDiscs){
	                if(sd.equals(stableDisc)){
	                    redundant = true;
	                    break;
	                }
	            }
	            if(!redundant) stableDiscs.add(sd);
	        }

	        //move up right
	        ArrayList<Move> murpts = new ArrayList<>();
	        mi = i - 1;
	        mj = j + 1;
	        while(mi>0 && mj<7 && board[mi][mj] == oplayer){
	            murpts.add(new Move(mi,mj));
	            mi--;
	            mj++;
	        }
	        for(Move sd : murpts){
	            boolean redundant = false;
	            for(Move stableDisc : stableDiscs){
	                if(sd.equals(stableDisc)){
	                    redundant = true;
	                    break;
	                }
	            }
	            if(!redundant) stableDiscs.add(sd);
	        }

	        //move down left
	        ArrayList<Move> mdlpts = new ArrayList<>();
	        mi = i + 1;
	        mj = j - 1;
	        while(mi<7 && mj>0 && board[mi][mj] == oplayer){
	            mdlpts.add(new Move(mi,mj));
	            mi++;
	            mj--;
	        }
	        for(Move sd : mdlpts){
	            boolean redundant = false;
	            for(Move stableDisc : stableDiscs){
	                if(sd.equals(stableDisc)){
	                    redundant = true;
	                    break;
	                }
	            }
	            if(!redundant) stableDiscs.add(sd);
	        }

	        //move down right
	        ArrayList<Move> mdrpts = new ArrayList<>();
	        mi = i + 1;
	        mj = j + 1;
	        while(mi<7 && mj<7 && board[mi][mj] == oplayer){
	            mdrpts.add(new Move(mi,mj));
	            mi++;
	            mj++;
	        }
	        for(Move sd : mdrpts){
	            boolean redundant = false;
	            for(Move stableDisc : stableDiscs){
	                if(sd.equals(stableDisc)){
	                    redundant = true;
	                    break;
	                }
	            }
	            if(!redundant) stableDiscs.add(sd);
	        }

	        return stableDiscs;
	    }
	//end implement//
	
	
	
	
	
	
	
	public void setTotalPoint(){
		for(int i=0;i<8;i++){
    		for(int j=0;j<8;j++){
    			if(i==0||i==7){
    				if(j==0||j==7){
    					scorePoint.put(new Move(i,j), 120);
    				}
    				if(j==1||j==6){
    					scorePoint.put(new Move(i,j),-20);
    				}
    				if(j==2||j==5){
    					scorePoint.put(new Move(i,j),20);
    				}
    				if(j==3||j==4){
    					scorePoint.put(new Move(i,j),5);
    				}
    			}
    			if(i==1||i==6){
    				if(j==0||j==7){
    					scorePoint.put(new Move(i,j), -20);
    				}
    				if(j==1||j==6){
    					scorePoint.put(new Move(i,j),-40);
    				}
    				if(j==2||j==5){
    					scorePoint.put(new Move(i,j),-5);
    				}
    				if(j==3||j==4){
    					scorePoint.put(new Move(i,j),-5);
    				}
    			}
    			if(i==2||i==5){
    				if(j==0||j==7){
    					scorePoint.put(new Move(i,j), 20);
    				}
    				if(j==1||j==6){
    					scorePoint.put(new Move(i,j),-5);
    				}
    				if(j==2||j==5){
    					scorePoint.put(new Move(i,j),15);
    				}
    				if(j==3||j==4){
    					scorePoint.put(new Move(i,j),3);
    				}
    			}
    			if(i==3||i==4){
    				if(j==0||j==7){
    					scorePoint.put(new Move(i,j), 5);
    				}
    				if(j==1||j==6){
    					scorePoint.put(new Move(i,j),-5);
    				}
    				if(j==2||j==5){
    					scorePoint.put(new Move(i,j),3);
    				}
    				if(j==3||j==4){
    					scorePoint.put(new Move(i,j),3);
    				}
    			}
    			
    		}
    	}
		scorePointSort = scorePoint.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
	}
	
	private static HashMap<Move,Integer> scoreUse = new HashMap<>();
    public void setPoint(){
    	scoreUse = new HashMap<>(scorePoint);
    }
    
    public void orderAll(HashSet<Move> legalMoves){
    	setPoint();
    	scoreUse.keySet().retainAll(legalMoves);
//    	HashMap<Move,Integer> tempmaxmin = new HashMap<>();
    	scorePointSort = scoreUse.entrySet().stream()
    			.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
    			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
    					(oldValue, newValue) -> oldValue, LinkedHashMap::new)); 
    }
    
    public HashMap<Move,Integer> maxToMin(HashSet<Move> legalMoves){
    	setPoint();
    	scoreUse.keySet().retainAll(legalMoves);
    	HashMap<Move,Integer> tempmaxmin = new HashMap<>();
    	tempmaxmin = scoreUse.entrySet().stream()
    			.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
    			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
    					(oldValue, newValue) -> oldValue, LinkedHashMap::new)); 
    	return tempmaxmin;

    }
    
    public HashMap<Move,Integer> MintoMax(HashSet<Move> legalMoves){
		setPoint();
    	scoreUse.keySet().retainAll(legalMoves);
    	HashMap<Move,Integer> tempminmax = new HashMap<>();
    	tempminmax = scoreUse.entrySet().stream()
    			.sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
    			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
    					(oldValue, newValue) -> oldValue, LinkedHashMap::new)); 
    	
    	return tempminmax;

    }
    
}
