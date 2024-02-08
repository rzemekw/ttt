import { Component, OnInit } from '@angular/core';
import { MatDialog } from "@angular/material/dialog";
import { UserDialogComponent } from "./user-dialog/user-dialog.component";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  loading = true;

  constructor(
    private dialog: MatDialog
  ) {
  }

  async ngOnInit() {
    this.dialog.open(UserDialogComponent, {disableClose: true}).afterClosed()
      .subscribe(() => this.loading = false);
  }
}


