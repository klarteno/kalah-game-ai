package org.openjfx;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

/**
 * Created by pietz on 15/10/15.
 */
public class GameTest {

    @Test
    public void testBuildTreeFromGame() throws Exception {

        Game game = new Game(5, 6, 6, 1);
        assertEquals("[6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0]", Arrays.toString(game.getBoard().getPits()));
    }
    
    @Test
    public void testTakeTurnHuman() throws Exception {

        Game game = new Game(5, 6, 6, 1);
        assertEquals("[6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0]", Arrays.toString(game.getBoard().getPits()));

    }

    @Test
    public void testTakeTurnRobot() throws Exception {

        Game game = new Game(5, 6, 6, 1);
        assertEquals("[6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0]", Arrays.toString(game.getBoard().getPits()));
    }

    @Test
    public void testComputerPlaysGame() throws Exception {
        Game game = new Game(5, 6, 6, 1);
        game.kalah_board.setPitStones(7,0);
        game.kalah_board.setPitStones(8,0);
        game.kalah_board.setPitStones(9,0);
        game.kalah_board.setPitStones(10,0);
        game.kalah_board.setPitStones(11,0);
        game.kalah_board.setPitStones(12,6);
        game.kalah_board.setPitStones(13,30);

        assertEquals("[6, 6, 6, 6, 6, 6, " +
                "0, " +
                "0, 0, 0, 0, 0, 6," +
                " 30]", Arrays.toString(game.getBoard().getPits()));

        game.getBoard().setBoardStatus(Board.BoardStatus.COMPUTER_TURN);
        assertTrue(game.getBoard().isComputersTurn());

        game.playComputer();

        assertEquals( game.getBoard().getPitStones(12),0);
        assertTrue(game.getBoard().isGameOver());
        assertEquals("[0, 0, 0, 0, 0, 0, " +
                "41, " +
                "0, 0, 0, 0, 0, 0," +
                " 31]", Arrays.toString(game.getBoard().getPits()));

        assertEquals(game.kalah_board.getComputerBoardStore(),31);
        assertEquals(game.kalah_board.getHumanBoardStore(),41);


    }



}