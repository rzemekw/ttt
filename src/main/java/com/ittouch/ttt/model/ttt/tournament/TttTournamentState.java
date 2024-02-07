package com.ittouch.ttt.model.ttt.tournament;

import lombok.Data;

import java.util.Collection;
import java.util.List;

@Data
public class TttTournamentState {
    private Collection<TttTournamentPlayer> players;
    private List<List<TttTournamentGame>> games;
    private TttTournamentStatus status;
}

