package com.ittouch.ttt.dto.ttt.tournament.event;

import com.ittouch.ttt.dto.ttt.tournament.TttTournamentDTO;
import lombok.Data;

@Data
public class TttTournamentTournamentClosedEvent implements TttTournamentEvent {
    private TttTournamentEventType type = TttTournamentEventType.TOURNAMENT_CLOSED;

    private TttTournamentDTO tournament;
}
