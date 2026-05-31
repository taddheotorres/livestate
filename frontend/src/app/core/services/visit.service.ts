import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Visit, VisitRequest } from '../models/visit.model';

@Injectable({ providedIn: 'root' })
export class VisitService {
  private apiUrl = `${environment.apiUrl}/visits`;

  constructor(private http: HttpClient) {}

  scheduleVisit(visit: VisitRequest): Observable<Visit> {
    return this.http.post<Visit>(this.apiUrl, visit);
  }

  getMyVisits(): Observable<Visit[]> {
    return this.http.get<Visit[]>(`${this.apiUrl}/my`);
  }

  getIncomingVisits(): Observable<Visit[]> {
    return this.http.get<Visit[]>(`${this.apiUrl}/incoming`);
  }

  updateVisitStatus(id: number, status: 'CONFIRMED' | 'DONE' | 'CANCELLED'): Observable<Visit> {
    return this.http.put<Visit>(`${this.apiUrl}/${id}/status`, { status });
  }
}
