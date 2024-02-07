package com.ittouch.ttt.dto.ttt.tournament.event;

import lombok.Data;

@Data
public class TttTournamentTournamentFinishedEvent implements TttTournamentEvent {
    private TttTournamentEventType type = TttTournamentEventType.TOURNAMENT_FINISHED;

    private String winnerName;
}
