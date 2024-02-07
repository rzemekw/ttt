package com.ittouch.ttt.model.ttt.tournament;

import lombok.Data;

@Data
public class TttTournament {
    private String id;
    private String name;
    private TttTournamentState state;
}

