import { Observable } from "rxjs";

export interface TttGameState {
  board: TttSquare[][];
  xIsNext: boolean;

  xTimeLeft: number; // in milliseconds
  oTimeLeft: number; // in milliseconds

  dateOfState: Date;

  status: TttGameStatus;
}

export enum TttSquare {
  Empty = 0,
  X = 1,
  O = 2
}

export enum TttGameStatus {
  NOT_STARTED = "NOT_STARTED",
  SCHEDULED = "SCHEDULED",
  IN_PROGRESS = "IN_PROGRESS",
  X_WON = "X_WON",
  O_WON = "O_WON",
}
export interface TttGameEvent {
  eventType: TttGameEventType;
}

export enum TttGameEventType {
  START = "START",
  MOVE = "MOVE",
  END = "END",
}

export interface TttGameMoveEvent extends TttGameEvent {
  eventType: TttGameEventType.MOVE;
  x: number;
  y: number;

  isX: boolean;

  xTimeLeft: number; // in milliseconds
  oTimeLeft: number; // in milliseconds

  eventDate: Date;
}

export interface TttGameEndEvent extends TttGameEvent {
  eventType: TttGameEventType.END;
  winnerName: string;

  winningLine?: TttGameWinningLine;
}

export interface TttGameWinningLine {
  x1: number;
  y1: number;
  x2: number;
  y2: number;
}

export interface TttGameStartEvent extends TttGameEvent {
  eventType: TttGameEventType.START;

  eventDate: Date;
}

export interface TttGame {
  id: number;
  state: TttGameState;

  xPlayerName: string;

  oPlayerName: string;

  events: Observable<TttGameEvent>;

  move(x: number, y: number): Promise<void>;
}