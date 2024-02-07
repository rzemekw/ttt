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
public class TttGameMessagingService {
    private final SimpMessagingTemplate messagingTemplate;
    private final TttMappingService mappingService;
    private String getGameTopic(String id) {
        return "/topic/ttt/games/" + id;
    }


    public void gameStarted(TttGame game) {
        var event = new TttTournamentTournamentClosedEvent();
        event.setTournament(mappingService.mapToDto(tournament));
        messagingTemplate.convertAndSend(getGameTopic(game.getId()), event);
    }

    public void tournamentStarted(TttTournament tournament) {
        var event = new TttTournamentTournamentStartedEvent();
        messagingTemplate.convertAndSend(getGameTopic(game.getId()), event);
    }

}