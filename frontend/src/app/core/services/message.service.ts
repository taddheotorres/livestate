import { Injectable, PLATFORM_ID, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { Client, Message } from '@stomp/stompjs';
import { isPlatformBrowser } from '@angular/common';

@Injectable({ providedIn: 'root' })
export class MessageService {
  private apiUrl = 'http://localhost:8081/api/messages';
  private stompClient: Client | null = null;
  private messageSubject = new Subject<any>();

  public onMessageReceived$ = this.messageSubject.asObservable();

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  connect(token: string) {
    if (!isPlatformBrowser(this.platformId)) return;
    if (this.stompClient && this.stompClient.active) return;

    this.stompClient = new Client({
      brokerURL: 'ws://localhost:8081/ws',
      connectHeaders: {
        Authorization: `Bearer ${token}`
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        console.log('Connected to STOMP WebSocket');
        this.stompClient?.subscribe('/user/queue/messages', (message: Message) => {
          if (message.body) {
            const body = JSON.parse(message.body);
            this.messageSubject.next(body);
          }
        });
      },
      onStompError: (frame) => {
        console.error('Broker error: ' + frame.headers['message']);
      }
    });

    this.stompClient.activate();
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.deactivate();
      this.stompClient = null;
    }
  }

  sendMessage(msg: { receiverId: number; content: string; propertyId?: number; }): Observable<any> {
    return this.http.post<any>(this.apiUrl, msg);
  }

  getConversation(otherUserId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/conversation/${otherUserId}`);
  }

  markAsRead(senderId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/read/${senderId}`, {});
  }
}
