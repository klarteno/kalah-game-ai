package org.openjfx;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The Board represents the Playing Board and all the elements on it. 
 * It's methods contain the entire logic and rules of the Game Kalaha.
 */
public final class Board {
    private  int no_of_pits_of_player;

    private int MANCALA_HUMAN;
    private int MANCALA_COMPUTER;

    private byte[] pits;

    public int getHumanStoreIndex() {
        return this.MANCALA_HUMAN;
    }

    public void setNewPits(byte[] pits_list) {
        this.pits = pits_list;
    }

    public enum BoardStatus {
        COMPUTER_TURN,
        HUMAN_TURN,
        GAME_OVER;
    }

    AtomicReference<BoardStatus> board_status = new AtomicReference<>(BoardStatus.HUMAN_TURN);

    /**
     * Instantiates a new Board based on a certain number of stones, number of pits and who can make a move first
     *
     * @param no_stones the stones inside each pit
     * @param no_of_pits   the number of playing pits for each player
     * @param is_human_turn      is true if Human can make the first move.
     */
    public Board(int no_stones, int no_of_pits, boolean is_human_turn ) {
        byte stones = (byte) no_stones;
        this.no_of_pits_of_player = no_of_pits;
        this.pits = new byte[2*no_of_pits+2];
        this.MANCALA_HUMAN    = no_of_pits_of_player;
        this.MANCALA_COMPUTER = 2 * no_of_pits_of_player + 1;

        for (int pit_i = 0; pit_i < this.no_of_pits_of_player; pit_i++) {
            setPitStones(pit_i, stones);
            int ty=oppositePitOf(pit_i);
            setPitStones(oppositePitOf(pit_i), stones);
        }

        //setPitStones(MANCALA_HUMAN,  0);
        //setPitStones(MANCALA_COMPUTER, 0);
        if (is_human_turn){
            this.board_status.set(BoardStatus.HUMAN_TURN);
        }else{
            this.board_status.set(BoardStatus.COMPUTER_TURN);
        }

    }


    /**
     * Initiates a new Board that creates a deep copy of another Board
     * @param b The board thats to be copied
     */
    public Board(Board b) {
        //setPits(new byte[b.getPits().length]);
        this.pits = new byte[b.getPits().length];
        for (int i = 0; i < b.getPits().length; ++i) {
            setPitStones(i, b.getPitStones(i));
        }
        setBoardStatus(b.board_status.get());
        this.MANCALA_HUMAN = b.MANCALA_HUMAN;
        this.MANCALA_COMPUTER = b.MANCALA_COMPUTER;
        this.no_of_pits_of_player = b.no_of_pits_of_player;
    }

    /**
     * Checks if its Humans turn
     *
     * @return true if it is Humans turn
     */
    public boolean isHumansTurn() {
        return this.board_status.get() == BoardStatus.HUMAN_TURN;
    }
    /**
     * Checks if its Computers turn
     *
     * @return true if it is Computers turn
     */
    public boolean isComputersTurn() {
        return this.board_status.get() == BoardStatus.COMPUTER_TURN;
    }
    /**
     * Gets the List of Pits
     *
     * @return the Array of Pits
     */
    public byte[] getPits() {
        return pits;
    }

    /**
     * Sets the List of Pits
     *
     * @param pits the pit list
     */
   /* public void setPits(byte[] pits) {
        this.pits = pits;
    }
    */

    /**
     * Sets one specific Pit inside a PitList
     *
     * @param index   the Index of the Pit that is to be set
     * @param stones the new amount of Stones inside the Pit
     */
    public void setPitStones(int index, int stones) {
        this.pits[index] = (byte) stones;
    }

    /**
     * Gets the number of stones inside a pit
     *
     * @param i the index of the Pit
     * @return the number of stones in a certain pit
     */
    public byte getPitStones(int i) {
        return this.getPits()[i];
    }

    /**
     * Sets who's player turn it is or if it is game over
     *
     * @param board_status of the board game played
     */
    public void setBoardStatus(BoardStatus board_status) {
        this.board_status.set(board_status);
    }


    /**
     * Gets the current Score
     *
     * @return the number of Stones ahead from the view of the Human Player
     */
    public int getScore() {
        return (getPitStones(MANCALA_HUMAN) - getPitStones(MANCALA_COMPUTER));
    }

    /**
     * Gets the number of stones from the Humans Store Pit
     *
     * @return  the stones from the Humans Store Pit
     */
    public int getHumanBoardStore() {
        return this.getPitStones(MANCALA_HUMAN);
    }

    /**
     * Gets the number of stones from the Robots Store Pit
     *
     * @return the stones from the Robots Store Pit
     */
    public int getComputerBoardStore() {
        return this.getPitStones(MANCALA_COMPUTER);
    }

