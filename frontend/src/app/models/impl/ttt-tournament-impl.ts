import {
  TttTournament,
  TttTournamentDTO,
  TttTournamentEvent,
  TttTournamentEventType,
  TttTournamentGameEndedEvent, TttTournamentGameScheduledEvent,
  TttTournamentState,
  TttTournamentStatus,
  TttTournamentTournamentClosedEvent
} from "../ttt-tournament.model";
import { concat, from, Observable, share, tap } from "rxjs";
import { WebsocketSubscription } from "../../service/websocket.service";
import { TttGameStatus } from "../ttt-game.model";

export class TttTournamentImpl implements TttTournament {
  id: string;
  name: string;
  state: TttTournamentState;
  events: Observable<TttTournamentEvent>;

  constructor(
    tournamentDto: TttTournamentDTO,
    eventsUntilJoined: TttTournamentEvent[],
    private subscription: WebsocketSubscription
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
    if (event.type === TttTournamentEventType.TOURNAMENT_FINISHED) {
      this.subscription.unsubscribe();
      return;
    }
    if (event.type === TttTournamentEventType.TOURNAMENT_CLOSED) {
      const tournamentStartedEvent = event as TttTournamentTournamentClosedEvent;
      this.state = tournamentStartedEvent.tournament.state;
      return;
    }
    if (event.type === TttTournamentEventType.TOURNAMENT_STARTED) {
      this.state.status = TttTournamentStatus.IN_PROGRESS;
      return;
    }
    if (event.type === TttTournamentEventType.GAME_ENDED) {
      const actualEvent = event as TttTournamentGameEndedEvent;
      const endedGame = this.state.games[actualEvent.gameId];
      endedGame.status = (actualEvent.xWon ? TttGameStatus.X_WON : TttGameStatus.O_WON);
      const isLatestGame = endedGame.roundIndex + 1 === this.state.gameIds.length;
      if (isLatestGame) {
        this.state.status = TttTournamentStatus.FINISHED;
      }

      const nextGameRoundIndex = endedGame.roundIndex + 1;
      const nextGameInRoundIndex = Math.floor(endedGame.inRoundIndex / 2);

      const nextGameId = this.state.gameIds[nextGameRoundIndex][nextGameInRoundIndex];
      const nextGame = this.state.games[nextGameId];
      if (!nextGame.xPlayerName) {
        nextGame.xPlayerName = endedGame.xPlayerName;
      } else {
        nextGame.oPlayerName = endedGame.xPlayerName;
      }
      return;
    }
    if (event.type === TttTournamentEventType.GAME_SCHEDULED) {
      const actualEvent = event as TttTournamentGameScheduledEvent;
      const game = this.state.games[actualEvent.gameId];
      game.status = TttGameStatus.SCHEDULED;
      game.xPlayerName = actualEvent.xPlayerName;
      game.oPlayerName = actualEvent.oPlayerName;
      return;
    }
  }

  disconnect() {
    this.subscription.unsubscribe();
  }
}