package com.ittouch.ttt.api;

import com.ittouch.ttt.dto.ttt.tournament.TttTournamentDTO;
import com.ittouch.ttt.service.ttt.TttService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ttt/tournaments")
@RequiredArgsConstructor
public class TttTournamentController {
    private final TttService tttService;

    @PostMapping
    public TttTournamentDTO createTournament(String name) {
        return tttService.createTournament(name);
    }

    @RequestMapping("/join")
    public TttTournamentDTO joinTournament(String id, String username) {
        return tttService.joinTournament(id, username);
    }

    @RequestMapping("/close")
    public void closeTournament(String id) {
        tttService.closeTournament(id);
    }

    @RequestMapping("/start")
    public void startTournament(String id) {
        tttService.startTournament(id);
    }
}
