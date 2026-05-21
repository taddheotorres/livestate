import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookingService } from '../../../core/services/booking.service';
import { VisitService } from '../../../core/services/visit.service';

@Component({
  selector: 'app-host-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="dashboard-panel">
      <h2>Panel de Anfitrión</h2>
      <p class="subtitle">Administra tus espacios, aprueba solicitudes y gestiona visitas.</p>

      <!-- SECCIÓN: RESERVAS ENTRANTES -->
      <div class="dashboard-section">
        <h3>Solicitudes de Renta</h3>
        <div class="items-list" *ngIf="bookings.length > 0">
          <div class="item-card" *ngFor="let b of bookings">
            <div class="item-info">
              <h4>{{ b.property?.title }} <span class="tenant-name">— Inquilino: {{ b.tenant?.name }}</span></h4>
              <p>{{ b.startDate | date }} - {{ b.endDate | date }}</p>
              <p class="amount">\${{ b.totalAmount | number }}</p>
            </div>
            <div class="item-actions" *ngIf="b.status === 'PENDING'">
              <button class="btn btn-primary btn-sm" (click)="updateBookingStatus(b.id, 'CONFIRMED')">Aprobar</button>
              <button class="btn btn-outline btn-sm" (click)="updateBookingStatus(b.id, 'CANCELLED')">Rechazar</button>
            </div>
            <div class="item-status" *ngIf="b.status !== 'PENDING'">
              <span class="status-badge" [ngClass]="b.status">{{ b.status }}</span>
            </div>
          </div>
        </div>
        <div class="empty-state" *ngIf="bookings.length === 0 && !loadingBookings">
          <p>No tienes solicitudes entrantes.</p>
        </div>
      </div>

      <!-- SECCIÓN: VISITAS ENTRANTES -->
      <div class="dashboard-section mt-4">
        <h3>Visitas Programadas</h3>
        <div class="items-list" *ngIf="visits.length > 0">
          <div class="item-card" *ngFor="let v of visits">
            <div class="item-info">
              <h4>{{ v.property?.title }} <span class="tenant-name">— Visitante: {{ v.visitor?.name }}</span></h4>
              <p>Fecha: {{ v.scheduledDate | date }} <span *ngIf="v.scheduledTime">Hora: {{ v.scheduledTime }}</span></p>
            </div>
            <div class="item-actions" *ngIf="v.status === 'PENDING'">
              <button class="btn btn-primary btn-sm" (click)="updateVisitStatus(v.id, 'CONFIRMED')">Aprobar</button>
              <button class="btn btn-outline btn-sm" (click)="updateVisitStatus(v.id, 'CANCELLED')">Rechazar</button>
            </div>
            <div class="item-actions" *ngIf="v.status === 'CONFIRMED'">
              <button class="btn btn-outline btn-sm" (click)="updateVisitStatus(v.id, 'DONE')">Marcar como Realizada</button>
            </div>
            <div class="item-status" *ngIf="v.status === 'CANCELLED' || v.status === 'DONE'">
              <span class="status-badge" [ngClass]="v.status">{{ v.status }}</span>
            </div>
          </div>
        </div>
        <div class="empty-state" *ngIf="visits.length === 0 && !loadingVisits">
          <p>No tienes visitas programadas.</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-panel { padding: 1rem; }
    h2 { font-size: 1.8rem; margin-bottom: 0.25rem; }
    .subtitle { color: var(--text-secondary); margin-bottom: 2rem; }
    .mt-4 { margin-top: 2rem; }
    
    .dashboard-section h3 {
      font-size: 1.2rem;
      margin-bottom: 1rem;
      border-bottom: 1px solid var(--border-light);
      padding-bottom: 0.5rem;
    }

    .items-list { display: flex; flex-direction: column; gap: 1rem; }

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
    .tenant-name { font-size: 0.9rem; color: var(--text-secondary); font-weight: 400; }
    .item-info p { margin: 0 0 0.25rem; color: var(--text-secondary); font-size: 0.9rem; }
    .item-info .amount { font-weight: 500; color: var(--text-primary); }

    .item-actions {
      display: flex;
      gap: 0.5rem;
    }

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
export class HostDashboardComponent implements OnInit {
  bookings: any[] = [];
  visits: any[] = [];
  loadingBookings = true;
  loadingVisits = true;

  constructor(
    private bookingService: BookingService,
    private visitService: VisitService
  ) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.bookingService.getIncomingBookings().subscribe({
      next: (data) => { this.bookings = data; this.loadingBookings = false; },
      error: () => { this.loadingBookings = false; }
    });
    this.visitService.getIncomingVisits().subscribe({
      next: (data) => { this.visits = data; this.loadingVisits = false; },
      error: () => { this.loadingVisits = false; }
    });
  }

  updateBookingStatus(id: number, status: 'CONFIRMED' | 'CANCELLED') {
    this.bookingService.updateBookingStatus(id, status).subscribe({
      next: () => this.loadData(),
      error: (err) => console.error(err)
    });
  }

  updateVisitStatus(id: number, status: 'CONFIRMED' | 'DONE' | 'CANCELLED') {
    this.visitService.updateVisitStatus(id, status).subscribe({
      next: () => this.loadData(),
      error: (err) => console.error(err)
    });
  }
}
