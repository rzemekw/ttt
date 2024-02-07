package com.ittouch.ttt.dto.ttt.tournament.event;

import lombok.Data;

import java.util.Date;

@Data
public class TttTournamentTournamentStartedEvent implements TttTournamentEvent {
    private TttTournamentEventType type = TttTournamentEventType.TOURNAMENT_STARTED;
}
