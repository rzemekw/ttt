package com.ittouch.ttt.dto.ttt.tournament.event;

import lombok.Data;

import java.util.Date;

@Data
public class TttTournamentGameScheduledEvent implements TttTournamentEvent {
    private TttTournamentEventType type = TttTournamentEventType.GAME_SCHEDULED;

    private String gameId;
    private int roundIndex;
    private int inRoundIndex;

    private String xPlayerName;
    private String oPlayerName;
}
