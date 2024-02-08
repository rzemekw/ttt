package com.ittouch.ttt.service.ttt;

import com.ittouch.ttt.dto.ttt.tournament.event.*;
import com.ittouch.ttt.model.ttt.game.TttGame;
import com.ittouch.ttt.model.ttt.game.TttGameStatus;
import com.ittouch.ttt.model.ttt.tournament.TttTournament;
import com.ittouch.ttt.model.ttt.tournament.TttTournamentGame;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TttTournamentMessagingService {
    private final SimpMessagingTemplate messagingTemplate;
    private final TttMappingService mappingService;

    private String getTournamentTopic(String id) {
        return "/topic/ttt/tournaments/" + id;
    }

    public void tournamentClosed(TttTournament tournament) {
        var event = new TttTournamentTournamentClosedEvent();
        event.setTournament(mappingService.mapToDto(tournament));
        messagingTemplate.convertAndSend(getTournamentTopic(tournament.getId()), event);
    }

    public void tournamentStarted(TttTournament tournament) {
        var event = new TttTournamentTournamentStartedEvent();
        messagingTemplate.convertAndSend(getTournamentTopic(tournament.getId()), event);
    }

    public void gameEnded(TttTournamentGame game, TttTournament tournament) {
        var event = new TttTournamentGameEndedEvent();
        event.setGameId(game.getGame().getId());
        event.setRoundIndex(game.getRoundIndex());
        event.setInRoundIndex(game.getInRoundIndex());
        if (game.getGame().getState().getStatus().equals(TttGameStatus.X_WON)) {
            event.setXWon(true);
        }
        messagingTemplate.convertAndSend(getTournamentTopic(tournament.getId()), event);
    }

    public void gameScheduled(TttTournamentGame game, TttTournament tournament) {
        var event = new TttTournamentGameScheduledEvent();
        event.setGameId(game.getGame().getId());
        event.setRoundIndex(game.getRoundIndex());
        event.setInRoundIndex(game.getInRoundIndex());
        event.setXPlayerName(game.getGame().getXPlayerName());
        event.setOPlayerName(game.getGame().getOPlayerName());

        messagingTemplate.convertAndSend(getTournamentTopic(tournament.getId()), event);
    }

    public void tournamentFinished(TttTournament tournament, TttGame game) {
        var event = new TttTournamentTournamentFinishedEvent();
        if (game.getState().getStatus().equals(TttGameStatus.X_WON)) {
            event.setWinnerName(game.getXPlayerName());
        } else {
            event.setWinnerName(game.getOPlayerName());
        }
        messagingTemplate.convertAndSend(getTournamentTopic(tournament.getId()), event);
    }

    public void playerJoined(TttTournament tournament, String playerName) {
        var event = new TttTournamentPlayerJoinedEvent();
        event.setPlayerName(playerName);
        messagingTemplate.convertAndSend(getTournamentTopic(tournament.getId()), event);
    }


}