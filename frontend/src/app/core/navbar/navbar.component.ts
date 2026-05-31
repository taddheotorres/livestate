import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from '../../core/auth/auth.service';
import { PropertyService } from '../services/property.service';
import { User } from '../models/user.model';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent implements OnInit, OnDestroy {
  isDarkMode = false;
  isLoggedIn = false;
  currentUser: User | null = null;
  private subs: Subscription[] = [];

  constructor(
    private authService: AuthService,
    private propertyService: PropertyService,
    private router: Router
  ) {}

  ngOnInit() {
    this.subs.push(
      this.authService.isLoggedIn$.subscribe(state => {
        this.isLoggedIn = state;
        if (state) {
          this.subs.push(
            this.propertyService.getCurrentUser().subscribe({
              next: (user) => {
                this.currentUser = user;
              },
              error: () => {
                console.warn('No se pudo obtener el usuario actual.');
              }
            })
          );
        } else {
          this.currentUser = null;
        }
      })
    );
  }

  ngOnDestroy() {
    this.subs.forEach(s => s.unsubscribe());
  }

  toggleTheme() {
    this.isDarkMode = !this.isDarkMode;
    if (this.isDarkMode) {
      document.documentElement.setAttribute('data-theme', 'dark');
    } else {
      document.documentElement.removeAttribute('data-theme');
    }
  }

  logout() {
    this.authService.logout();
    this.currentUser = null;
    this.router.navigate(['/']);
  }
}

