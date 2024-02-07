package com.ittouch.ttt.model.ttt.tournament;

import lombok.Data;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Data
public class TttTournamentState {
    private Collection<TttTournamentPlayer> players;
    private List<List<String>> gameIds;
    private Map<String, TttTournamentGame> games;
    private TttTournamentStatus status;
}

