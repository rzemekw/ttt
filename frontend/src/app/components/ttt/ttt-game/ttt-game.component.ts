import { Component, Input, OnInit } from '@angular/core';
import { TttGame, TttSquare } from "../../../models/ttt-game.model";
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

  playerType!: PlayerType;

  constructor(
    private userService: UserService,
  ) {
  }

  ngOnInit() {
    this.game = {
      state: {
        board: [
          [0, 0, 0],
          [0, 0, 0],
          [0, 0, 0]
        ],
        xIsNext: true,
        xTimeLeft: 0,
        oTimeLeft: 0,
        dateOfState: new Date()
      }
    } as any;

    this.determinePlayerType();
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

  move(i: number, j: number) {
    console.log(i, j);
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
    this.game.state.board[i][j] = this.playerType === PlayerType.X ? TttSquare.X : TttSquare.O;
  }
}

enum PlayerType {
  X = 'X',
  O = 'O',
  SPECTATOR = 'SPECTATOR'
}

