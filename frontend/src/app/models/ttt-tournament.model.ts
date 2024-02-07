import { Observable } from "rxjs";
import { TttGameStatus } from "./ttt-game.model";

export interface TttTournamentState {
  players: TttTournamentPlayer[];
  games: TttTournamentGame[][];
  gameMap: TttTournamentGame[][];
  status: TttTournamentStatus;
}

export interface TttTournamentGame {
  id: string;
  xPlayerName: string;
  oPlayerName: string;
  status: TttGameStatus;
  roundIndex: number;
  inRoundIndex: number;
}

export interface TttTournamentPlayer {
  playerName: string;
}

export interface TttTournament {
  id: number;
  name: string;
  state: TttTournamentState;

  events: Observable<TttTournamentEvent>;

  disconnect(): void;
}

export interface TttTournamentDTO {
  id: number;
  name: string;
  state: TttTournamentState;
}

export interface TttTournamentListItem {
  id: number;
  name: string;
  status: TttTournamentStatus;
}

export enum TttTournamentStatus {
  WAITING_FOR_PLAYERS = "WAITING_FOR_PLAYERS",
  NOT_STARTED = "NOT_STARTED",
  IN_PROGRESS = "IN_PROGRESS",
  FINISHED = "FINISHED",
}


export interface TttTournamentEvent {
  eventType: TttTournamentEventType;
}

export enum TttTournamentEventType {
  TOURNAMENT_CLOSED = "TOURNAMENT_CLOSED",
  TOURNAMENT_STARTED = "TOURNAMENT_STARTED",
  GAME_SCHEDULED = "GAME_SCHEDULED",
  GAME_ENDED = "GAME_ENDED",
  TOURNAMENT_FINISHED = "TOURNAMENT_FINISHED"
}

export interface TttTournamentStartedEvent extends TttTournamentEvent {
  eventType: TttTournamentEventType.TOURNAMENT_STARTED;
  state: TttTournamentState;
}