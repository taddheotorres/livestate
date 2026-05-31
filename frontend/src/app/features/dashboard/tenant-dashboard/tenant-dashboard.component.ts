import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { BookingService } from '../../../core/services/booking.service';
import { VisitService } from '../../../core/services/visit.service';
import { FavoriteService } from '../../../core/services/favorite.service';
import { ToastService } from '../../../core/services/toast.service';
import { PropertyCardComponent } from '../../../shared/components/property-card/property-card.component';

@Component({
  selector: 'app-tenant-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, PropertyCardComponent],
  template: `
    <div class="dashboard-panel">
      <h2>Panel de Inquilino</h2>
      <p class="subtitle">Gestiona tus favoritos, solicitudes de renta y visitas programadas.</p>

      <!-- TABS -->
      <div class="tabs">
        <button class="tab" [class.active]="activeTab === 'favorites'" (click)="activeTab = 'favorites'">
          Mis Favoritos <span class="tab-count" *ngIf="favorites.length">{{ favorites.length }}</span>
        </button>
        <button class="tab" [class.active]="activeTab === 'bookings'" (click)="activeTab = 'bookings'">
          Solicitudes de Renta <span class="tab-count" *ngIf="bookings.length">{{ bookings.length }}</span>
        </button>
        <button class="tab" [class.active]="activeTab === 'visits'" (click)="activeTab = 'visits'">
          Visitas Agendadas <span class="tab-count" *ngIf="visits.length">{{ visits.length }}</span>
        </button>
      </div>

      <!-- SECCIÓN: FAVORITOS -->
      <div class="dashboard-section" *ngIf="activeTab === 'favorites'">
        <div class="favorites-grid" *ngIf="favorites.length > 0">
          <app-property-card *ngFor="let fav of favorites" [property]="fav"></app-property-card>
        </div>
        <div class="empty-state" *ngIf="favorites.length === 0 && !loadingFavorites">
          <p>Aún no tienes propiedades favoritas.</p>
          <a routerLink="/catalog" class="btn btn-primary mt-3">Explorar propiedades</a>
        </div>
      </div>

      <!-- SECCIÓN: RESERVAS -->
      <div class="dashboard-section" *ngIf="activeTab === 'bookings'">
        <h3>Tus Solicitudes de Renta</h3>
        <div class="items-list" *ngIf="bookings.length > 0">
          <div class="item-card" *ngFor="let b of bookings">
            <div class="item-info">
              <h4>{{ b.property?.title }}</h4>
              <p>{{ b.startDate | date }} - {{ b.endDate | date }}</p>
              <p class="amount">\${{ b.totalAmount | number }} ({{ b.paymentMethod }})</p>
            </div>
            <div class="item-status">
              <span class="status-badge" [ngClass]="b.status">{{ b.status }}</span>
            </div>
          </div>
        </div>
        <div class="empty-state" *ngIf="bookings.length === 0 && !loadingBookings">
          <p>No tienes solicitudes de renta.</p>
        </div>
      </div>

      <!-- SECCIÓN: VISITAS -->
      <div class="dashboard-section" *ngIf="activeTab === 'visits'">
        <h3>Tus Visitas Agendadas</h3>
        <div class="items-list" *ngIf="visits.length > 0">
          <div class="item-card" *ngFor="let v of visits">
            <div class="item-info">
              <h4>{{ v.property?.title }}</h4>
              <p>Fecha: {{ v.scheduledDate | date }} <span *ngIf="v.scheduledTime">Hora: {{ v.scheduledTime }}</span></p>
              <p>Anfitrión: {{ v.agent?.name }}</p>
            </div>
            <div class="item-status">
              <span class="status-badge" [ngClass]="v.status">{{ v.status }}</span>
            </div>
          </div>
        </div>
        <div class="empty-state" *ngIf="visits.length === 0 && !loadingVisits">
          <p>No tienes visitas agendadas.</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-panel {
      padding: 1rem;
    }
    h2 { font-size: 1.8rem; margin-bottom: 0.25rem; }
    .subtitle { color: var(--text-secondary); margin-bottom: 2rem; }
    .mt-3 { margin-top: 1rem; display: inline-block; }
    .mt-4 { margin-top: 2rem; }
    
    /* Tabs */
    .tabs {
      display: flex;
      gap: 0;
      border-bottom: 1px solid var(--border-light);
      margin-bottom: 2rem;
    }
    .tab {
      background: none;
      border: none;
      padding: 0.75rem 1.5rem;
      font-size: 0.9rem;
      font-family: inherit;
      color: var(--text-secondary);
      cursor: pointer;
      border-bottom: 2px solid transparent;
      transition: all 0.2s;
      display: flex;
      align-items: center;
      gap: 0.5rem;

      &:hover { color: var(--text-primary); }
      &.active {
        color: var(--text-primary);
        border-bottom-color: var(--brand-primary, #95606B);
        font-weight: 500;
      }
    }
    .tab-count {
      background: var(--brand-primary, #95606B);
      color: white;
      font-size: 0.7rem;
      padding: 0.1rem 0.5rem;
      border-radius: 10px;
      font-weight: 600;
    }

    .dashboard-section h3 {
      font-size: 1.2rem;
      margin-bottom: 1rem;
      border-bottom: 1px solid var(--border-light);
      padding-bottom: 0.5rem;
    }

    .favorites-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 2rem;
    }

    .items-list {
      display: flex;
      flex-direction: column;
      gap: 1rem;
    }

    .item-card {
      background: var(--bg-secondary);
      border-radius: 8px;
      padding: 1rem 1.5rem;
      display: flex;
      justify-content: space-between;
      align-items: center;
      border: 1px solid var(--border-light);
    }

    .item-info h4 { margin: 0 0 0.25rem; font-size: 1.1rem; }
    .item-info p { margin: 0 0 0.25rem; color: var(--text-secondary); font-size: 0.9rem; }
    .item-info .amount { font-weight: 500; color: var(--text-primary); }

    .status-badge {
      padding: 0.3rem 0.8rem;
      border-radius: 20px;
      font-size: 0.75rem;
      font-weight: 500;
      text-transform: uppercase;
      letter-spacing: 0.05em;

      &.PENDING { background: rgba(243, 156, 18, 0.15); color: #f39c12; }
      &.CONFIRMED { background: rgba(46, 204, 113, 0.15); color: #2ecc71; }
      &.CANCELLED { background: rgba(231, 76, 60, 0.15); color: #e74c3c; }
      &.DONE { background: rgba(52, 152, 219, 0.15); color: #3498db; }
    }

    .empty-state {
      padding: 2rem;
      text-align: center;
      background: var(--bg-secondary);
      border-radius: 8px;
      color: var(--text-muted);
    }
  `]
})
export class TenantDashboardComponent implements OnInit {
  activeTab: 'favorites' | 'bookings' | 'visits' = 'favorites';
  
  bookings: any[] = [];
  visits: any[] = [];
  favorites: any[] = [];
  
  loadingBookings = true;
  loadingVisits = true;
  loadingFavorites = true;

  constructor(
    private bookingService: BookingService,
    private visitService: VisitService,
    private favoriteService: FavoriteService,
    private toastService: ToastService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      if (params['payment'] === 'success') {
        this.toastService.show('¡Pago completado con éxito! Tu reserva está procesándose.', 'success');
      }
    });
    this.favoriteService.getMyFavorites().subscribe({
      next: (data) => { this.favorites = data; this.loadingFavorites = false; },
      error: () => { this.loadingFavorites = false; }
    });
    this.bookingService.getMyBookings().subscribe({
      next: (data) => { this.bookings = data; this.loadingBookings = false; },
      error: () => { this.loadingBookings = false; }
    });
    this.visitService.getMyVisits().subscribe({
      next: (data) => { this.visits = data; this.loadingVisits = false; },
      error: () => { this.loadingVisits = false; }
    });
  }
}
