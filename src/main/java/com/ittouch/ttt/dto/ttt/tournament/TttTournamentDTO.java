package com.ittouch.ttt.dto.ttt.tournament;

import lombok.Data;

@Data
public class TttTournamentDTO {
    private String id;
    private String name;
    private TttTournamentStateDTO state;
}

