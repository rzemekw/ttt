package com.ittouch.ttt.dto.ttt.game.event;

import lombok.Data;

import java.util.Date;

@Data
public class TttGameMoveEvent implements TttGameEvent {
    private TttGameEventType type = TttGameEventType.MOVE;

    private int x;
    private int y;

    private boolean isX;

    private Long xTimeLeft;
    private Long oTimeLeft;

    private Date eventDate;
}
