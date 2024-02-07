import { concat, from, Observable, share, tap } from "rxjs";
import { WebsocketSubscription } from "../../service/websocket.service";
import {
  TttGame,
  TttGameDTO,
  TttGameEndEvent,
  TttGameEvent,
  TttGameEventType,
  TttGameMoveEvent,
  TttGameState,
  TttGameStatus,
  TttSquare
} from "../ttt-game.model";
import { MatSnackBar } from "@angular/material/snack-bar";
import { TttService } from "../../service/ttt.service";

export class TttGameImpl implements TttGame {
  id: string;
  state: TttGameState;

  xPlayerName: string;

  oPlayerName: string;

  events: Observable<TttGameEvent>;

  tournamentId: string;



  constructor(
    tournamentId: string,
    gameDto: TttGameDTO,
    eventsUntilJoined: TttGameEvent[],
    private subscription: WebsocketSubscription,
    private snackbar: MatSnackBar,
    private tttService: TttService,
  ) {
    this.tournamentId = tournamentId;
    this.id = gameDto.id;
    this.state = gameDto.state;
    this.xPlayerName = gameDto.xPlayerName;
    this.oPlayerName = gameDto.oPlayerName;
    const events = concat(from(eventsUntilJoined), subscription.events as Observable<TttGameEvent>);
    this.events = events.pipe(
      tap((event: TttGameEvent) => this.processEvent(event)),
      share(),
    );
    this.events.subscribe();
  }

  processEvent(event: TttGameEvent) {
    if (event.type === TttGameEventType.END) {
      const actualEvent = event as TttGameEndEvent;
      this.snackbar.open(`Game ended. Winner: ${actualEvent.winnerName}`, "Close");

      const xWon = actualEvent.winnerName === this.xPlayerName;
      this.state.status = (xWon ? TttGameStatus.X_WON : TttGameStatus.O_WON);
      this.subscription.unsubscribe();
      return;
    }
    if (event.type === TttGameEventType.MOVE) {
      const actualEvent = event as TttGameMoveEvent;
      this.state.board[actualEvent.x][actualEvent.y] = actualEvent.xMoved ? TttSquare.X : TttSquare.O;
      this.state.xTimeLeft = actualEvent.xTimeLeft;
      this.state.oTimeLeft = actualEvent.oTimeLeft;
      this.state.dateOfState = actualEvent.eventDate;
      this.state.xIsNext = !actualEvent.xMoved;
      return;
    }
    if (event.type === TttGameEventType.START) {
      this.state.status = TttGameStatus.IN_PROGRESS;
      return;
    }
  }

  disconnect() {
    this.subscription.unsubscribe();
  }

  move(x: number, y: number): Promise<void> {
    return this.tttService.move(this.tournamentId, this.id, x, y);
  }

  join(): Promise<void> {
    return this.tttService.joinGame(this.tournamentId, this.id);
  }
}