package com.ittouch.ttt.model.ttt.tournament;

import lombok.Data;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Data
public class TttTournamentState {
    private Map<String, TttTournamentPlayer> players; // userName -> player
    private List<List<String>> gameIds;
    private Map<String, TttTournamentGame> games;
    private TttTournamentStatus status;
}

