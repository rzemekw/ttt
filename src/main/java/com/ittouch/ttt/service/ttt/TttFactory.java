package com.ittouch.ttt.service.ttt;

import com.ittouch.ttt.model.ttt.game.TttGame;
import com.ittouch.ttt.model.ttt.game.TttGameState;
import com.ittouch.ttt.model.ttt.game.TttGameStatus;
import com.ittouch.ttt.model.ttt.game.TttSquare;
import com.ittouch.ttt.model.ttt.tournament.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class TttFactory {
    public TttTournament createNewTournament(String name) {
        TttTournament tournament = new TttTournament();
        tournament.setName(name);
        tournament.setId(UUID.randomUUID().toString());

        var state = new TttTournamentState();
        state.setPlayers(new ConcurrentHashMap<>());
        state.setStatus(TttTournamentStatus.WAITING_FOR_PLAYERS);
        tournament.setState(state);

        mockPlayers(tournament);

        return tournament;
    }

    private void mockPlayers(TttTournament tournament) {
        var state = tournament.getState();
        var players = state.getPlayers();
        players.put("player1", createNewPlayer("player1", "session1"));
        players.put("player2", createNewPlayer("player2", "session2"));
        players.put("player3", createNewPlayer("player3", "session3"));
        players.put("player4", createNewPlayer("player4", "session4"));
        players.put("player5", createNewPlayer("player5", "session5"));
        players.put("player6", createNewPlayer("player6", "session6"));
        players.put("player7", createNewPlayer("player7", "session7"));
        players.put("player8", createNewPlayer("player8", "session8"));
        players.put("player9", createNewPlayer("player9", "session9"));
        players.put("player10", createNewPlayer("player10", "session10"));
        players.put("player11", createNewPlayer("player11", "session11"));
        players.put("player12", createNewPlayer("player12", "session12"));
        players.put("player13", createNewPlayer("player13", "session13"));
        players.put("player14", createNewPlayer("player14", "session14"));
        players.put("player15", createNewPlayer("player15", "session15"));
        players.put("player16", createNewPlayer("player16", "session16"));
        players.put("player17", createNewPlayer("player17", "session17"));
        players.put("player18", createNewPlayer("player18", "session18"));
        players.put("player19", createNewPlayer("player19", "session19"));
        players.put("player20", createNewPlayer("player20", "session20"));
        players.put("player21", createNewPlayer("player21", "session21"));
        players.put("player22", createNewPlayer("player22", "session22"));
        players.put("player23", createNewPlayer("player23", "session23"));
    }

    public TttTournamentGame createNewTournamentGame(int round, int gameNumber, String playerX, String playerO) {
        var tournamentGame = new TttTournamentGame();
        tournamentGame.setRoundIndex(round);
        tournamentGame.setInRoundIndex(gameNumber);

        var game = createNewGame();
        tournamentGame.setGame(game);
        game.setXPlayerName(playerX);
        game.setOPlayerName(playerO);

        return tournamentGame;
    }

    public TttGame createNewGame() {
        var game = new TttGame();
        game.setId(UUID.randomUUID().toString());

        var state = new TttGameState();
        state.setXIsNext(true);
        var board = new TttSquare[3][3];
        for (int i = 0; i < 3; i++) {
            board[i] = new TttSquare[3];
            for (int j = 0; j < 3; j++) {
                board[i][j] = TttSquare.EMPTY;
            }
        }
        state.setBoard(board);
        state.setStatus(TttGameStatus.NOT_STARTED);

        return game;
    }

    public TttTournamentPlayer createNewPlayer(String username, String sessionId) {
        var player = new TttTournamentPlayer();
        player.setPlayerName(username);
        player.setSessionId(sessionId);
        return player;
    }

}
