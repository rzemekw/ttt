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

  getTournaments(): Promise<TttTournamentListItem[]> {
    return lastValueFrom(this.http.get<TttTournamentListItem[]>(`/api/ttt/tournaments`));
  }

  async joinTournament(id: number): Promise<void> {
    await lastValueFrom(this.http.post<TttTournamentDTO>(`/api/ttt/tournaments/${id}/join`, {}));
  }

  async getTournament(id: number): Promise<TttTournament> {
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

  async createTournament(name: string): Promise<TttTournamentDTO> {
    const body = {name};
    return lastValueFrom(this.http.post<TttTournamentDTO>(`/api/ttt/tournaments`, body));
  }
}

