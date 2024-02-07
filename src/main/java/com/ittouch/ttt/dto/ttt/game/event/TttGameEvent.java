package com.ittouch.ttt.dto.ttt.game.event;

import lombok.Data;

public interface TttGameEvent {
    TttGameEventType getType();
}
