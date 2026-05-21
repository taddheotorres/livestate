import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class MessageService {
  private apiUrl = 'http://localhost:8081/api/messages';

  constructor(private http: HttpClient) {}

  sendMessage(msg: {
    receiverId: number;
    content: string;
    propertyId?: number;
  }): Observable<any> {
    return this.http.post<any>(this.apiUrl, msg);
  }

  getConversation(otherUserId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/conversation/${otherUserId}`);
  }

  markAsRead(senderId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/read/${senderId}`, {});
  }
}
