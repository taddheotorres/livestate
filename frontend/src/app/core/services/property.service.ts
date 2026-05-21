import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PropertyService {
  private apiUrl = 'http://localhost:8081/api/properties';

  constructor(private http: HttpClient) { }

  getAllProperties(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getPropertyById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  createProperty(property: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, property);
  }

  getAgentById(id: number): Observable<any> {
    return this.http.get<any>(`http://localhost:8081/api/users/${id}`);
  }

  getCurrentUser(): Observable<any> {
    return this.http.get<any>('http://localhost:8081/api/users/me');
  }

  updateCurrentUser(user: any): Observable<any> {
    return this.http.put<any>('http://localhost:8081/api/users/me', user);
  }
}
