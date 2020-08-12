package org.openjfx.agents;

import org.openjfx.Board;
import org.openjfx.Game;

import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

public class ComputerAgentAlphaBetaMemoised implements ComputerAgentAI {

    private  int SIMULATION_DEPTH = 15;

    // init transposition table
    private HashMap<Long, TransEntry> transTable = new HashMap<Long, TransEntry>();;
    private int nSeeds = 3 * 12;
    private long[][] zobristTable = new long[14][nSeeds + 1];
    //TransEntry trans;
    Random prng = new Random();

    private long zobristHash(byte[] state) {
        long key = 0;
        for (int i = 0; i < 14; ++i) {
            key ^= zobristTable[i][state[i]];
        }
        return key;
    }


    @Override
    public byte[] playComputer(Game game) {
        for (int i = 0; i < 14; ++i) {
            for (int j = 0; j < nSeeds + 1; ++j) {
                zobristTable[i][j] = prng.nextLong();
            }
        }


        Board new_round_board = game.kalah_board.makeMove(find_next_move(game.kalah_board));
        byte[] toUpdateButtons = game.getIndexesBoardChanges(game.kalah_board,new_round_board);
        game.kalah_board = new_round_board;

        return toUpdateButtons;
    }

    @Override
    public Byte find_next_move(Board initialBoard) {
        boolean currentPlayer = initialBoard.isComputersTurn();
       // assert currentPlayer;

        Vector<Byte> legalMoves = initialBoard.getSelectableSlots();
        ChildMove next_node_move = new ChildMove(legalMoves.get(prng.nextInt(legalMoves.size())),initialBoard);

        MoveScore currentBest = alphabeta(next_node_move, SIMULATION_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, currentPlayer);

        assert currentBest != null;

        System.out.println("currentBest.move currentBest.move : "+ currentBest.move);


        return  Byte.valueOf((byte) currentBest.move);
    }

    private int heuristic(Board board) {
        int computer_score = 0;
        int human_score = 0;

        int array_offset = 1;

        for (int i = 0; i < board.getPits().length/2 - array_offset; ++i) {
            human_score += board.getPitStones(i);
            computer_score += board.getPitStones(board.oppositePitOf(i));
        }
        //System.out.println(human_score + "  "+  computer_score );
        int score = (board.getComputerBoardStore() + computer_score) - (board.getHumanBoardStore() + human_score) ;
        //System.out.println("score  "+  score );
        return score;

    }

    private MoveScore alphabeta(ChildMove move, int search_depth, int alpha, int beta, boolean maximizingPlayer) {
        int value = 0, bestMove = 0;
        MoveScore searchResult;
        TransEntry trans;
        // trans table state lookup
        long state_hash = zobristHash(move.board.getPits());

        if (transTable.containsKey(state_hash)) {
            trans = transTable.get(state_hash);
            if (trans.depth >= search_depth) {
                if (trans.lowerbound >= beta) {
                    return new MoveScore(move.move, trans.lowerbound);
                }
                if (trans.upperbound <= alpha) {
                    return new MoveScore(move.move, trans.upperbound);
                }
                alpha = Math.max(alpha, trans.lowerbound);
                beta = Math.min(beta, trans.upperbound);
            }
        }


        //base case of iterative depenning
        if (search_depth == 0 || move.board.isGameOver()) {
            return new MoveScore(move.move, heuristic(move.board));
        }

        /*gets the moves sorted by the heuristic and the most promising will be expanded first*/
        Vector<Byte> legalMoves = move.board.getSelectableSlots(Boolean.TRUE);

        for (Byte next_move : legalMoves) {
            Board next_node_board = null;
            boolean playNextTurn = playBoard( move.board, next_move, next_node_board);

            ChildMove next_node_move = new ChildMove(next_move,next_node_board);
            if (maximizingPlayer) {
                int oldAlpha = alpha;
                value = Integer.MIN_VALUE;
                searchResult = alphabeta(next_node_move, search_depth - 1, alpha, beta, playNextTurn);
                if (searchResult.score >= value) {
                    value = searchResult.score;
                    bestMove = next_move;
                }
                alpha = Math.max(alpha,value );

            } else {
                value = Integer.MAX_VALUE;
                searchResult = alphabeta(next_node_move, search_depth - 1, alpha, beta, !playNextTurn);
                value = Integer.MAX_VALUE;
                if (searchResult.score <= value) {
                    value = searchResult.score;
                    bestMove = next_move;
                }
                beta = Math.min(beta, value);
            }

            if (beta <= alpha) {
                break;
            }
        }

        // store trans table values
        trans = transTable.getOrDefault(state_hash, new TransEntry());

        if (trans.depth <= search_depth) {
            // fail low implies an upper bound
            if (value <= alpha) {
                trans.upperbound = value;
            }
            // fail high implies a lower bound
            else if (value >= beta) {
                trans.lowerbound = value;
            }
            // accurate minimax value
            else {
                trans.lowerbound = value;
                trans.upperbound = value;
            }
            trans.depth = search_depth;
            transTable.put(state_hash, trans);
        }

        return new MoveScore(bestMove, value);
    }

    private boolean playBoard(Board node_board,Byte move, Board next_node_board){
        if (node_board.isHumansTurn()){
            next_node_board = node_board.makeMove(move);
            return next_node_board.isHumansTurn();
        }
        if(node_board.isComputersTurn()){
            next_node_board = node_board.makeMove(move);
            return next_node_board.isComputersTurn();
        }
        if(node_board.isGameOver()){
            next_node_board = node_board;
            return false;
        }

        return false;
    }

    class TransEntry {
        public int depth;
        public int upperbound;
        public int lowerbound;

        public TransEntry() {
            this.depth = 0;
            this.upperbound = Integer.MAX_VALUE;
            this.lowerbound = Integer.MIN_VALUE;
        }
    }
    class MoveScore {
        public int move;
        public int score;

        public MoveScore(int move, int score) {
            this.move = move;
            this.score = score;
        }
    }

    class ChildMove {
        public int move;
        public Board board;

        public ChildMove(int move, Board board) {
            this.move = move;
            this.board = board;
        }
    }

    }

