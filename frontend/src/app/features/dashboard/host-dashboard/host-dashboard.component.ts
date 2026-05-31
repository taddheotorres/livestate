import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { BookingService } from '../../../core/services/booking.service';
import { VisitService } from '../../../core/services/visit.service';
import { PropertyService } from '../../../core/services/property.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-host-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="dashboard-panel">
      <h2>Panel de Anfitrión</h2>
      <p class="subtitle">Administra tus espacios, aprueba solicitudes y gestiona visitas.</p>

      <!-- TABS -->
      <div class="tabs">
        <button class="tab" [class.active]="activeTab === 'spaces'" (click)="activeTab = 'spaces'">
          Mis Espacios <span class="tab-count" *ngIf="properties.length">{{ properties.length }}</span>
        </button>
        <button class="tab" [class.active]="activeTab === 'bookings'" (click)="activeTab = 'bookings'">
          Solicitudes de Renta <span class="tab-count" *ngIf="pendingBookings > 0">{{ pendingBookings }}</span>
        </button>
        <button class="tab" [class.active]="activeTab === 'visits'" (click)="activeTab = 'visits'">
          Visitas <span class="tab-count" *ngIf="pendingVisits > 0">{{ pendingVisits }}</span>
        </button>
      </div>

      <!-- TAB: MIS ESPACIOS -->
      <div class="dashboard-section" *ngIf="activeTab === 'spaces'">
        <div class="section-header">
          <h3>Mis Espacios Publicados</h3>
          <a routerLink="/properties/new" class="btn btn-primary btn-sm">+ Publicar Espacio</a>
        </div>
        <div class="items-list" *ngIf="properties.length > 0">
          <div class="property-row" *ngFor="let p of properties">
            <div class="property-thumb">
              <img *ngIf="getPrimaryImage(p)" [src]="getPrimaryImage(p)" [alt]="p.title" />
              <div *ngIf="!getPrimaryImage(p)" class="thumb-placeholder">🏠</div>
            </div>
            <div class="property-info">
              <h4>{{ p.title }}</h4>
              <p>{{ p.location }} · {{ p.bedrooms }} hab · {{ p.bathrooms }} baños · {{ p.areaSqm }}m²</p>
              <p class="price">\${{ p.price | number:'1.0-0' }} <span class="price-unit">/ noche</span></p>
            </div>
            <div class="property-status">
              <span class="status-badge" [ngClass]="p.status">{{ p.status }}</span>
            </div>
            <div class="property-actions">
              <a [routerLink]="['/properties', p.id, 'edit']" class="btn btn-outline btn-sm">Editar</a>
              <button class="btn btn-danger btn-sm" (click)="confirmDelete(p)">Eliminar</button>
            </div>
          </div>
        </div>
        <div class="empty-state" *ngIf="properties.length === 0 && !loadingProperties">
          <p>Aún no has publicado ningún espacio.</p>
          <a routerLink="/properties/new" class="btn btn-primary">Publicar mi primer espacio</a>
        </div>
      </div>

      <!-- TAB: RESERVAS ENTRANTES -->
      <div class="dashboard-section" *ngIf="activeTab === 'bookings'">
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

      <!-- TAB: VISITAS -->
      <div class="dashboard-section" *ngIf="activeTab === 'visits'">
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

      <!-- MODAL DE CONFIRMACIÓN DE ELIMINACIÓN -->
      <div class="delete-overlay" *ngIf="propertyToDelete" (click)="propertyToDelete = null">
        <div class="delete-modal" (click)="$event.stopPropagation()">
          <h3>¿Eliminar "{{ propertyToDelete.title }}"?</h3>
          <p>Esta acción no se puede deshacer. Se eliminarán también las reservas y visitas asociadas.</p>
          <div class="modal-actions">
            <button class="btn btn-outline" (click)="propertyToDelete = null">Cancelar</button>
            <button class="btn btn-danger" (click)="deleteProperty()" [disabled]="deleting">
              {{ deleting ? 'Eliminando...' : 'Sí, eliminar' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-panel { padding: 1rem; }
    h2 { font-size: 1.8rem; margin-bottom: 0.25rem; }
    .subtitle { color: var(--text-secondary); margin-bottom: 1.5rem; }

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

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1rem;
    }
    .section-header h3 { margin: 0; }

    .dashboard-section h3 {
      font-size: 1.2rem;
      margin-bottom: 1rem;
      padding-bottom: 0.5rem;
    }

    .items-list { display: flex; flex-direction: column; gap: 1rem; }

    /* Property Row (Mis Espacios) */
    .property-row {
      background: var(--bg-secondary);
      border-radius: 8px;
      padding: 1rem 1.5rem;
      display: flex;
      align-items: center;
      gap: 1.25rem;
      border: 1px solid var(--border-light);
      transition: box-shadow 0.2s;

      &:hover { box-shadow: 0 2px 12px rgba(0,0,0,0.06); }
    }

    .property-thumb {
      width: 80px;
      height: 60px;
      border-radius: 6px;
      overflow: hidden;
      flex-shrink: 0;

      img { width: 100%; height: 100%; object-fit: cover; }
    }
    .thumb-placeholder {
      width: 100%;
      height: 100%;
      background: var(--border-light);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1.5rem;
    }

    .property-info {
      flex: 1;
      min-width: 0;

      h4 { margin: 0 0 0.25rem; font-size: 1.05rem; }
      p { margin: 0; color: var(--text-secondary); font-size: 0.85rem; }
      .price { font-weight: 500; color: var(--text-primary); margin-top: 0.25rem; }
      .price-unit { font-weight: 400; color: var(--text-secondary); font-size: 0.8rem; }
    }

    .property-status { flex-shrink: 0; }

    .property-actions {
      display: flex;
      gap: 0.5rem;
      flex-shrink: 0;
    }

    /* Item cards (Bookings / Visits) */
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

    .item-actions { display: flex; gap: 0.5rem; }

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
      &.AVAILABLE { background: rgba(46, 204, 113, 0.15); color: #2ecc71; }
      &.SOLD { background: rgba(52, 152, 219, 0.15); color: #3498db; }
      &.RENTED { background: rgba(155, 89, 182, 0.15); color: #9b59b6; }
    }

    .empty-state {
      padding: 3rem 2rem;
      text-align: center;
      background: var(--bg-secondary);
      border-radius: 8px;
      color: var(--text-muted);

      .btn { margin-top: 1rem; }
    }

    .btn-danger {
      background: #e74c3c;
      color: white;
      border: none;
      padding: 0.35rem 1rem;
      border-radius: 4px;
      font-size: 0.8rem;
      cursor: pointer;
      font-family: inherit;
      transition: background 0.2s;

      &:hover { background: #c0392b; }
      &:disabled { opacity: 0.6; cursor: not-allowed; }
    }

    /* Delete confirmation modal */
    .delete-overlay {
      position: fixed;
      inset: 0;
      background: rgba(0,0,0,0.5);
      z-index: 2000;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .delete-modal {
      background: var(--bg-primary);
      padding: 2rem;
      border-radius: 12px;
      max-width: 420px;
      width: 90%;
      border: 1px solid var(--border-light);

      h3 { margin: 0 0 0.5rem; }
      p { color: var(--text-secondary); font-size: 0.9rem; margin-bottom: 1.5rem; }
    }
    .modal-actions {
      display: flex;
      gap: 0.75rem;
      justify-content: flex-end;
    }

    @media (max-width: 768px) {
      .property-row { flex-wrap: wrap; }
      .property-actions { width: 100%; justify-content: flex-end; }
      .tabs { overflow-x: auto; }
    }
  `]
})
export class HostDashboardComponent implements OnInit {
  activeTab: 'spaces' | 'bookings' | 'visits' = 'spaces';
  bookings: any[] = [];
  visits: any[] = [];
  properties: any[] = [];
  loadingBookings = true;
  loadingVisits = true;
  loadingProperties = true;
  propertyToDelete: any = null;
  deleting = false;

  get pendingBookings(): number {
    return this.bookings.filter(b => b.status === 'PENDING').length;
  }
  get pendingVisits(): number {
    return this.visits.filter(v => v.status === 'PENDING').length;
  }

  constructor(
    private bookingService: BookingService,
    private visitService: VisitService,
    private propertyService: PropertyService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.propertyService.getMyProperties().subscribe({
      next: (data) => { this.properties = data; this.loadingProperties = false; },
      error: () => { this.loadingProperties = false; }
    });
    this.bookingService.getIncomingBookings().subscribe({
      next: (data) => { this.bookings = data; this.loadingBookings = false; },
      error: () => { this.loadingBookings = false; }
    });
    this.visitService.getIncomingVisits().subscribe({
      next: (data) => { this.visits = data; this.loadingVisits = false; },
      error: () => { this.loadingVisits = false; }
    });
  }

  getPrimaryImage(property: any): string | null {
    if (property.images && property.images.length > 0) {
      const primary = property.images.find((img: any) => img.isPrimary);
      return primary ? primary.imageUrl : property.images[0].imageUrl;
    }
    return null;
  }

  confirmDelete(property: any) {
    this.propertyToDelete = property;
  }

  deleteProperty() {
    if (!this.propertyToDelete) return;
    this.deleting = true;
    this.propertyService.deleteProperty(this.propertyToDelete.id).subscribe({
      next: () => {
        this.properties = this.properties.filter(p => p.id !== this.propertyToDelete.id);
        this.propertyToDelete = null;
        this.deleting = false;
      },
      error: (err) => {
        console.error('Error al eliminar:', err);
        this.deleting = false;
        this.toastService.show('Error al eliminar la propiedad.', 'error');
      }
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
