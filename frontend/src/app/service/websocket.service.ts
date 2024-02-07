import { Injectable } from "@angular/core";
import * as SockJS from 'sockjs-client';
import { CompatClient, Stomp } from '@stomp/stompjs';
import { Observable, Subject } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  private stompClient!: CompatClient;

  private connected!: (stompClient: CompatClient) => any;

  private onConnected = new Promise<CompatClient>((resolve) => {
    this.connected = resolve;
  });

  connectIfNotConnected() {
    if (this.stompClient) {
      return this.onConnected;
    }
    const socket = new SockJS('http://localhost:8080/ws');
    this.stompClient = Stomp.over(socket);
    this.stompClient.connect({}, () => this.connected(this.stompClient));
    return this.onConnected;
  }

  async subscribe(topic: string) : Promise<WebsocketSubscription> {
    const stompClient = await this.connectIfNotConnected();
    const subject = new Subject<any>();
    const subscription = stompClient.subscribe(topic, (message) => {
      const body = JSON.parse(message.body);
      subject.next(body);
    });
    return {
      unsubscribe: () => {
        subscription.unsubscribe();
        subject.complete();
      },
      subscriptionId: subscription.id,
      events: subject.asObservable()
    };
  }
}

export interface WebsocketSubscription {
  unsubscribe(): void;
  subscriptionId: string;
  events: Observable<any>;
}