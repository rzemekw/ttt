import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppComponent } from './components/app.component';
import { MatSelectModule } from "@angular/material/select";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { HttpClientModule } from "@angular/common/http";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatCardModule } from "@angular/material/card";
import { MatInputModule } from "@angular/material/input";
import { TttGameComponent } from "./components/ttt/ttt-game/ttt-game.component";
import { HomeComponent } from "./components/home/home.component";
import { UserDialogComponent } from "./components/user-dialog/user-dialog.component";
import { MatDialogModule } from "@angular/material/dialog";
import { MatSnackBarModule } from "@angular/material/snack-bar";
import {
  CreateTttTournamentComponent
} from "./components/ttt/ttt-tournament/create-ttt-tournament/create-ttt-tournament.component";
import {
  TttTournamentListComponent
} from "./components/ttt/ttt-tournament/ttt-tournament-list/ttt-tournament-list.component";
import { TttTournamentComponent } from "./components/ttt/ttt-tournament/ttt-tournament/ttt-tournament.component";
import {
  TttTournamentBracketComponent
} from "./components/ttt/ttt-tournament/ttt-tournament-bracket/ttt-tournament-bracket.component";

@NgModule({
  imports: [
    BrowserModule,
    ReactiveFormsModule,
    RouterModule.forRoot([
      {path: '', component: HomeComponent},
      {path: 'create-tournament', component: CreateTttTournamentComponent},
      {path: 'tournament/:id', component: TttTournamentComponent},
    ]),
    MatSelectModule,
    BrowserAnimationsModule,
    HttpClientModule,
    MatButtonModule,
    FormsModule,
    MatIconModule,
    MatCardModule,
    MatInputModule,
    MatDialogModule,
    MatSnackBarModule
  ],
  declarations: [
    AppComponent,
    TttGameComponent,
    HomeComponent,
    UserDialogComponent,
    CreateTttTournamentComponent,
    TttTournamentListComponent,
    TttTournamentComponent,
    TttTournamentBracketComponent
  ],
  bootstrap: [
    AppComponent
  ],
  providers: []
})
export class AppModule {
}


