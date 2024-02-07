import { Component, Input, OnInit } from '@angular/core';
import { TttGame, TttGameEventType, TttGameStatus, TttSquare } from "../../../models/ttt-game.model";
import { UserService } from "../../../service/user.service";

@Component({
  selector: 'app-ttt-game',
  templateUrl: './ttt-game.component.html',
  styleUrls: ['./ttt-game.component.scss']
})
export class TttGameComponent implements OnInit {

  @Input()
  game!: TttGame;

  TttSquare = TttSquare;
  TttGameStatus = TttGameStatus;

  playerType!: PlayerType;

  loading = false;

  constructor(
    private userService: UserService,
  ) {
  }

  ngOnInit() {
    this.determinePlayerType();

    this.game.events.subscribe(event => {
      if (event.type === TttGameEventType.MOVE) {
        this.loading = false;
      }
    });

    if (this.playerType !== PlayerType.SPECTATOR) {
      this.game.join();
    }
  }

  private determinePlayerType() {
    const username = this.userService.getUserName();
    if (username === this.game.xPlayerName) {
      this.playerType = PlayerType.X;
      return
    }
    if (username === this.game.oPlayerName) {
      this.playerType = PlayerType.O;
      return
    }
    this.playerType = PlayerType.SPECTATOR;
  }

  async move(i: number, j: number) {
    if (this.loading) {
      return;
    }
    if (this.playerType === PlayerType.SPECTATOR) {
      return;
    }
    if (this.game.state.board[i][j] !== TttSquare.Empty) {
      return;
    }
    if (this.playerType === PlayerType.X && !this.game.state.xIsNext) {
      return;
    }
    if (this.playerType === PlayerType.O && this.game.state.xIsNext) {
      return;
    }
    this.loading = true;
    try {
      await this.game.move(i, j);
    } catch (e) {
      console.error(e);
      this.loading = false;
    }
  }
}

enum PlayerType {
  X = 'X',
  O = 'O',
  SPECTATOR = 'SPECTATOR'
}

