package com.ittouch.ttt.service.ttt;

import com.ittouch.ttt.dto.ttt.game.TttGameWinningLineDTO;
import com.ittouch.ttt.dto.ttt.tournament.TttTournamentDTO;
import com.ittouch.ttt.model.ttt.game.TttGame;
import com.ittouch.ttt.model.ttt.game.TttGameStatus;
import com.ittouch.ttt.model.ttt.game.TttSquare;
import com.ittouch.ttt.model.ttt.tournament.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class TttService {
    private final TttMappingService mappingService;
    private final TttFactory factory;
    private final TttGameMessagingService gameMessagingService;
    private final TttTournamentMessagingService tournamentMessagingService;
    private final TttGameTimerService gameTimerService;

    private final Map<String, TttTournament> tournaments = new ConcurrentHashMap<>();

    public TttTournamentDTO createTournament(String name) {
        var tournament = factory.createNewTournament(name);
        tournaments.put(tournament.getId(), tournament);
        return mappingService.mapToDto(tournament);
    }

    public TttTournamentDTO joinTournament(String id, String username, String sessionId) {
        var tournament = tournaments.get(id);
        if (tournament == null) {
            throw new IllegalArgumentException("Tournament not found");
        }
        if (!tournament.getState().getStatus().equals(TttTournamentStatus.WAITING_FOR_PLAYERS)) {
            var prevPlayer = tournament.getState().getPlayers().get(username);
            if (prevPlayer == null) {
                tournament.getState().getPlayers().put(username, factory.createNewPlayer(username));
            } else if (!prevPlayer.getSessionId().equals(sessionId)) {
                throw new IllegalArgumentException("Player already joined with different session");
            }
        } // todo exception + additional method for spectate
        return mappingService.mapToDto(tournament);
    }

    public void closeTournament(String id) {
        var tournament = tournaments.get(id);
        if (tournament == null) {
            throw new IllegalArgumentException("Tournament not found");
        }
        if (!tournament.getState().getStatus().equals(TttTournamentStatus.WAITING_FOR_PLAYERS)) {
            throw new IllegalArgumentException("Tournament already started");
        }
        if (tournament.getState().getPlayers().size() < 2) {
            throw new IllegalArgumentException("Not enough players");
        }

        closeTournament(tournament);
    }

    public void startTournament(String id) {
        var tournament = tournaments.get(id);
        if (tournament == null) {
            throw new IllegalArgumentException("Tournament not found");
        }
        if (!tournament.getState().getStatus().equals(TttTournamentStatus.NOT_STARTED)) {
            throw new IllegalArgumentException("Tournament already started");
        }
        if (tournament.getState().getPlayers().size() < 2) {
            throw new IllegalArgumentException("Not enough players");
        }
        startTournament(tournament);
    }

    //TODO move to some other place
    public void closeTournament(TttTournament tournament) {
        var state = tournament.getState();
        state.setStatus(TttTournamentStatus.NOT_STARTED);

        var shuffledPlayers = new ArrayList<>(state.getPlayers().keySet());
        Collections.shuffle(shuffledPlayers);

        var playersCount = shuffledPlayers.size();

        var gameIds = new ArrayList<List<String>>();
        var games = new HashMap<String, TttTournamentGame>();

        int currentRoundGamesCount = findClosestPowerOfTwo(playersCount) / 2;
        int processedPlayers = 0;
        int currentRound = 0;
        while (currentRoundGamesCount > 0) {
            var roundGameIds = new ArrayList<String>();
            for (int i = 0; i < currentRoundGamesCount; i++) {
                var xPlayer = Optional.of(processedPlayers++)
                        .filter(index -> index < playersCount)
                        .map(shuffledPlayers::get)
                        .orElse(null);
                var oPlayer = Optional.of(processedPlayers++)
                        .filter(index -> index < playersCount)
                        .map(shuffledPlayers::get)
                        .orElse(null);

                var game = factory.createNewTournamentGame(currentRound, i, xPlayer, oPlayer);
                var gameId = game.getGame().getId();
                games.put(gameId, game);
                roundGameIds.add(gameId);
            }
            gameIds.add(Collections.unmodifiableList(roundGameIds));
            currentRoundGamesCount /= 2;
        }

        state.setGameIds(Collections.unmodifiableList(gameIds));
        state.setGames(Collections.unmodifiableMap(games));

        tournamentMessagingService.tournamentClosed(tournament);
    }

    public void startTournament(TttTournament tournament) {
        var state = tournament.getState();
        state.setStatus(TttTournamentStatus.IN_PROGRESS);
        var firstRoundGameIds = state.getGameIds().get(0);
        var firstRoundGames = firstRoundGameIds.stream()
                .map(state.getGames()::get)
                .toList();
        firstRoundGames.forEach(game -> scheduleGame(game, tournament));

        tournamentMessagingService.tournamentStarted(tournament);
    }

    public void playerMoved(String playerName, String tournamentId, String gameId, int x, int y) {
        var tournament = tournaments.get(tournamentId);
        if (tournament == null) {
            throw new IllegalArgumentException("Tournament not found");
        }
        if (!tournament.getState().getStatus().equals(TttTournamentStatus.IN_PROGRESS)) {
            throw new IllegalArgumentException("Tournament not in progress");
        }
        var game = tournament.getState().getGames().get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found");
        }
        var gameState = game.getGame().getState();
        if (!gameState.getStatus().equals(TttGameStatus.IN_PROGRESS)) {
            throw new IllegalArgumentException("Game not in progress");
        }
        boolean isXPlayer = playerName.equals(game.getGame().getXPlayerName());
        if (isXPlayer && !gameState.isXIsNext() || !isXPlayer && gameState.isXIsNext()) {
            throw new IllegalArgumentException("Not your turn");
        }

        var board = gameState.getBoard();
        var square = board[x][y];
        if (square != TttSquare.EMPTY) {
            throw new IllegalArgumentException("Square already taken");
        }

        gameTimerService.cancelTaskForGame(gameId);

        board[x][y] = isXPlayer ? TttSquare.X : TttSquare.O;
        gameState.setDateOfState(new Date());
        gameState.setXIsNext(!isXPlayer);

        var timeSinceLastMove = new Date().getTime() - gameState.getDateOfState().getTime();
        if (isXPlayer) {
            gameState.setXTimeLeft(gameState.getXTimeLeft() - timeSinceLastMove);
            gameTimerService.startGameTimer(gameId, gameState.getOTimeLeft(), () -> playerTimedOut(tournament, game));
        } else {
            gameState.setOTimeLeft(gameState.getOTimeLeft() - timeSinceLastMove);
            gameTimerService.startGameTimer(gameId, gameState.getXTimeLeft(), () -> playerTimedOut(tournament, game));
        }

        gameMessagingService.playerMoved(game.getGame(), x, y, isXPlayer);

        var winningLine = findWinningLine(board);
        if (winningLine != null) {
            gameState.setStatus(isXPlayer ? TttGameStatus.X_WON : TttGameStatus.O_WON);
            gameMessagingService.gameEnded(game.getGame(), winningLine);
        } else if (isBoardFull(board)) {
            var random = new Random();
            if (random.nextBoolean()) {
                gameState.setStatus(TttGameStatus.X_WON);
            } else {
                gameState.setStatus(TttGameStatus.O_WON);
            }
            gameMessagingService.gameEnded(game.getGame(), null);
        }
    }

    private void processGameEndedForTournament(TttTournament tournament, TttTournamentGame game) {
        tournamentMessagingService.gameEnded(game, tournament);

        var roundsCount = tournament.getState().getGameIds().size();
        var roundIndex = game.getRoundIndex();
        if (roundsCount == roundIndex + 1) {
            tournamentMessagingService.tournamentFinished(tournament, game.getGame());
            tournaments.remove(tournament.getId());
            return;
        }

        var winnerName = game.getGame().getState().getStatus() == TttGameStatus.X_WON
                ? game.getGame().getXPlayerName()
                : game.getGame().getOPlayerName();

        int nextGameRoundIndex = roundIndex + 1;
        int nextGameInRoundIndex = game.getInRoundIndex() / 2;

        var nextGameId = tournament.getState().getGameIds().get(nextGameRoundIndex).get(nextGameInRoundIndex);
        var nextGame = tournament.getState().getGames().get(nextGameId);

        if (!nextGame.getGame().getState().getStatus().equals(TttGameStatus.NOT_STARTED)) {
            log.warn("processGameEndedForTournament - Next game already started - SOMETHING WENT WRONG");
            return;
        }

        if (nextGame.getGame().getXPlayerName() == null) {
            log.info("processGameEndedForTournament - xPlayerName is null");
            nextGame.getGame().setXPlayerName(winnerName);
        } else {
            log.info("processGameEndedForTournament - xPlayerName is not null, scheduling game");
            nextGame.getGame().setOPlayerName(winnerName);
            scheduleGame(nextGame, tournament);
        }
    }

    private void playerTimedOut(TttTournament tournament, TttTournamentGame game) {
        var gameState = game.getGame().getState();
        gameState.setStatus(gameState.isXIsNext() ? TttGameStatus.O_WON : TttGameStatus.X_WON);
        gameMessagingService.gameEnded(game.getGame(), null);
        processGameEndedForTournament(tournament, game);
    }

    private boolean isBoardFull(TttSquare[][] board) {
        for (var row : board) {
            for (var square : row) {
                if (square == TttSquare.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    private TttGameWinningLineDTO findWinningLine(TttSquare[][] board) {
        // Check rows and columns
        for (int i = 0; i < 3; i++) {
            // Check row
            if (board[i][0] != TttSquare.EMPTY && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return new TttGameWinningLineDTO(i, 0, i, 2);
            }
            // Check column
            if (board[0][i] != TttSquare.EMPTY && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                return new TttGameWinningLineDTO(0, i, 2, i);
            }
        }

        // Check diagonals
        if (board[0][0] != TttSquare.EMPTY && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return new TttGameWinningLineDTO(0, 0, 2, 2);
        }
        if (board[0][2] != TttSquare.EMPTY && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return new TttGameWinningLineDTO(0, 2, 2, 0);
        }

        // No winner
        return null;
    }

    private void scheduleGame(TttTournamentGame tournamentGame, TttTournament tournament) {
        var game = tournamentGame.getGame();
        var state = game.getState();
        state.setDateOfState(new Date());
        state.setXTimeLeft(30000);
        state.setOTimeLeft(30000);
        state.setStatus(TttGameStatus.SCHEDULED);

        tournamentMessagingService.gameScheduled(tournamentGame, tournament);

        gameTimerService.startGameTimer(game.getId(), 5000, () -> startGame(tournamentGame, tournament));
    }

    private void startGame(TttTournamentGame game, TttTournament tournament) {
        var state = game.getGame().getState();
        state.setDateOfState(new Date());
        state.setStatus(TttGameStatus.IN_PROGRESS);

        gameTimerService.startGameTimer(game.getGame().getId(), game.getGame().getState().getXTimeLeft(),
                () -> playerTimedOut(tournament, game));
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