    /**
     * Makes a play move and adjusts the Board accordingly : capture the opponent stones and switch to the next player
     *
     * @param i the Index of the Pit that was selected for the Move.
     * @return a copy of the Board if the Board was changed. The initial Board remains unchanged.
     */
    public Board makeMove(int i) {
        assert this.board_status.get() != BoardStatus.GAME_OVER;

        Board nextBoard = new Board(this);
        byte stones = getPitStones(i);

        nextBoard.setPitStones(i, 0);
        while (stones != 0) {
            i = nextBoard.getNextPit(i);
            nextBoard.setPitStones(i, nextBoard.getPitStones(i)+1);
            --stones;
        }
        nextBoard.tryToCapture(i);
        if (nextBoard.isStorePit(i))
            nextBoard.switchPlayer();
        if (nextBoard.isGameOver())
            nextBoard.putInStoreAllPitsOfPlayer();

        return nextBoard;
    }

    /**
     * Make multiple moves in a row
     *
     * @param array the array containing the chosen indices
     * @return the changed board. The initial Board remains unchanged.
     */
    public Board makeMoves(int[] array) {
        Board b = new Board(this);
        for (int i : array) {
            b = b.makeMove(i);
        }
        return b;
    }

    /**
     * Gets the next Pit for the placement of stones depending on who's turn it is.
     *
     * @param i the Index of the current Pit
     * @return the Index of the Pit where the next stone will fall into.
     */
    public int getNextPit(int i) {
        i = (i == this.MANCALA_COMPUTER) ? 0 : i+1; //i=i+1;

        if (this.board_status.get() == BoardStatus.HUMAN_TURN && i == this.MANCALA_COMPUTER) return 0;
        if (this.board_status .get() == BoardStatus.COMPUTER_TURN && i == this.MANCALA_HUMAN) return this.MANCALA_HUMAN+1;

        return i;
    }


    /**
     * Checks for and executes the Capture Operation,
     * which takes all the stones from the opposite Pit when players pit is empty before the last stone is placed into it
     *
     * @param i the Index of Pit that the last Stone fell into
     */
    public void tryToCapture(int i) {
        // Checks if all conditions for the capture move are met
        if (isStorePit(i) && getPitStones(i) == 1 ) {
            if (isHumansTurn() && i < this.MANCALA_HUMAN) {
                setPitStones(this.MANCALA_HUMAN , getPitStones(this.MANCALA_HUMAN) + getPitStones(i) + getPitStones(oppositePitOf(i)));
                setPitStones(i, 0);
                setPitStones(oppositePitOf(i), 0);
            }
            else if (isComputersTurn() && i > this.MANCALA_HUMAN) {
                setPitStones(this.MANCALA_COMPUTER, getPitStones(this.MANCALA_COMPUTER) + getPitStones(i) + getPitStones(oppositePitOf(i)));
                setPitStones(i, 0);
                setPitStones(oppositePitOf(i), 0);
            }
        }
    }

    /**
     * Checks if the Pit of a certain index is empty
     *
     * @param i the index of the specified Pit
     * @return true if the Pit is empty (equal to 0)
     */
    public boolean isPitEmpty(int i) {
        return (getPitStones(i) == 0);
    }


    /**
     * Gets the Pit opposite to a certain Pit
     *
     * @param i the Index of a certain Pit
     * @return the Index of the Pit opposite of it
     */
    public int oppositePitOf(int i) {
        //if (i <= this.MANCALA_HUMAN){
        //    return no_of_pits_of_player + (no_of_pits_of_player-i);
        //}
        //else {
        //    if (i > this.MANCALA_HUMAN && i<this.pits.length) {
        //        return (i - (this.no_of_pits_of_player + 1));
        if(0 <= i && i < this.pits.length -1) {
            return 2*no_of_pits_of_player - i;
        }
        else {
                if( i == this.pits.length -1)
                    return i;
                else 
                    throw new ArrayIndexOutOfBoundsException("board number of pits");
        }
    }    


    /**
     * Checks if a certain Pit is a Store Pit
     *
     * @param i Index of a certain Pit
     * @return true if it's a Store Pit
     */
    public boolean isStorePit(int i) {
        return i != this.MANCALA_HUMAN && i != this.MANCALA_COMPUTER;
    }


    /**
     * Switches Players
     */
    public void switchPlayer() {
        assert this.board_status.get() != BoardStatus.GAME_OVER;

        if(this.board_status.get() == BoardStatus.COMPUTER_TURN){
            this.board_status.set(BoardStatus.HUMAN_TURN);
        }else if (this.board_status.get() == BoardStatus.HUMAN_TURN){
            this.board_status.set(BoardStatus.COMPUTER_TURN);
        }

    }


