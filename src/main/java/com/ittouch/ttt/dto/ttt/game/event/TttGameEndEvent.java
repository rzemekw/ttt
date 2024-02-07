package com.ittouch.ttt.dto.ttt.game.event;

import com.ittouch.ttt.dto.ttt.game.TttGameWinningLineDTO;
import lombok.Data;

import java.util.Date;

@Data
public class TttGameEndEvent implements TttGameEvent {
    private TttGameEventType type = TttGameEventType.END;

    private String winnerName;

    private TttGameWinningLineDTO winningLine;
}
