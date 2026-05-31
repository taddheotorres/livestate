import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';
import { Property, PropertyRequest } from '../models/property.model';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class PropertyService {

  constructor(private api: ApiService) { }

  getAllProperties(): Observable<Property[]> {
    return this.api.get<Property[]>('/properties');
  }

  getPropertyById(id: number): Observable<Property> {
    return this.api.get<Property>(`/properties/${id}`);
  }

  createProperty(property: PropertyRequest): Observable<Property> {
    return this.api.post<Property>('/properties', property);
  }

  updateProperty(id: number, property: PropertyRequest): Observable<Property> {
    return this.api.put<Property>(`/properties/${id}`, property);
  }

  deleteProperty(id: number): Observable<void> {
    return this.api.delete<void>(`/properties/${id}`);
  }

  getMyProperties(): Observable<Property[]> {
    return this.api.get<Property[]>('/properties/my');
  }

  getAgentById(id: number): Observable<User> {
    return this.api.get<User>(`/users/${id}`);
  }

  getCurrentUser(): Observable<User> {
    return this.api.get<User>('/users/me');
  }

  updateCurrentUser(user: Partial<User>): Observable<User> {
    return this.api.put<User>('/users/me', user);
  }
}
