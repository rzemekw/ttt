import {
  TttTournament,
  TttTournamentDTO,
  TttTournamentEvent,
  TttTournamentEventType,
  TttTournamentGameEndedEvent, TttTournamentGameScheduledEvent, TttTournamentPlayerJoinedEvent,
  TttTournamentState,
  TttTournamentStatus,
  TttTournamentTournamentClosedEvent, TttTournamentTournamentFinishedEvent
} from "../ttt-tournament.model";
import { concat, from, Observable, share, tap } from "rxjs";
import { WebsocketSubscription } from "../../service/websocket.service";
import { TttGameStatus } from "../ttt-game.model";
import { MatSnackBar } from "@angular/material/snack-bar";

export class TttTournamentImpl implements TttTournament {
  id: string;
  name: string;
  state: TttTournamentState;
  events: Observable<TttTournamentEvent>;

  constructor(
    tournamentDto: TttTournamentDTO,
    eventsUntilJoined: TttTournamentEvent[],
    private subscription: WebsocketSubscription,
    private snackBar: MatSnackBar,
  ) {
    this.id = tournamentDto.id;
    this.name = tournamentDto.name;
    this.state = tournamentDto.state;
    const events = concat(from(eventsUntilJoined), subscription.events as Observable<TttTournamentEvent>);
    this.events = events.pipe(
      tap((event: TttTournamentEvent) => this.processEvent(event)),
      share(),
    );
    this.events.subscribe();
  }

  processEvent(event: TttTournamentEvent) {
    console.log(event)
    if (event.type === TttTournamentEventType.TOURNAMENT_FINISHED) {
      this.subscription.unsubscribe();
      const actualEvent = event as TttTournamentTournamentFinishedEvent;
      this.snackBar.open(`Tournament finished. Winner: ${actualEvent.winnerName}`, 'Close');
      return;
    }
    if (event.type === TttTournamentEventType.TOURNAMENT_CLOSED) {
      const tournamentStartedEvent = event as TttTournamentTournamentClosedEvent;
      this.state = tournamentStartedEvent.tournament.state;
      return;
    }
    if (event.type === TttTournamentEventType.PLAYER_JOINED) {
      const tournamentStartedEvent = event as TttTournamentPlayerJoinedEvent;
      this.state.players.push({playerName: tournamentStartedEvent.playerName});
      return;
    }
    if (event.type === TttTournamentEventType.TOURNAMENT_STARTED) {
      this.state.status = TttTournamentStatus.IN_PROGRESS;
      return;
    }
    if (event.type === TttTournamentEventType.GAME_ENDED) {
      const actualEvent = event as TttTournamentGameEndedEvent;
      const endedGame = this.state.games[actualEvent.gameId];
      endedGame.status = (actualEvent.xwon ? TttGameStatus.X_WON : TttGameStatus.O_WON);
      const isLatestGame = endedGame.roundIndex + 1 === this.state.gameIds.length;
      if (isLatestGame) {
        this.state.status = TttTournamentStatus.FINISHED;
        return;
      }

      const nextGameRoundIndex = endedGame.roundIndex + 1;
      const nextGameInRoundIndex = Math.floor(endedGame.inRoundIndex / 2);

      const nextGameId = this.state.gameIds[nextGameRoundIndex][nextGameInRoundIndex];
      const nextGame = this.state.games[nextGameId];
      if (!nextGame.xplayerName) {
        nextGame.xplayerName = endedGame.xplayerName;
      } else {
        nextGame.oplayerName = endedGame.xplayerName;
      }
      return;
    }
    if (event.type === TttTournamentEventType.GAME_SCHEDULED) {
      const actualEvent = event as TttTournamentGameScheduledEvent;
      const game = this.state.games[actualEvent.gameId];
      game.status = TttGameStatus.SCHEDULED;
      game.xplayerName = actualEvent.xplayerName;
      game.oplayerName = actualEvent.oplayerName;
      return;
    }
  }

  disconnect() {
    this.subscription.unsubscribe();
  }
}