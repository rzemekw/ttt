package com.ittouch.ttt.dto.ttt.tournament;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TttTournamentStateDTO {
    private List<TttTournamentPlayerDTO> players;
    private List<List<String>> gameIds;
    private Map<String, TttTournamentGameDTO> games;
    private String status;
}

