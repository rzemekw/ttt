import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { lastValueFrom, retry } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private username: string = '';

  constructor(private http: HttpClient) {
  }

  getUserName() {
    return this.username;
  }

  async login(username: string): Promise<void> {
    await lastValueFrom(this.http.post<void>('/api/user/login', username));
  }
}