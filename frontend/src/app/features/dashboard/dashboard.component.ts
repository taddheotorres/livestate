import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from '../../core/auth/auth.service';
import { PropertyService } from '../../core/services/property.service';
import { User } from '../../core/models/user.model';
import { TenantDashboardComponent } from './tenant-dashboard/tenant-dashboard.component';
import { HostDashboardComponent } from './host-dashboard/host-dashboard.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, TenantDashboardComponent, HostDashboardComponent],
  template: `
    <div class="dashboard-wrapper">
      <div class="sidebar">
        <h3 class="sidebar-title">Mi Actividad</h3>
        <nav class="sidebar-nav">
          <a routerLink="/dashboard" class="nav-item active">Panel Principal</a>
          <a *ngIf="user" [routerLink]="['/agents', user.id]" class="nav-item">Mi Perfil Público</a>
          <a routerLink="/catalog" class="nav-item">Explorar Espacios</a>
        </nav>
      </div>
      
      <div class="main-content">
        <div *ngIf="loading" class="loading-state">Cargando panel...</div>
        
        <ng-container *ngIf="!loading && user">
          <app-tenant-dashboard *ngIf="user.role === 'USER'"></app-tenant-dashboard>
          <app-host-dashboard *ngIf="user.role === 'AGENT'"></app-host-dashboard>
        </ng-container>
        
        <div *ngIf="!loading && !user" class="error-state">
          No tienes sesión activa. <a routerLink="/login">Inicia sesión</a>.
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-wrapper {
      display: flex;
      min-height: calc(100vh - 64px);
      max-width: 1400px;
      margin: 0 auto;
    }

    .sidebar {
      width: 250px;
      background: var(--bg-secondary);
      border-right: 1px solid var(--border-light);
      padding: 2rem 1.5rem;
      flex-shrink: 0;
    }

    .sidebar-title {
      font-size: 0.85rem;
      text-transform: uppercase;
      letter-spacing: 0.1em;
      color: var(--text-muted);
      margin-bottom: 1.5rem;
    }

    .sidebar-nav {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
    }

    .nav-item {
      text-decoration: none;
      color: var(--text-secondary);
      padding: 0.75rem 1rem;
      border-radius: 6px;
      font-size: 0.95rem;
      transition: all 0.2s;

      &:hover {
        background: rgba(0, 0, 0, 0.05);
        color: var(--text-primary);
      }
      
      &.active {
        background: var(--bg-card);
        color: var(--text-primary);
        font-weight: 500;
        box-shadow: var(--shadow-sm);
      }
    }

    :global([data-theme='dark']) .nav-item:hover {
      background: rgba(255, 255, 255, 0.05);
    }

    .main-content {
      flex: 1;
      padding: 2rem 3rem;
      overflow-y: auto;
    }

    .loading-state, .error-state {
      padding: 3rem;
      text-align: center;
      color: var(--text-muted);
    }

    @media (max-width: 768px) {
      .dashboard-wrapper {
        flex-direction: column;
      }
      .sidebar {
        width: 100%;
        border-right: none;
        border-bottom: 1px solid var(--border-light);
        padding: 1.5rem;
      }
      .main-content {
        padding: 1.5rem;
      }
    }
  `]
})
export class DashboardComponent implements OnInit, OnDestroy {
  user: User | null = null;
  loading = true;
  private subs: Subscription[] = [];

  constructor(
    private authService: AuthService,
    private propertyService: PropertyService,
    private router: Router
  ) {}

  ngOnInit() {
    this.subs.push(
      this.authService.isLoggedIn$.subscribe(isLoggedIn => {
        if (isLoggedIn) {
          this.subs.push(
            this.propertyService.getCurrentUser().subscribe({
              next: (user) => {
                this.user = user;
                this.loading = false;
              },
              error: () => {
                this.loading = false;
              }
            })
          );
        } else {
          this.loading = false;
          this.router.navigate(['/login']);
        }
      })
    );
  }

  ngOnDestroy() {
    this.subs.forEach(s => s.unsubscribe());
  }
}
