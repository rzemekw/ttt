import { Component } from '@angular/core';
import { TttService } from "../../../../service/ttt.service";
import { Router } from "@angular/router";

@Component({
  selector: 'app-create-ttt-tournament',
  templateUrl: './create-ttt-tournament.component.html',
  styleUrls: ['./create-ttt-tournament.component.scss']
})
export class CreateTttTournamentComponent {
  name = '';
  loading = false;

  constructor(
    private tttService: TttService,
    private router: Router
  ) {
  }

  async submit() {
    if (!this.name) {
      return;
    }
    if (this.loading) {
      return;
    }

    const tournament = await this.tttService.createTournament(this.name);

    this.router.navigate(['/tournaments', tournament.id], { queryParams: { owner: true } });
  }
}
