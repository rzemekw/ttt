package com.ittouch.ttt.model.ttt.game;

import lombok.Data;

@Data
public class TttGame {
    private String id;
    private TttGameState state;
    private String xPlayerName;
    private String oPlayerName;

    private boolean xPlayerJoined;
    private boolean oPlayerJoined;
}
