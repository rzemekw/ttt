package com.ittouch.ttt.api;

import com.ittouch.ttt.dto.ttt.game.TttGameDTO;
import com.ittouch.ttt.dto.ttt.game.TttGameMoveDTO;
import com.ittouch.ttt.dto.ttt.tournament.CreateTttTournamentDTO;
import com.ittouch.ttt.dto.ttt.tournament.TttTournamentDTO;
import com.ittouch.ttt.dto.ttt.tournament.TttTournamentListItemDTO;
import com.ittouch.ttt.service.authentication.AuthenticationService;
import com.ittouch.ttt.service.ttt.TttService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public List<TttTournamentListItemDTO> getTournaments() {
        return tttService.getTournaments();
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

    @PostMapping("/{id}/games/{gameId}/move")
    public void makeMove(@PathVariable String id, @PathVariable String gameId, @RequestBody TttGameMoveDTO dto) {
        var username = authenticationService.getCurrentUserName();
        tttService.playerMoved(username, id, gameId, dto.getX(), dto.getY());
    }
    @PostMapping("/{id}/games/{gameId}/join")
    public void joinGame(@PathVariable String id, @PathVariable String gameId) {
        var username = authenticationService.getCurrentUserName();
        tttService.joinGame(username, id, gameId);
    }

    @GetMapping("/{id}/games/{gameId}")
    public TttGameDTO getGame(@PathVariable String id, @PathVariable String gameId) {
        return tttService.getGame(id, gameId);
    }
}
