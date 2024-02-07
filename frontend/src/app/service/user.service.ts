import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { lastValueFrom, retry } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private username: string = '';

  constructor(private _http: HttpClient) {
  }

  async retrieveUserName(): Promise<string> {
    return this.username;
    // const username = await lastValueFrom(this.http.get<string>('/api/user').pipe(retry(3)));
    // if (!username) {
    //   return '';
    // }
    // this.username = username;
    // return username;
  }

  getUserName() {
    return this.username;
  }

  async login(username: string): Promise<void> {
    // await lastValueFrom(this.http.post<void>('/api/user/login', username));
    this.username = username;
  }
}