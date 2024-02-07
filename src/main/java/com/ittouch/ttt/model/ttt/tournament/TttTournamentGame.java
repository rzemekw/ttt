package com.ittouch.ttt.model.ttt.tournament;

import com.ittouch.ttt.model.ttt.game.TttGame;
import lombok.Data;

@Data
public class TttTournamentGame {
    private TttGame game;
    private int roundIndex;
    private int inRoundIndex;
}
