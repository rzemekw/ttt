package com.ittouch.ttt.service.ttt;

import com.ittouch.ttt.dto.ttt.game.TttGameDTO;
import com.ittouch.ttt.dto.ttt.game.TttGameStateDTO;
import com.ittouch.ttt.dto.ttt.tournament.*;
import com.ittouch.ttt.model.ttt.game.TttGame;
import com.ittouch.ttt.model.ttt.game.TttGameState;
import com.ittouch.ttt.model.ttt.game.TttSquare;
import com.ittouch.ttt.model.ttt.tournament.TttTournament;
import com.ittouch.ttt.model.ttt.tournament.TttTournamentGame;
import com.ittouch.ttt.model.ttt.tournament.TttTournamentPlayer;
import com.ittouch.ttt.model.ttt.tournament.TttTournamentState;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TttMappingService {
    public TttGameDTO mapToDto(TttGame game) {
        var result = new TttGameDTO();
        result.setId(game.getId());
        result.setState(mapToDto(game.getState()));
        result.setXPlayerName(game.getXPlayerName());
        result.setOPlayerName(game.getOPlayerName());
        return result;
    }

    private TttGameStateDTO mapToDto(TttGameState state) {
        var result = new TttGameStateDTO();
        result.setBoard(mapToDto(state.getBoard()));
        result.setDateOfState(state.getDateOfState());
        result.setOTimeLeft(state.getOTimeLeft());
        result.setXTimeLeft(state.getXTimeLeft());
        result.setXIsNext(state.isXIsNext());
        result.setStatus(state.getStatus().name());
        return result;
    }

    private int[][] mapToDto(TttSquare[][] board) {
        var result = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result[i][j] = board[i][j].ordinal();
            }
        }
        return result;
    }

    public TttTournamentDTO mapToDto(TttTournament tournament) {
        var result = new TttTournamentDTO();
        result.setId(tournament.getId());
        result.setName(tournament.getName());
        result.setState(mapToDto(tournament.getState()));
        return result;
    }

    public TttTournamentStateDTO mapToDto(TttTournamentState state) {
        var result = new TttTournamentStateDTO();
        result.setPlayers(mapToDto(state.getPlayers().values()));
        result.setGames(mapToTttTournamentGamesDTO(state.getGames()));
        result.setGameIds(state.getGameIds());
        result.setStatus(state.getStatus().name());
        return result;
    }

    private Map<String, TttTournamentGameDTO> mapToTttTournamentGamesDTO(Map<String, TttTournamentGame> games) {
        if (games == null) {
            return null;
        }
        return games.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> mapToTttTournamentGameDto(e.getValue())));
    }

    private TttTournamentGameDTO mapToTttTournamentGameDto(TttTournamentGame game) {
        var result = new TttTournamentGameDTO();
        result.setId(game.getGame().getId());
        result.setXPlayerName(game.getGame().getXPlayerName());
        result.setOPlayerName(game.getGame().getOPlayerName());
        result.setRoundIndex(game.getRoundIndex());
        result.setInRoundIndex(game.getInRoundIndex());
        return result;
    }

    private TttTournamentPlayerDTO mapToDto(TttTournamentPlayer player) {
        var result = new TttTournamentPlayerDTO();
        result.setPlayerName(player.getPlayerName());
        return result;
    }

    private List<TttTournamentPlayerDTO> mapToDto(Collection<TttTournamentPlayer> players) {
        return players.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public TttTournamentListItemDTO mapToListItemDto(TttTournament tournament) {
        var result = new TttTournamentListItemDTO();
        result.setId(tournament.getId());
        result.setName(tournament.getName());
        result.setStatus(tournament.getState().getStatus().name());
        return result;
    }
}
