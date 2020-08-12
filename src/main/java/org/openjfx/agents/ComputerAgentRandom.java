package org.openjfx.agents;

import org.openjfx.Board;
import org.openjfx.Game;

import java.util.Random;
import java.util.Vector;

public class ComputerAgentRandom implements ComputerAgentAI {

    @Override
    public byte[] playComputer(Game game) {
        Random rand = new Random();
        Vector<Byte> pits = game.getBoard().getSelectableSlots();
        if(!(pits.size()>0)){
            return new byte[game.kalah_board.getPits().length];
        }
        int index =  pits.get(rand.nextInt(pits.size()));

        Board board = game.kalah_board.makeMove(index);
        byte[] toUpdateButtons = game.getIndexesBoardChanges(game.kalah_board,board);
        game.kalah_board = board;

        return toUpdateButtons;
    }

    @Override
    public Byte find_next_move(Board game_board) {
        return null;
    }

    @Override
    public String toString() {
        return "Random";
    }


}
