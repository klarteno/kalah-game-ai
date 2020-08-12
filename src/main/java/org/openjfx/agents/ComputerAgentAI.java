package org.openjfx.agents;

import org.openjfx.Board;
import org.openjfx.Game;

public interface ComputerAgentAI {
    public byte[] playComputer(Game game);

    Byte find_next_move(Board game_board);
}
