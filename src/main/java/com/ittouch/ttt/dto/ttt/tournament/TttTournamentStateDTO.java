package com.ittouch.ttt.dto.ttt.tournament;

import lombok.Data;

import java.util.List;

@Data
public class TttTournamentStateDTO {
    private List<TttTournamentPlayerDTO> players;
    private List<List<TttTournamentGameDTO>> games;
    private String status;
}

