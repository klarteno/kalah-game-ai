package org.openjfx;

import org.openjfx.agents.ComputerAgentAI;
import org.openjfx.agents.ComputerAgentAlphaBeta;
import org.openjfx.agents.ComputerAgentMCTS;
import org.openjfx.agents.ComputerAgentRandom;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * The Game represents the game interactions that is going on between two Players.
 * It is not a representation of Kalahas logic or rules. See Board.java 
 * for this purpose.
 */
public class Game {

    public Board kalah_board;
    private ComputerAgentAI computerAgentAI;

    private int difficulty;
    private boolean logCreated = false;
    private boolean io_error = false;

    /**
     * Gets difficulty.
     *
     * @return the difficulty
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * Gets board.
     *
     * @return the board
     */
    public Board getBoard() {
        return this.kalah_board;
    }

    /**
     * Instantiates a new Game.
     *
     * @param difficulty the difficulty or Depth of the StoreTree
     * @param stones     the number of stones
     * @param pits       the number of pits for each player
     * @param start      defines who starts the game
     * @pre Number of all Stones cannot be greater than the maximum Byte Value
     */
    public Game(int difficulty, int stones, int pits, int start) {
       // this.computerAgentAI = new ComputerAgentAlphaBeta();
        this.computerAgentAI = new ComputerAgentMCTS();
        // Sum of all stones has to be representable by Byte
        assert(pits*stones*2 < Byte.MAX_VALUE);
        this.difficulty = difficulty;
        boolean human;
        switch (start) {
            case -1: human = false; break;
            case 0: human = randomStart(); break;
            case 1: human = true; break;
            default: human = randomStart();
        }
        this.kalah_board = new Board(stones, pits, human);
        this.logCreated = false;
    }

    /**
     * Randomizes who starts the Game
     *
     * @return true if Human starts
     */
    public boolean randomStart() {
        return Math.random() < 0.5;
    }

    /**
     * Writes a log file during the Game and saves it into a Text File.
     *
     * @param i the
     */
    public void writeLog(int i) {
        if (!io_error) {
            try{
                Writer out = new FileWriter("KalahaGo_Log.txt", logCreated);
                if (!logCreated)
                    out.write(System.getProperty("line.separator"));
                String prefix = "";
                // Call for recursive Print Method
                this.kalah_board.printBoard();
                out.write("----------");
                out.write(System.getProperty("line.separator"));
                if (!this.getBoard().isGameOver()) {
                    out.write(Integer.toString(i + ((getBoard().isHumansTurn()) ? 0 : getBoard().getPits().length/2)));
                    out.write(System.getProperty("line.separator"));
                }
                out.close();
            } catch (IOException e) {
                System.err.println("Error: IO Operation unsuccessful");
                io_error = true;
            }
            if (!logCreated) 
                logCreated = true;
        }
    }

    public byte[] getIndexesBoardChanges(Board kalah_board, Board newBoard) {
        byte[] res = new byte[kalah_board.getPits().length];
        for (int i = 0; i < newBoard.getPits().length; i++) {
            if (kalah_board.getPitStones(i) != newBoard.getPitStones(i)) {
                res[i]= newBoard.getPitStones(i);
            }
            else{
                res[i] = -1;
            }
        }
        return res;
    }

    public byte[] playHuman(int index) {
        if(this.kalah_board.getPitStones(index)>0){
            Board board = this.kalah_board.makeMove(index);
            byte[] toUpdateButtons = getIndexesBoardChanges(this.kalah_board, board);
            this.kalah_board = board;
            return toUpdateButtons;
        }

        return  this.kalah_board.getPits();
    }


    public byte[] playComputer() {
        return this.computerAgentAI.playComputer(this);
    }
}
