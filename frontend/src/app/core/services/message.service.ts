import { Injectable, PLATFORM_ID, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { Client, Message } from '@stomp/stompjs';
import { isPlatformBrowser } from '@angular/common';
import { environment } from '../../../environments/environment';
import { Message as ChatMessage, MessageRequest } from '../models/message.model';

@Injectable({ providedIn: 'root' })
export class MessageService {
  private apiUrl = `${environment.apiUrl}/messages`;
  private stompClient: Client | null = null;
  private messageSubject = new Subject<ChatMessage>();

  public onMessageReceived$ = this.messageSubject.asObservable();

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  connect(token: string) {
    if (!isPlatformBrowser(this.platformId)) return;
    if (this.stompClient && this.stompClient.active) return;

    const wsUrl = environment.wsUrl.startsWith('ws')
      ? environment.wsUrl
      : `${window.location.protocol === 'https:' ? 'wss:' : 'ws:'}//${window.location.host}${environment.wsUrl}`;

    this.stompClient = new Client({
      brokerURL: wsUrl,
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

  sendMessage(msg: MessageRequest): Observable<ChatMessage> {
    return this.http.post<ChatMessage>(this.apiUrl, msg);
  }

  getConversation(otherUserId: number): Observable<ChatMessage[]> {
    return this.http.get<ChatMessage[]>(`${this.apiUrl}/conversation/${otherUserId}`);
  }

  markAsRead(senderId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/read/${senderId}`, {});
  }
}
