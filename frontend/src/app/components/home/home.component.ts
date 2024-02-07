import { Component, OnInit } from '@angular/core';
import { MatDialog } from "@angular/material/dialog";
import { UserDialogComponent } from "../user-dialog/user-dialog.component";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
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
