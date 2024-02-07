import { Component, OnInit } from '@angular/core';
import { TttService } from "../../../../service/ttt.service";
import {
  TttTournament,
  TttTournamentEventType,
  TttTournamentGameScheduledEvent,
  TttTournamentStatus
} from "../../../../models/ttt-tournament.model";
import { MatSnackBar } from "@angular/material/snack-bar";
import { ActivatedRoute } from "@angular/router";
import { UserService } from "../../../../service/user.service";
import { TttGame } from "../../../../models/ttt-game.model";

@Component({
  templateUrl: './ttt-tournament.component.html',
  styleUrls: ['./ttt-tournament.component.scss']
})
export class TttTournamentComponent implements OnInit {
  tournament!: TttTournament;

  isOwner = false;

  TttTournamentStatus = TttTournamentStatus;

  activeGame: TttGame | null = null;

  constructor(
    private tttService: TttService,
    private snackBar: MatSnackBar,
    private route: ActivatedRoute,
    private userService: UserService
  ) {
  }

  async ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id') as string
    this.isOwner = !!this.route.snapshot.queryParamMap.get('owner')
    this.tournament = await this.tttService.getTournament(id)

    this.tournament.events.subscribe(event => {
      if (event.type === TttTournamentEventType.GAME_SCHEDULED) {
        const actualEvent = event as TttTournamentGameScheduledEvent;
        if (this.userService.getUserName() === actualEvent.xPlayerName || this.userService.getUserName() === actualEvent.oPlayerName) {
          this.activeGame = this.tttService.getGame(actualEvent.gameId);
        }
      }
    });
  }

  closeTournament() {
    this.tttService.closeTournament(this.tournament.id)
  }

  startTournament() {
    this.tttService.startTournament(this.tournament.id)
  }

  showBracket(): boolean {
    if (this.tournament.state.status !== TttTournamentStatus.WAITING_FOR_PLAYERS) {
      return false;
    }
    return !this.activeGame
  }
}
