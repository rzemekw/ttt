import { ChangeDetectorRef, Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { TttGame, TttGameEventType, TttGameStatus, TttSquare } from "../../../models/ttt-game.model";
import { UserService } from "../../../service/user.service";

@Component({
  selector: 'app-ttt-game',
  templateUrl: './ttt-game.component.html',
  styleUrls: ['./ttt-game.component.scss']
})
export class TttGameComponent implements OnChanges, OnDestroy {

  @Input()
  game!: TttGame;

  TttSquare = TttSquare;
  TttGameStatus = TttGameStatus;

  playerType!: PlayerType;

  loading = false;

  xTimeLeft: string = '';
  oTimeLeft: string = '';
  private timerHandle!: number;

  constructor(
    private userService: UserService,
    private cdr: ChangeDetectorRef
  ) {
  }

  ngOnDestroy() {
    if (this.timerHandle) {
      clearInterval(this.timerHandle);
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    if (!changes['game'] || !this.game) {
      return;
    }
    this.determinePlayerType();
    this.initTimers();

    this.game.events.subscribe(event => {
      if (event.type === TttGameEventType.MOVE) {
        this.loading = false;
      }
      this.cdr.detectChanges();
    });

    if (this.playerType !== PlayerType.SPECTATOR) {
      this.game.join();
    }
  }

  private determinePlayerType() {
    const username = this.userService.getUserName();
    if (username === this.game.xplayerName) {
      this.playerType = PlayerType.X;
      return
    }
    if (username === this.game.oplayerName) {
      this.playerType = PlayerType.O;
      return
    }
    this.playerType = PlayerType.SPECTATOR;
  }

  async move(i: number, j: number) {
    if (this.loading) {
      console.log('loading')
      return;
    }
    if (this.game.state.status !== TttGameStatus.IN_PROGRESS) {
      console.log('not in progress')
      return;
    }
    if (this.playerType === PlayerType.SPECTATOR) {
      console.log('spectator')
      return;
    }
    if (this.game.state.board[i][j] !== TttSquare.Empty) {
      console.log('not empty')
      return;
    }
    if (this.playerType === PlayerType.X && !this.game.state.xisNext) {
      console.log('not x turn')
      return;
    }
    if (this.playerType === PlayerType.O && this.game.state.xisNext) {
      console.log('not o turn')
      return;
    }
    this.loading = true;
    try {
      console.log('move')
      await this.game.move(i, j);
    } catch (e) {
      console.error(e);
      this.loading = false;
    }
  }

  shouldShowLoadingBlocker() {
    if (this.loading) {
      return true;
    }
    if (this.game.state.status !== TttGameStatus.IN_PROGRESS) {
      return true;
    }
    if (this.playerType === PlayerType.X && !this.game.state.xisNext) {
      return true;
    }
    if (this.playerType === PlayerType.O && this.game.state.xisNext) {
      return true;
    }
    return false;
  }

  protected readonly PlayerType = PlayerType;

  initTimers() {
    this.timerHandle = setInterval(() => {
      this.xTimeLeft = this.calculateXTimeLeft();
      this.oTimeLeft = this.calculateOTimeLeft();
    }, 10);
  }

  timeLeft(stateTimeLeft: number, freeze: boolean) {
    let timeLeft = stateTimeLeft;
    if (!freeze) {
      const lastDate = new Date(this.game.state.dateOfState);
      const dateNow = new Date();
      const diff = dateNow.getTime() - lastDate.getTime();
      timeLeft = stateTimeLeft - diff;
    }

    return `${Math.floor(timeLeft / 1000)}: ${timeLeft % 1000}`;

  }
  calculateXTimeLeft() {
    return this.timeLeft(this.game.state.xtimeLeft, !this.game.state.xisNext || this.game.state.status !== TttGameStatus.IN_PROGRESS);
  }

  calculateOTimeLeft() {
    return this.timeLeft(this.game.state.otimeLeft, this.game.state.xisNext || this.game.state.status !== TttGameStatus.IN_PROGRESS);
  }
}

enum PlayerType {
  X = 'X',
  O = 'O',
  SPECTATOR = 'SPECTATOR'
}

