import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Property } from '../models/property.model';
import { FavoriteToggleResponse } from '../models/favorite.model';

@Injectable({
  providedIn: 'root'
})
export class FavoriteService {
  private apiUrl = `${environment.apiUrl}/favorites`;

  constructor(private http: HttpClient) {}

  getMyFavorites(): Observable<Property[]> {
    return this.http.get<Property[]>(this.apiUrl);
  }

  checkFavorite(propertyId: number): Observable<FavoriteToggleResponse> {
    return this.http.get<FavoriteToggleResponse>(`${this.apiUrl}/check/${propertyId}`);
  }

  toggleFavorite(propertyId: number): Observable<FavoriteToggleResponse> {
    return this.http.post<FavoriteToggleResponse>(`${this.apiUrl}/${propertyId}/toggle`, {});
  }
}
