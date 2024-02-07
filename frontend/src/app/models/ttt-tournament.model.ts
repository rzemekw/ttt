import { Observable } from "rxjs";
import { TttGameStatus } from "./ttt-game.model";

export interface TttTournamentState {
  players: TttTournamentPlayer[];
  gameIds: string[][];
  games: Record<string, TttTournamentGame>;
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
  id: string;
  name: string;
  state: TttTournamentState;

  events: Observable<TttTournamentEvent>;

  disconnect(): void;
}

export interface TttTournamentDTO {
  id: string;
  name: string;
  state: TttTournamentState;
}

export interface TttTournamentListItem {
  id: string;
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
  type: TttTournamentEventType;
}

export enum TttTournamentEventType {
  TOURNAMENT_CLOSED = "TOURNAMENT_CLOSED",
  TOURNAMENT_STARTED = "TOURNAMENT_STARTED",
  GAME_SCHEDULED = "GAME_SCHEDULED",
  GAME_ENDED = "GAME_ENDED",
  TOURNAMENT_FINISHED = "TOURNAMENT_FINISHED"
}

export interface TttTournamentTournamentClosedEvent extends TttTournamentEvent {
  type: TttTournamentEventType.TOURNAMENT_CLOSED;
  tournament: TttTournamentDTO;
}


export interface TttTournamentTournamentFinishedEvent extends TttTournamentEvent {
  type: TttTournamentEventType.TOURNAMENT_FINISHED;
  winnerName: string;
}

export interface TttTournamentGameScheduledEvent extends TttTournamentEvent {
  type: TttTournamentEventType.GAME_SCHEDULED;
  gameId: string;
  xPlayerName: string;
  oPlayerName: string;
  roundIndex: number;
  inRoundIndex: number;
}

export interface TttTournamentGameEndedEvent extends TttTournamentEvent {
  type: TttTournamentEventType.GAME_SCHEDULED;
  gameId: string;
  xWon: boolean;
  roundIndex: number;
  inRoundIndex: number;
}