package com.ittouch.ttt.dto.ttt.tournament.event;

import lombok.Data;

@Data
public class TttTournamentGameEndedEvent implements TttTournamentEvent {
    private TttTournamentEventType type = TttTournamentEventType.GAME_ENDED;

    private String gameId;
    private int roundIndex;
    private int inRoundIndex;

    private boolean xWon;
}
