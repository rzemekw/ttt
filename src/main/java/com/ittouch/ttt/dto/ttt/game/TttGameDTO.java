package com.ittouch.ttt.dto.ttt.game;

import lombok.Data;

@Data
public class TttGameDTO {
    private String id;
    private TttGameStateDTO state;
    private String xPlayerName;
    private String oPlayerName;
}
