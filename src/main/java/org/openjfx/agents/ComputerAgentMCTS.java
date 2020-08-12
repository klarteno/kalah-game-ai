package org.openjfx.agents;

import org.openjfx.Board;
import org.openjfx.Game;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComputerAgentMCTS  implements ComputerAgentAI {
    NodeState root_node;
    private static final int processors = Runtime.getRuntime().availableProcessors();
    private static final int MAX_ITERATIONS = 30;
    private static final byte DEPTH_PLAY_GAME_SIMULATION = 20;
    //private static final LongAdder tasksCompleted = new LongAdder();

    @Override
    public byte[] playComputer(Game game) {
        this.root_node = new NodeState(game.kalah_board);
        Board new_round_board;
        if(game.kalah_board.getSelectableSlots().size()==1){
            new_round_board = game.kalah_board.makeMove(game.kalah_board.getSelectableSlots().lastElement());
        }else {
              new_round_board = game.kalah_board.makeMove(this.compute_move(game.kalah_board));
        }

        byte[] toUpdateButtons = game.getIndexesBoardChanges(game.kalah_board,new_round_board);
        game.kalah_board = new_round_board;

        return toUpdateButtons;
    }

    @Override
    public Byte find_next_move(Board game_board) {
        return null;
    }


    public NodeState computeTree(final Board board){
        ComputerAgentAI computerAgentAI  = new ComputerAgentAlphaBeta();

        NodeState root_node = new NodeState(board);
        NodeState node = root_node;

        for (int iteration = 0; iteration < this.MAX_ITERATIONS; iteration++) {
            while(node.isFullyExpanded()){
                node = node.getNextRankedUCTChildNode();
            }
        if(node.hasUntriedMoves()){
            node = node.expandOneNode();
        }
        double score_result = 0;
        score_result = node.startStateSimulation(DEPTH_PLAY_GAME_SIMULATION,computerAgentAI);
        //System.out.println("score_result to propagate: "+ score_result);

        node.propagate(score_result);
        }

        return root_node;
    }


    public int compute_move(Board kalah_board) {
        //Map<Byte, Integer> visits = new TreeMap<>();
        //Map<Byte, Double> wins = new TreeMap<>();
        int[] visits = new int[5];
        double[] wins = new double[5];
        //AtomicReference atomic_root_node = new AtomicReference(this.root_node);

        final CompletableFuture<NodeState>[] futures = new CompletableFuture[processors];
            for (int i = 0; i < processors; i++) {
                futures[i] = CompletableFuture.supplyAsync(() -> this.computeTree(kalah_board));
            }

        List<NodeState> resultComputations = Stream.of(futures)
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        visits = new int[14];
        wins = new double[14];

        List<NodeState> children_nodes;
        for (NodeState root_computation_result : resultComputations) {
           children_nodes = root_computation_result.children_nodes;
            NodeState max_visits_move = root_computation_result.getRobustChild();
            for (int node = 0; node < children_nodes.size() ; node++) {
                visits[children_nodes.get(node).pit_index_action] += children_nodes.get(node).getVisitCount();
                wins[children_nodes.get(node).pit_index_action] += children_nodes.get(node).getWinningCount();
            }
        }

        double best_score = -1;
        double success_aproximation = -1;
        int  best_move = -1;

        int second_best_move = 8;   //random default value given
        int max_visits = 0;         //random default value given
        for (int i = 0; i < visits.length ; i++) {
            if (! (visits[i] == 0)) {
                success_aproximation = wins[i] / visits[i];
                if (success_aproximation > best_score){
                    best_score = success_aproximation;
                    best_move = i;
                }
                if (max_visits < visits[i]){
                    second_best_move = i;
                    max_visits = visits[i];
                }
            }
        }

        if (best_score <= 0){
            System.out.println("second_best_move of computer chosen: " + second_best_move);
            return second_best_move;
        }else {
            System.out.println("best move of computer chosen found: " + best_move);
            return best_move;
        }
    }
}

class NodeState {
    private Random random_distribution = new Random();
    //private Random r2 = new Random( System.currentTimeMillis());
    private static final double CONSTANT = 1.0f/Math.sqrt(2.0f);

    private LongAdder visitCount;
    private DoubleAdder winCount;

    public Board game_board;
    public NodeState parent;
    Vector<Byte> moves;
    public List<NodeState> children_nodes;
    /*the board store in this node is the result of this move of the pit index*/
    Byte pit_index_action;

    NodeState(Board board) {
        this.game_board = board;
        moves = this.game_board.getSelectableSlots();
        this.children_nodes = new ArrayList<>();
        this.visitCount = new LongAdder();
        this.winCount = new DoubleAdder();
    }

    public long getVisitCount(){ return visitCount.longValue(); }

    public double getWinningCount(){ return winCount.doubleValue(); }

    boolean isNonTerminal() {
        return this.game_board.isGameOver();
    }

