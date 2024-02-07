package com.ittouch.ttt.dto.ttt.game;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TttGameWinningLineDTO {
    private int x1;
    private int y1;
    private int x2;
    private int y2;
}
