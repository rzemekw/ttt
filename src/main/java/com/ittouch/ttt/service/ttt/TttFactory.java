package com.ittouch.ttt.service.ttt;

import com.ittouch.ttt.model.ttt.game.TttGame;
import com.ittouch.ttt.model.ttt.game.TttGameState;
import com.ittouch.ttt.model.ttt.game.TttGameStatus;
import com.ittouch.ttt.model.ttt.game.TttSquare;
import com.ittouch.ttt.model.ttt.tournament.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class TttFactory {
    public TttTournament createNewTournament(String name) {
        TttTournament tournament = new TttTournament();
        tournament.setName(name);
        tournament.setId(UUID.randomUUID().toString());

        var state = new TttTournamentState();
        state.setPlayers(new ConcurrentLinkedQueue<>());
        state.setStatus(TttTournamentStatus.WAITING_FOR_PLAYERS);

        return tournament;
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

    public TttTournamentPlayer createNewPlayer(String username) {
        var player = new TttTournamentPlayer();
        player.setPlayerName(username);
        return player;
    }

}
