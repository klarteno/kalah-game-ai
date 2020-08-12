package org.openjfx;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;


public class BoardTest {

    @Test
    public void testConstructor() throws Exception {
        Board b = new Board(6, 6, true);
        assertEquals("[6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0]", Arrays.toString(b.getPits()));
        assertEquals(true, b.isHumansTurn());
    }

    @Test
    public void testGetScore() throws Exception {
        Board b = new Board(6, 6, true);
        b = b.makeMoves(new int[] {0, 4, 11, 5, 12});
        assertEquals(1, b.getScore());
    }

    @Test
    public void testMakeMoveHuman() throws Exception {
        Board b1 = new Board(6, 6, true);
        assertEquals("[6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0]", Arrays.toString(b1.getPits()));
        b1 = b1.makeMove(0);
        b1 = b1.makeMove(1);
        assertEquals("[0, 0, 8, 8, 8, 8, 2, 7, 7, 6, 6, 6, 6, 0]", Arrays.toString(b1.getPits()));
    }
    
    @Test
    public void testMakeMoveComputer() throws Exception {
        Board b1 = new Board(6, 6, false);
        assertEquals("[6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0]", Arrays.toString(b1.getPits()));
        b1 = b1.makeMove(7);
        b1 = b1.makeMove(12);
        assertEquals("[7, 7, 7, 7, 7, 7, 0, 0, 7, 7, 7, 7, 0, 2]", Arrays.toString(b1.getPits()));
    }

    @Test
    public void testCopyConstructor() throws Exception {
        Board b1 = new Board(6, 6, true);
        assertEquals("[6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0]", Arrays.toString(b1.getPits()));

        assertTrue(b1.isHumansTurn());
        b1 = b1.makeMove(0);
        assertTrue(b1.isHumansTurn());

        Board b2 = b1.makeMove(3);
        assertTrue(b2.isComputersTurn());
        assertTrue(b1.isHumansTurn());
        b1 = b1.makeMove(1);

        assertEquals("[0, 0, 8, 8, 8, 8, 2, 7, 7, 6, 6, 6, 6, 0]", Arrays.toString(b1.getPits()));
        assertEquals("[0, 7, 7, 0, 8, 8, 2, 7, 7, 7, 7, 6, 6, 0]", Arrays.toString(b2.getPits()));
    }

    @Test
    public void testCapture() throws Exception {
        Board b = new Board(4, 6, true);
        System.out.println(Arrays.toString(b.getPits()));
/*
        b = b.makeMove(5);
        System.out.println(Arrays.toString(b.getPits()));
        b = b.makeMove(9);
        System.out.println(Arrays.toString(b.getPits()));
        b = b.makeMove(1);
        System.out.println(Arrays.toString(b.getPits()));
 */
        b = b.makeMoves(new int[] {5, 9, 1});
        assertEquals("[5, 0, 5, 5, 5, 0, 7, 0, 5, 0, 5, 5, 5, 1]", Arrays.toString(b.getPits()));
    }

    @Test
    public void testGetLastStones() throws Exception {
        Board b = new Board(1, 3, true);
        b = b.makeMoves(new int[] {2, 1, 5, 0});
        assertEquals("[0, 0, 0, 4, 0, 0, 0, 2]", Arrays.toString(b.getPits()));
    }
    
    @Test
    public void testGameOver() throws Exception {
        Board b = new Board(1, 3, true);
        b = b.makeMoves(new int[] {2, 1, 5, 0});
        assertTrue(b.isGameOver());
    }

    @Test
    public void testIndexOfLastStone() throws Exception {
        Board b = new Board(6, 6, true);
        assertEquals(11, b.indexOfLastStone(5));
    }

    @Test
    public void testAll() throws Exception {
        Board a = new Board(6, 6, true);
        assertEquals("[6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0]", Arrays.toString(a.getPits()));
        assertEquals(0, a.getScore());
        a = a.makeMoves(new int[] {0, 5, 12, 5, 4, 12, 9, 5, 3, 8, 5, 2, 11, 3, 4, 9});
        assertEquals("[4, 11, 1, 0, 0, 4, 10, 12, 3, 0, 14, 1, 6, 6]", Arrays.toString(a.getPits()));
        assertEquals(4, a.getScore());
        assertFalse(a.isGameOver());


        //a = a.makeMoves(new int[] {7, 5, 11, 8, 10, 5, 4, 5, 1, 12, 4, 0, 9, 2});
        //assertEquals("[1, 2, 0, 6, 2, 5, 23, 6, 5, 1, 4, 1, 1, 15]", Arrays.toString(a.getPits()));
        //a = a.makeMoves(new int[] {12, 8, 12, 11, 12, 7, 12, 10, 4, 5, 12, 9, 12, 11});
        //assertEquals("[3, 4, 1, 6, 0, 0, 25, 1, 2, 0, 2, 0, 1, 27]", Arrays.toString(a.getPits()));
       // a = a.makeMoves(new int[] {3, 12, 10, 2, 8, 5, 4, 11, 12, 10});
       // assertEquals("[0, 0, 0, 0, 0, 0, 32, 0, 0, 0, 0, 0, 0, 40]", Arrays.toString(a.getPits()));
        //assertEquals(true, a.gameOver());
    }
}