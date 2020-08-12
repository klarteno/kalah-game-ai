package org.openjfx.agents;

import org.openjfx.Board;
import org.openjfx.Game;

import java.util.Vector;

public class ComputerAgentAlphaBeta implements ComputerAgentAI {

    private int SIMULATION_DEPTH = 35;
    private Byte currentBest;

    @Override
    public byte[] playComputer(Game game) {
        Board new_round_board = game.kalah_board.makeMove(find_next_move(game.kalah_board));
        byte[] toUpdateButtons = game.getIndexesBoardChanges(game.kalah_board,new_round_board);
        game.kalah_board = new_round_board;

        return toUpdateButtons;
    }
    @Override
    public Byte find_next_move(Board initialBoard) {
        boolean currentPlayer = initialBoard.isComputersTurn();
       // assert currentPlayer;

        currentBest = null;
        alphabeta(initialBoard, SIMULATION_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, currentPlayer);
        return currentBest;
    }

    private int heuristic(Board board) {
        int computer_score = 0;
        int human_score = 0;

        int array_offset = 1;

        for (int i = 0; i < board.getPits().length/2 /*- array_offset*/; ++i) {
            human_score += board.getPitStones(i);
            computer_score += board.getPitStones(board.oppositePitOf(i));
        }
        //System.out.println(human_score + "  "+  computer_score );
        int score = (board.getComputerBoardStore() + computer_score) - (board.getHumanBoardStore() + human_score) ;
        //int score = board.getComputerBoardStore()-board.getHumanBoardStore();
	//System.out.println("score "+  score );

	return score;

    }

    private int alphabeta(Board node_board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        assert  node_board!=null;

        if (depth == 0 || node_board.isGameOver()) {
            return heuristic(node_board);
        }

        /*gets the moves sorted by the heuristic and the most promising will be expanded first*/
        Vector<Byte> legalMoves = node_board.getSelectableSlots(Boolean.TRUE);

        for (Byte move : legalMoves) {
            Board next_node_board = playBoard( node_board, move);
           // assert next_node_board!=null;
            boolean playNextTurn = this.nextPlayer(node_board,next_node_board);

            if (maximizingPlayer) {
                int oldAlpha = alpha;
                alpha = Math.max(alpha, alphabeta(next_node_board, depth - 1, alpha, beta, playNextTurn));
                if (depth == SIMULATION_DEPTH && (oldAlpha < alpha || currentBest == null)) {
                    currentBest = move;
                }
            } else {
                beta = Math.min(beta, alphabeta(next_node_board, depth - 1, alpha, beta, !playNextTurn));
            }

            if (beta <= alpha) {
                break;
            }
        }
        return maximizingPlayer ? alpha : beta;
    }

    private Board playBoard(Board node_board,Byte move){
        if(node_board.isGameOver()){
            return node_board;
        }else{
            return node_board.makeMove(move);
        }
    }

    private boolean nextPlayer(Board node_board, Board next_node_board){
        if (node_board.isHumansTurn()){
            return next_node_board.isHumansTurn();
        }
        if(node_board.isComputersTurn()){
            return next_node_board.isComputersTurn();
        }
        if(node_board.isGameOver()){
            return false;
        }

        return false;
    }

}

