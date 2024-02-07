package com.ittouch.ttt.service.ttt;

import com.ittouch.ttt.dto.ttt.tournament.TttTournamentDTO;
import com.ittouch.ttt.model.ttt.game.TttGame;
import com.ittouch.ttt.model.ttt.game.TttGameStatus;
import com.ittouch.ttt.model.ttt.tournament.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TttService {
    private final TttMappingService mappingService;
    private final TttFactory factory;

    private final Map<String, TttTournament> tournaments = new ConcurrentHashMap<>();

    public TttTournamentDTO createTournament(String name) {
        var tournament = factory.createNewTournament(name);
        tournaments.put(tournament.getId(), tournament);
        return mappingService.mapToDto(tournament);
    }

    public TttTournamentDTO joinTournament(String id, String username) {
        var tournament = tournaments.get(id);
        if (tournament == null) {
            throw new IllegalArgumentException("Tournament not found"); //TODo lepszy wyjatek
        }
        if (!tournament.getState().getStatus().equals(TttTournamentStatus.WAITING_FOR_PLAYERS)) {
            tournament.getState().getPlayers().add(factory.createNewPlayer(username));
        } // todo exception + additional method for spectate
        return mappingService.mapToDto(tournament);
    }

    public void closeTournament(String id) {
        var tournament = tournaments.get(id);
        if (tournament == null) {
            throw new IllegalArgumentException("Tournament not found"); //TODo lepszy wyjatek
        }
        if (!tournament.getState().getStatus().equals(TttTournamentStatus.WAITING_FOR_PLAYERS)) {
            throw new IllegalArgumentException("Tournament already started"); //TODo lepszy wyjatek
        }
        if (tournament.getState().getPlayers().size() < 2) {
            throw new IllegalArgumentException("Not enough players"); //TODo lepszy wyjatek
        }

        closeTournament(tournament);
    }

    public void startTournament(String id) {
        var tournament = tournaments.get(id);
        if (tournament == null) {
            throw new IllegalArgumentException("Tournament not found"); //TODo lepszy wyjatek
        }
        if (!tournament.getState().getStatus().equals(TttTournamentStatus.NOT_STARTED)) {
            throw new IllegalArgumentException("Tournament already started"); //TODo lepszy wyjatek
        }
        if (tournament.getState().getPlayers().size() < 2) {
            throw new IllegalArgumentException("Not enough players"); //TODo lepszy wyjatek
        }
        startTournament(tournament);
    }

    //TODO move to some other place
    public void closeTournament(TttTournament tournament) {
        var state = tournament.getState();
        state.setStatus(TttTournamentStatus.NOT_STARTED);

        var shuffledPlayers = new ArrayList<>(state.getPlayers());
        Collections.shuffle(shuffledPlayers);

        var playersCount = shuffledPlayers.size();

        var games = new ArrayList<List<TttTournamentGame>>();

        int currentRoundGamesCount = findClosestPowerOfTwo(playersCount) / 2;
        int processedPlayers = 0;
        int currentRound = 0;
        while (currentRoundGamesCount > 0) {
            var round = new ArrayList<TttTournamentGame>();
            for (int i = 0; i < currentRoundGamesCount; i++) {
                var xPlayer = Optional.of(processedPlayers++)
                        .filter(index -> index < playersCount)
                        .map(shuffledPlayers::get)
                        .map(TttTournamentPlayer::getPlayerName)
                        .orElse(null);
                var oPlayer = Optional.of(processedPlayers++)
                        .filter(index -> index < playersCount)
                        .map(shuffledPlayers::get)
                        .map(TttTournamentPlayer::getPlayerName)
                        .orElse(null);

                var game = factory.createNewTournamentGame(currentRound, i, xPlayer, oPlayer);
                round.add(game);
            }
            games.add(round);
            currentRoundGamesCount /= 2;
        }

        state.setGames(Collections.unmodifiableList(games));
    }

    public void startTournament(TttTournament tournament) {
        var state = tournament.getState();
        state.setStatus(TttTournamentStatus.IN_PROGRESS);
        var firstRoundGames = state.getGames().get(0);
        firstRoundGames.forEach(game -> scheduleGame(game.getGame()));
    }

    public void scheduleGame(TttGame game) {
        var state = game.getState();
        state.setDateOfState(new Date());
        state.setXTimeLeft(30000);
        state.setOTimeLeft(30000);
        state.setStatus(TttGameStatus.SCHEDULED);
    }

    public void startGame(TttGame game) {
        var state = game.getState();
        state.setDateOfState(new Date());
        state.setStatus(TttGameStatus.IN_PROGRESS);
    }

    private static int findClosestPowerOfTwo(int number) {
        if (number <= 1) {
            return number;
        }

        int result = 1;
        while (result * 2 <= number) {
            result *= 2;
        }

        return result;
    }
}