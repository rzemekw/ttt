import { Component } from '@angular/core';
import { UserService } from "../../service/user.service";
import { MatDialogRef } from "@angular/material/dialog";
import { MatSnackBar } from "@angular/material/snack-bar";

@Component({
  templateUrl: './user-dialog.component.html',
  styleUrls: ['./user-dialog.component.scss']
})
export class UserDialogComponent {
  username = '';
  loading = false;

  constructor(
    private userService: UserService,
    private ref: MatDialogRef<UserDialogComponent>,
    private snackbar: MatSnackBar
  ) {
  }

  async login() {
    if (this.loading) {
      return;
    }
    if (!this.username) {
      return;
    }

    this.loading = true;

    try {
      await this.userService.login(this.username);
      this.ref.close();
    } catch (e) {
      this.snackbar.open('Wybierz inną nazwę użytkownika', 'OK');
    } finally {
      this.loading = false;
    }
  }
}
