import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class BookingService {
  private apiUrl = 'http://localhost:8081/api/bookings';

  constructor(private http: HttpClient) {}

  createBooking(booking: {
    propertyId: number;
    startDate: string;
    endDate: string;
    totalAmount: number;
    paymentMethod: 'CARD' | 'TRANSFER';
    notes?: string;
  }): Observable<any> {
    return this.http.post<any>(this.apiUrl, booking);
  }

  getMyBookings(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/my`);
  }

  getIncomingBookings(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/incoming`);
  }

  updateBookingStatus(id: number, status: 'CONFIRMED' | 'CANCELLED'): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}/status`, { status });
  }

  createCheckoutSession(bookingId: number): Observable<{ url: string }> {
    return this.http.post<{ url: string }>('http://localhost:8081/api/payments/create-checkout-session', { bookingId });
  }
}
