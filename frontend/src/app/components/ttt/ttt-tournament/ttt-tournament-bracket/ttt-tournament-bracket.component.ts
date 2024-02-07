import { Component, Input, OnInit } from '@angular/core';
import { TttTournament } from "../../../../models/ttt-tournament.model";
import { TttGameStatus } from "../../../../models/ttt-game.model";

@Component({
  selector: 'app-ttt-tournament-bracket',
  templateUrl: './ttt-tournament-bracket.component.html',
  styleUrls: ['./ttt-tournament-bracket.component.scss'],
})
export class TttTournamentBracketComponent implements OnInit {

  TttGameStatus = TttGameStatus;

  missingFirstRoundGames: number = 0;

  @Input()
  tournament!: TttTournament;

  ngOnInit() {
    const gameIds = this.tournament.state.gameIds;
    if (gameIds.length === 1) {
      this.missingFirstRoundGames = 0;
    }
    const firstRoundGameIds = gameIds[0];
    const secondRoundGameIds = gameIds[1];
    this.missingFirstRoundGames = secondRoundGameIds.length * 2 - firstRoundGameIds.length;
  }

  getGame(id: string) {
    return this.tournament.state.games[id];
  }

  counter(i: number) {
    return new Array(i);
  }

  gameClicked(gameId: string) {
    console.log(gameId);
  }
}
