import { Observable } from "rxjs";

export interface TttGameState {
  board: TttSquare[][];
  xisNext: boolean;

  xtimeLeft: number; // in milliseconds
  otimeLeft: number; // in milliseconds

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
  type: TttGameEventType;
}

export enum TttGameEventType {
  START = "START",
  MOVE = "MOVE",
  END = "END",
}

export interface TttGameMoveEvent extends TttGameEvent {
  type: TttGameEventType.MOVE;
  x: number;
  y: number;

  xmoved: boolean;

  xtimeLeft: number; // in milliseconds
  otimeLeft: number; // in milliseconds

  eventDate: Date;
}

export interface TttGameEndEvent extends TttGameEvent {
  type: TttGameEventType.END;
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
  type: TttGameEventType.START;

  eventDate: Date;
}

export interface TttGame {
  id: string;
  state: TttGameState;

  xplayerName: string;

  oplayerName: string;

  events: Observable<TttGameEvent>;

  move(x: number, y: number): Promise<void>;

  join(): Promise<void>;

  disconnect(): void;
}

export interface TttGameDTO {
  id: string;
  state: TttGameState;
  xplayerName: string;
  oplayerName: string;
}