import { Injectable, PLATFORM_ID, Inject } from '@angular/core';
import { ApiService } from '../services/api.service';
import { Observable, tap, BehaviorSubject } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private loggedIn = new BehaviorSubject<boolean>(this.hasToken());
  
  isLoggedIn$ = this.loggedIn.asObservable();

  constructor(
    private api: ApiService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  private hasToken(): boolean {
    if (isPlatformBrowser(this.platformId)) {
      return !!localStorage.getItem('token');
    }
    return false;
  }

  register(user: any): Observable<any> {
    return this.api.post('/auth/register', user).pipe(
      tap((res: any) => {
        if (isPlatformBrowser(this.platformId) && res.token) {
          localStorage.setItem('token', res.token);
          this.loggedIn.next(true);
        }
      })
    );
  }

  login(credentials: any): Observable<any> {
    return this.api.post('/auth/login', credentials).pipe(
      tap((res: any) => {
        if (isPlatformBrowser(this.platformId) && res.token) {
          localStorage.setItem('token', res.token);
          this.loggedIn.next(true);
        }
      })
    );
  }

  logout() {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('token');
    }
    this.loggedIn.next(false);
  }

  getToken(): string | null {
    if (isPlatformBrowser(this.platformId)) {
      return localStorage.getItem('token');
    }
    return null;
  }
}
