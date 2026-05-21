import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FavoriteService {
  private apiUrl = 'http://localhost:8081/api/favorites';

  constructor(private http: HttpClient) {}

  getMyFavorites(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  checkFavorite(propertyId: number): Observable<{ isFavorite: boolean }> {
    return this.http.get<{ isFavorite: boolean }>(`${this.apiUrl}/check/${propertyId}`);
  }

  toggleFavorite(propertyId: number): Observable<{ isFavorite: boolean }> {
    return this.http.post<{ isFavorite: boolean }>(`${this.apiUrl}/${propertyId}/toggle`, {});
  }
}
