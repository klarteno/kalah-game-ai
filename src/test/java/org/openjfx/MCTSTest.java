package org.openjfx;

import org.junit.jupiter.api.Test;
import org.openjfx.agents.ComputerAgentAI;
import org.openjfx.agents.ComputerAgentMCTS;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

public class MCTSTest {


    @Test
    public void testConstructor() throws Exception {
        Board b = new Board(6, 6, true);
        assertEquals("[6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0]", Arrays.toString(b.getPits()));
        assertEquals(true, b.isHumansTurn());

        Game game = new Game(5, 6, 6, 1);
        assertEquals("[6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0]", Arrays.toString(game.getBoard().getPits()));
        assertTrue(game.kalah_board.isHumansTurn());

        byte[] res1 = game.playHuman(2);
        assertTrue(game.kalah_board.isComputersTurn());

        byte[] res2 = game.playComputer();
        assertNotNull(res2);


    }

}
