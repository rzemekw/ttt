import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { lastValueFrom } from "rxjs";
import {
  TttTournament,
  TttTournamentDTO,
  TttTournamentEvent,
  TttTournamentListItem
} from "../models/ttt-tournament.model";
import { WebsocketService } from "./websocket.service";
import { TttTournamentImpl } from "../models/impl/ttt-tournament-impl";

@Injectable({
  providedIn: 'root'
})
export class TttService {
  constructor(
    private http: HttpClient,
    private webSocketService: WebsocketService
  ) {
  }

  getTournaments(id: number): Promise<TttTournamentListItem[]> {
    return lastValueFrom(this.http.get<TttTournamentListItem[]>(`/api/ttt/tournaments/${id}`));
  }

  async joinTournament(id: number): Promise<TttTournament> {
    const subscription = await this.webSocketService.subscribe(`/topic/ttt/tournaments/${id}`);

    const eventsUntilJoined: TttTournamentEvent[] = [];
    const s = subscription.events.subscribe((event: TttTournamentEvent) => {
      eventsUntilJoined.push(event);
    });

    let tournamentDto: TttTournamentDTO;
    try {
      tournamentDto = await lastValueFrom(this.http.post<TttTournamentDTO>(`/api/ttt/tournaments/${id}/join`, {}));
      s.unsubscribe()
    } catch (e) {
      subscription.unsubscribe();
      throw e;
    }

    return new TttTournamentImpl(tournamentDto, eventsUntilJoined, subscription);
  }

  async spectateTournament(id: number): Promise<TttTournament> {
    const subscription = await this.webSocketService.subscribe(`/topic/ttt/tournaments/${id}`);

    const eventsUntilJoined: TttTournamentEvent[] = [];
    const s = subscription.events.subscribe((event: TttTournamentEvent) => {
      eventsUntilJoined.push(event);
    });

    let tournamentDto: TttTournamentDTO;
    try {
      tournamentDto = await lastValueFrom(this.http.get<TttTournamentDTO>(`/api/ttt/tournaments/${id}`));
      s.unsubscribe()
    } catch (e) {
      subscription.unsubscribe();
      throw e;
    }

    return new TttTournamentImpl(tournamentDto, eventsUntilJoined, subscription);
  }

  async createTournament(name: string): Promise<TttTournament> {
    const body = {name};
    const tournamentDto = await lastValueFrom(this.http.post<TttTournamentDTO>(`/api/ttt/tournaments`, body));
    const subscription = await this.webSocketService.subscribe(`/topic/ttt/tournaments/${tournamentDto.id}`);
    return new TttTournamentImpl(tournamentDto, [], subscription);
  }
}

