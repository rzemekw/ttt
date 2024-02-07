package com.ittouch.ttt.api;

import com.ittouch.ttt.dto.ttt.tournament.CreateTttTournamentDTO;
import com.ittouch.ttt.dto.ttt.tournament.TttTournamentDTO;
import com.ittouch.ttt.service.authentication.AuthenticationService;
import com.ittouch.ttt.service.ttt.TttService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ttt/tournaments")
@RequiredArgsConstructor
public class TttTournamentController {
    private final TttService tttService;
    private final AuthenticationService authenticationService;

    @PostMapping
    public TttTournamentDTO createTournament(@RequestBody CreateTttTournamentDTO dto) {
        return tttService.createTournament(dto.getName());
    }

    @PostMapping("/{id}/join")
    public TttTournamentDTO joinTournament(HttpServletRequest request, @PathVariable String id) {
        var username = authenticationService.getCurrentUserName();
        return tttService.joinTournament(id, username, request.getSession(false).getId());
    }

    @GetMapping("/{id}")
    public TttTournamentDTO getTournament(@PathVariable String id) {
        return tttService.getTournament(id);
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
