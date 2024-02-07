package com.ittouch.ttt.api;

import com.ittouch.ttt.dto.ttt.tournament.TttTournamentDTO;
import com.ittouch.ttt.service.authentication.AuthenticationService;
import com.ittouch.ttt.service.ttt.TttService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ttt/tournaments")
@RequiredArgsConstructor
public class TttTournamentController {
    private final TttService tttService;
    private final AuthenticationService authenticationService;

    @PostMapping
    public TttTournamentDTO createTournament(String name) {
        return tttService.createTournament(name);
    }

    @PostMapping("/{id}/join")
    public TttTournamentDTO joinTournament(HttpServletRequest request, @PathVariable String id) {
        var username = authenticationService.getCurrentUserName();
        return tttService.joinTournament(id, username, request.getSession(false).getId());
    }

    @PostMapping("/{id}/close")
    public void closeTournament(@PathVariable String id) {
        tttService.closeTournament(id);
    }

    @PostMapping("/{id}/start")
    public void startTournament(@PathVariable String id) {
        tttService.startTournament(id);
    }
}
