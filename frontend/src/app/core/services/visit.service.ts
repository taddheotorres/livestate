import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class VisitService {
  private apiUrl = 'http://localhost:8081/api/visits';

  constructor(private http: HttpClient) {}

  scheduleVisit(visit: {
    propertyId: number;
    scheduledDate: string;
    scheduledTime?: string;
    notes?: string;
  }): Observable<any> {
    return this.http.post<any>(this.apiUrl, visit);
  }

  getMyVisits(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/my`);
  }

  getIncomingVisits(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/incoming`);
  }

  updateVisitStatus(id: number, status: 'CONFIRMED' | 'DONE' | 'CANCELLED'): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}/status`, { status });
  }
}
