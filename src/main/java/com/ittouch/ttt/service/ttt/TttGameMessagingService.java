package com.ittouch.ttt.service.ttt;

import com.ittouch.ttt.dto.ttt.game.TttGameWinningLineDTO;
import com.ittouch.ttt.dto.ttt.game.event.TttGameEndEvent;
import com.ittouch.ttt.dto.ttt.game.event.TttGameMoveEvent;
import com.ittouch.ttt.dto.ttt.game.event.TttGameStartEvent;
import com.ittouch.ttt.model.ttt.game.TttGame;
import com.ittouch.ttt.model.ttt.game.TttGameStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class TttGameMessagingService {
    private final SimpMessagingTemplate messagingTemplate;
    private String getGameTopic(String id) {
        return "/topic/ttt/games/" + id;
    }


    public void gameStarted(TttGame game) {
        var event = new TttGameStartEvent();
        event.setEventDate(new Date());
        messagingTemplate.convertAndSend(getGameTopic(game.getId()), event);
    }

    public void playerMoved(TttGame game, int x, int y, boolean isX) {
        var event = new TttGameMoveEvent();
        event.setEventDate(new Date());
        event.setX(x);
        event.setY(y);
        event.setX(isX);
        event.setXTimeLeft(game.getState().getXTimeLeft());
        event.setOTimeLeft(game.getState().getOTimeLeft());

        messagingTemplate.convertAndSend(getGameTopic(game.getId()), event);
    }

    public void gameEnded(TttGame game, TttGameWinningLineDTO winningLine) {
        var event = new TttGameEndEvent();
        event.setWinningLine(winningLine);
        if (game.getState().getStatus().equals(TttGameStatus.X_WON)) {
            event.setWinnerName(game.getXPlayerName());
        } else if (game.getState().getStatus().equals(TttGameStatus.O_WON)) {
            event.setWinnerName(game.getOPlayerName());
        }
        messagingTemplate.convertAndSend(getGameTopic(game.getId()), event);
    }

}