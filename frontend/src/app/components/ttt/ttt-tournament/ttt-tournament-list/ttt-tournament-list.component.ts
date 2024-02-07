import { Component, Input, OnInit } from '@angular/core';
import { TttService } from "../../../../service/ttt.service";
import { TttTournamentListItem } from "../../../../models/ttt-tournament.model";
import { MatSnackBar } from "@angular/material/snack-bar";
import { Router } from "@angular/router";

@Component({
  selector: 'app-ttt-tournament-list',
  templateUrl: './ttt-tournament-list.component.html',
  styleUrls: ['./ttt-tournament-list.component.scss']
})
export class TttTournamentListComponent implements OnInit {
  tournaments: TttTournamentListItem[] = []

  constructor(
    private tttTournamentService: TttService,
    private snackBar: MatSnackBar,
    private router: Router,
  ) {
  }

  async ngOnInit() {
    this.tournaments = await this.tttTournamentService.getTournaments()
  }

  async join(t: TttTournamentListItem) {
    try {
      await this.tttTournamentService.joinTournament(t.id);
      this.router.navigate([`/tournament/${t.id}`]);
    } catch (e) {
      this.snackBar.open("Failed to join tournament - try changing username")
    }
  }
}
