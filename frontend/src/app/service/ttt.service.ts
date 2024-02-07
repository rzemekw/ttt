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
import { TttGame, TttGameDTO, TttGameEvent } from "../models/ttt-game.model";
import { TttGameImpl } from "../models/impl/ttt-game-impl";
import { MatSnackBar } from "@angular/material/snack-bar";

@Injectable({
  providedIn: 'root'
})
export class TttService {
  constructor(
    private http: HttpClient,
    private webSocketService: WebsocketService,
    private snackbar: MatSnackBar,
  ) {
  }

  getTournaments(): Promise<TttTournamentListItem[]> {
    return lastValueFrom(this.http.get<TttTournamentListItem[]>(`/api/ttt/tournaments`));
  }

  async joinTournament(id: string): Promise<void> {
    await lastValueFrom(this.http.post<TttTournamentDTO>(`/api/ttt/tournaments/${id}/join`, {}));
  }

  async getTournament(id: string): Promise<TttTournament> {
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

  async getGame(id: string, tournamentId: string): Promise<TttGame> {
    const subscription = await this.webSocketService.subscribe(`/topic/ttt/games/${id}`);

    const eventsUntilJoined: TttGameEvent[] = [];
    const s = subscription.events.subscribe((event: TttGameEvent) => {
      eventsUntilJoined.push(event);
    });

    let gameDTO: TttGameDTO;
    try {
      gameDTO = await lastValueFrom(this.http.get<TttGameDTO>(`/api/ttt/tournaments/${tournamentId}/games/${id}`));
      s.unsubscribe()
    } catch (e) {
      subscription.unsubscribe();
      throw e;
    }

    return new TttGameImpl(tournamentId, gameDTO, eventsUntilJoined, subscription, this.snackbar, this);
  }

  async createTournament(name: string): Promise<TttTournamentDTO> {
    const body = {name};
    return lastValueFrom(this.http.post<TttTournamentDTO>(`/api/ttt/tournaments`, body));
  }

  async closeTournament(id: string): Promise<void> {
    await lastValueFrom(this.http.post<TttTournamentDTO>(`/api/ttt/tournaments/${id}/close`, {}));
  }

  async startTournament(id: string): Promise<void> {
    await lastValueFrom(this.http.post<TttTournamentDTO>(`/api/ttt/tournaments/${id}/start`, {}));
  }

  move(tournamentId: string, id: string, x: number, y: number) {
    const body = {x, y};
    return lastValueFrom(this.http.post<void>(`/api/ttt/tournaments/${tournamentId}/games/${id}/move`, body));
  }

  joinGame(tournamentId: string, id: string) {
    return lastValueFrom(this.http.post<void>(`/api/ttt/tournaments/${tournamentId}/games/${id}/join`, {}));
  }
}

