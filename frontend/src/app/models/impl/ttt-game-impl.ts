import { concat, from, Observable, share, tap } from "rxjs";
import { WebsocketSubscription } from "../../service/websocket.service";
import {
  TttGame,
  TttGameDTO,
  TttGameEndEvent,
  TttGameEvent,
  TttGameEventType,
  TttGameMoveEvent, TttGameStartEvent,
  TttGameState,
  TttGameStatus,
  TttSquare
} from "../ttt-game.model";
import { MatSnackBar } from "@angular/material/snack-bar";
import { TttService } from "../../service/ttt.service";

export class TttGameImpl implements TttGame {
  id: string;
  state: TttGameState;

  xplayerName: string;

  oplayerName: string;

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
    this.xplayerName = gameDto.xplayerName;
    this.oplayerName = gameDto.oplayerName;
    const events = concat(from(eventsUntilJoined), subscription.events as Observable<TttGameEvent>);
    this.events = events.pipe(
      tap((event: TttGameEvent) => this.processEvent(event)),
      share(),
    );
    this.events.subscribe();
  }

  processEvent(event: TttGameEvent) {
    console.log(event)
    if (event.type === TttGameEventType.END) {
      const actualEvent = event as TttGameEndEvent;
      this.snackbar.open(`Game ended. Winner: ${actualEvent.winnerName}`, "Close");

      const xWon = actualEvent.winnerName === this.xplayerName;
      this.state.status = (xWon ? TttGameStatus.X_WON : TttGameStatus.O_WON);
      this.subscription.unsubscribe();
      return;
    }
    if (event.type === TttGameEventType.MOVE) {
      const actualEvent = event as TttGameMoveEvent;
      this.state.board[actualEvent.x][actualEvent.y] = actualEvent.xmoved ? TttSquare.X : TttSquare.O;
      this.state.xtimeLeft = actualEvent.xtimeLeft;
      this.state.otimeLeft = actualEvent.otimeLeft;
      this.state.dateOfState = actualEvent.eventDate;
      this.state.xisNext = !actualEvent.xmoved;
      return;
    }
    if (event.type === TttGameEventType.START) {
      const actualEvent = event as TttGameStartEvent;
      this.state.dateOfState = actualEvent.eventDate;
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