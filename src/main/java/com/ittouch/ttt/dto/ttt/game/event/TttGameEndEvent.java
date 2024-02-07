package com.ittouch.ttt.dto.ttt.game.event;

import lombok.Data;

import java.util.Date;

@Data
public class TttGameEndEvent implements TttGameEvent {
    private TttGameEventType type = TttGameEventType.START;

    private String winnerName;

    private Date eventDate;
}
