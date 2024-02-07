package com.ittouch.ttt.dto.ttt.game;

import lombok.Data;

import java.util.Date;

@Data
public class TttGameStateDTO {
    private boolean xIsNext;
    private int[][] board;
    private long xTimeLeft;
    private long oTimeLeft;
    private Date dateOfState;
    private String status;
}