    //ranks the chilren nodes based on the UCT formula and assumes no unknown children are left
    NodeState getNextRankedUCTChildNode() {
        NodeState best = null;
        double value = 0;
        for (NodeState node : children_nodes) {
            double wC = node.winCount.doubleValue();
            double vC = node.visitCount.doubleValue();
            double UCT_score =  wC/vC + CONSTANT*Math.sqrt(2*Math.log(visitCount.doubleValue()) / vC);

            if (best == null || UCT_score > value) {
                value = UCT_score;
                best = node;
            }
        }

        return best;
    }

    boolean isFullyExpanded() {
       // return (children_nodes.size() > 0) && (children_nodes.size() == game_board.getSelectableSlots().size());
         return (children_nodes.size() > 0) && this.moves.isEmpty();
    }

    public Byte nextRandomMove(){
        int nxt_index = random_distribution.nextInt(this.moves.size());

        return this.moves.remove(nxt_index);        //remove pit_index from present moves

    }

    public NodeState getNextNodeFromMove(Byte pit_index) {
        Board next_node_board = this.game_board.makeMove(pit_index);
        NodeState next_node_tree = new NodeState(next_node_board);
        next_node_tree.pit_index_action = pit_index;
        next_node_tree.parent = this;

        return next_node_tree;
    }

    public  void expandFull() {
        if (children_nodes == null) {
            Vector<Byte> nextMoves = this.game_board.getSelectableSlots();
            for (int i = 0; i < nextMoves.size() ; i++) {
                this.children_nodes.add(new NodeState(this.game_board.makeMove(nextMoves.get(i))));
            }
        }
    }

    public NodeState expandOneNode(){
        NodeState next_node = this.getNextNodeFromMove((byte) this.nextRandomMove());
        this.children_nodes.add(next_node);
        return next_node;
    }

    public void propagate(final double score) {
        //System.out.println("score: +"+ score);
        winCount.add(score);
        visitCount.increment();
        if (parent != null) {
            parent.propagate(score);
        }
    }
    public NodeState getRobustChild() {
        return this.children_nodes.stream().max(Comparator.comparingDouble(node -> node.visitCount.longValue())).orElseThrow(() -> new RuntimeException("No children here"));
    }

    public int heuristic(Board board) {
        int computer_score = 0;
        int human_score = 0;
        int array_offset = 1;
        for (int i = 0; i < board.getPits().length/2 /*- array_offset*/; ++i) {
            human_score += board.getPitStones(i);
            computer_score += board.getPitStones(board.oppositePitOf(i));
        }
        //System.out.println(human_score + "  "+  computer_score );
        int score = (board.getComputerBoardStore() + computer_score) - (board.getHumanBoardStore() + human_score) ;
        //int score = board.getComputerBoardStore();
	//System.out.println("score  "+  score );
        return score;
    }

    @Override
    public String toString()
    {
        String s = "P: " + this.game_board.isHumansTurn() + "M: " + this.pit_index_action +
                "W/V: "+ this.winCount + "/" +this.visitCount + "U: " + this.game_board.getSelectableSlots().size() + "\n";
        return s;
    }

    private String tree_to_string(int max_depth, int indent)
    {
        if (indent >= max_depth) {
            return "";
        }

        String s = indent_string(indent) + this.toString();
        for (int i = 0; i < children_nodes.size() ; i++) {
            s += children_nodes.get(i).tree_to_string(max_depth, indent + 1);
        }

        return s;
    }

    private String indent_string(int indent)
    {
        String s = "";
        for (int i = 1; i <= indent; ++i) {
            s += "| ";
        }
        return s;
    }

    public boolean hasUntriedMoves() {
        return !this.moves.isEmpty();
    }

    public double startStateSimulation(byte depthPlayGameSimulation, ComputerAgentAI computerAgentAI) {
        Byte next_move = computerAgentAI.find_next_move(this.game_board);
        if (next_move != null){
            //System.out.println("next_move from alpha beta : " + next_move);
            //TO DO: the move index result from alpha beta looks off with one more position
            return this.heuristic(this.game_board.makeMove(next_move-1));
        }else {
            return 0;
        }
    }

    //lefover and not yet future use
    public double startStateRandomSimulation(byte depthPlayGameSimulation) {
        Board board = null;

        if(depthPlayGameSimulation > 0 && this.game_board.getSelectableSlots().size()>0){
            board = this.game_board.makeMove(random_distribution.nextInt(this.game_board.getSelectableSlots().size()));
            depthPlayGameSimulation--;

        }else{
            return 0;
        }

        while (depthPlayGameSimulation>0 && board.getSelectableSlots().size()>0)
        {
            board = board.makeMove(random_distribution.nextInt(board.getSelectableSlots().size()));
            depthPlayGameSimulation--;

        }

        return this.heuristic(board);


    }

}

