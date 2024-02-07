import {
  TttTournament,
  TttTournamentDTO,
  TttTournamentEvent,
  TttTournamentEventType, TttTournamentStartedEvent,
  TttTournamentState
} from "../ttt-tournament.model";
import { concat, from, Observable, share, shareReplay, tap } from "rxjs";
import { WebsocketSubscription } from "../../service/websocket.service";

export class TttTournamentImpl implements TttTournament {
  id: number;
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
    if (event.eventType === TttTournamentEventType.TOURNAMENT_FINISHED) {
      this.subscription.unsubscribe();
      return;
    }
    if (event.eventType === TttTournamentEventType.TOURNAMENT_STARTED) {
      const tournamentStartedEvent = event as TttTournamentStartedEvent;
      this.state = tournamentStartedEvent.state;
      return;
    }
  }

  disconnect() {
    this.subscription.unsubscribe();
  }
}