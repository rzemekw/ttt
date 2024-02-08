package com.ittouch.ttt.dto.ttt.tournament.event;

import lombok.Data;

@Data
public class TttTournamentPlayerJoinedEvent implements TttTournamentEvent {
    private TttTournamentEventType type = TttTournamentEventType.PLAYER_JOINED;

    private String playerName;
}
