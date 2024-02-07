package com.ittouch.ttt.model.ttt.game;

import lombok.Data;

import java.util.Date;

@Data
public class TttGameState {
    private boolean xIsNext;
    private TttSquare[][] board;
    private long xTimeLeft;
    private long oTimeLeft;
    private Date dateOfState;
    private TttGameStatus status;
}
