import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Booking, BookingRequest } from '../models/booking.model';

@Injectable({ providedIn: 'root' })
export class BookingService {
  private apiUrl = `${environment.apiUrl}/bookings`;
  private paymentsUrl = `${environment.apiUrl}/payments`;

  constructor(private http: HttpClient) {}

  createBooking(booking: BookingRequest): Observable<Booking> {
    return this.http.post<Booking>(this.apiUrl, booking);
  }

  getMyBookings(): Observable<Booking[]> {
    return this.http.get<Booking[]>(`${this.apiUrl}/my`);
  }

  getIncomingBookings(): Observable<Booking[]> {
    return this.http.get<Booking[]>(`${this.apiUrl}/incoming`);
  }

  updateBookingStatus(id: number, status: 'CONFIRMED' | 'CANCELLED'): Observable<Booking> {
    return this.http.put<Booking>(`${this.apiUrl}/${id}/status`, { status });
  }

  createCheckoutSession(bookingId: number): Observable<{ url: string }> {
    return this.http.post<{ url: string }>(`${this.paymentsUrl}/create-checkout-session`, { bookingId });
  }
}
