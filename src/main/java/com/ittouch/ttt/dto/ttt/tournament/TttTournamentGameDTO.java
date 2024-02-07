package com.ittouch.ttt.dto.ttt.tournament;

import lombok.Data;

@Data
public class TttTournamentGameDTO {
    private String id;
    private String xPlayerName;
    private String oPlayerName;
    private String status;
    private int roundIndex;
    private int inRoundIndex;
}