    /**
     * Checks if the Game is over
     *
     * @return true if the Game is over
     */
    public boolean isGameOver() {
        int humanStones = 0, computerStones = 0;
        for (int i = 0; i < this.no_of_pits_of_player; i++) {
            humanStones += getPitStones(i);
            computerStones += getPitStones(oppositePitOf(i));
        }
        if ((humanStones == 0 || computerStones == 0)){
            this.board_status.set(BoardStatus.GAME_OVER);
            return true;
        }else {
            return false;
        }


    }

    /**
     * Gets the index of the Pit where the last stone will fall into, if a move is being made on this Pit
     *
     * @param pit the Pit on which a move is being made
     * @return the index of the Pit where the last stone will fall into
     */
    int indexOfLastStone(int pit) {
        for (int stones = this.getPitStones(pit); stones > 0; --stones) {
            pit = getNextPit(pit);
        }
        return pit;
    }

    public int indexOfLastStoneForUIDisplay(int pit) {
        BoardStatus status = this.board_status.get();
        if (pit < this.MANCALA_HUMAN){
            this.board_status.set(BoardStatus.HUMAN_TURN);
            for (int stones = this.getPitStones(pit); stones > 0; --stones) {
                pit = getNextPit(pit);
            }

        }else if (pit > this.MANCALA_HUMAN){
            this.board_status.set(BoardStatus.COMPUTER_TURN);
            for (int stones = this.getPitStones(pit); stones > 0; --stones) {
                pit = getNextPit(pit);
            }
        }
        this.board_status.set(status);

        return pit;
    }


    /**
     * Puts all the stones from playing pits to the store pit of the respective player
     */
    public void putInStoreAllPitsOfPlayer() {
        for (int i = 0; i < this.no_of_pits_of_player; ++i) {
            setPitStones(this.MANCALA_HUMAN, getPitStones(this.MANCALA_HUMAN) + getPitStones(i));
            setPitStones(i, 0);
            setPitStones(this.MANCALA_COMPUTER, getPitStones(this.MANCALA_COMPUTER)+ getPitStones(oppositePitOf(i)));
            setPitStones(oppositePitOf(i), 0);
        }
    }


    public Vector<Byte> getSelectableSlots() {
        Vector<Byte> pits = new Vector<>();
        for (int i = this.getHumanStoreIndex()+1; i < this.getPits().length-1; i++) {
            if(this.getPitStones(i)>0){
                pits.add((byte) i);
            }
        }
        return pits;
    }

    public Vector<Byte> getSelectableSlots(Boolean sortMovesWithHeuristic) {
        assert sortMovesWithHeuristic == Boolean.TRUE;

        Vector<Byte> pits = new Vector<>();
        for (int i = this.getHumanStoreIndex()+1; i < this.getPits().length-1; i++) {
            if(this.getPitStones(i)>0){
                int pit_for_last_stone = this.indexOfLastStone(i);
                if(this.isHumansTurn()){
                    if(pit_for_last_stone == this.MANCALA_HUMAN || (pit_for_last_stone < this.MANCALA_HUMAN && this.isPitEmpty(i))){
                        pits.add(0,(byte)i); //if is a promising play move add it fist and than it is assumed that it will be used first by the algorithm
                    }
                }
                if(this.isComputersTurn()){
                    if(pit_for_last_stone == this.MANCALA_COMPUTER || (pit_for_last_stone < this.MANCALA_COMPUTER && this.isPitEmpty(i))){
                        pits.add(0,(byte)i);//if is a promising play move add it fist and than it is assumed that it will be used first by the algorithm
                    }
                }else {
                    pits.add((byte) i);
                }
            }
        }
        return pits;
    }



    /**
     * Prints the current Board to the console. For Test purposes only
     */
    public void printBoard() {
        if (this.board_status.get() == BoardStatus.HUMAN_TURN) {
            System.out.print("It's Humans Turn\n");
        } else if (this.board_status.get() == BoardStatus.COMPUTER_TURN) {
            System.out.print("It's Computers Turn\n");
        }else {
            System.out.print("Game is over\n");
        }
        System.out.print("\t");
        for (int i = this.MANCALA_COMPUTER-1; i > this.MANCALA_HUMAN; --i) {
            System.out.printf("%4d", getPitStones(i));
        }

        System.out.println();
        System.out.printf("%4d", getPitStones(this.MANCALA_COMPUTER));
        System.out.print("\t\t\t\t\t\t");
        System.out.printf("%4d", getPitStones(this.MANCALA_HUMAN));
        System.out.println();
        System.out.print("\t");
        for (int i = 0; i < this.MANCALA_HUMAN; ++i) {
            System.out.printf("%4d", getPitStones(i));
        }
        System.out.print("\n\n");
    }
}
