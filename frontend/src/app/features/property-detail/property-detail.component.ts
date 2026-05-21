import { Component, OnInit, OnDestroy, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PropertyService } from '../../core/services/property.service';
import { AuthService } from '../../core/auth/auth.service';
import { BookingService } from '../../core/services/booking.service';
import { VisitService } from '../../core/services/visit.service';
import { MessageService } from '../../core/services/message.service';

@Component({
  selector: 'app-property-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './property-detail.component.html',
  styleUrl: './property-detail.component.scss'
})
export class PropertyDetailComponent implements OnInit, OnDestroy {
  property: any;
  loading = true;
  activeImageUrl = '';

  // Drawers
  activeDrawer: 'NONE' | 'BOOKING' | 'VISIT' = 'NONE';
  isLoggedIn = false;

  // Booking State
  bookingStartDate = '';
  bookingEndDate = '';
  bookingPaymentMethod: 'CARD' | 'TRANSFER' = 'CARD';
  bookingTotalAmount = 0;
  bookingSuccess = false;
  bookingLoading = false;

  // Visit State
  visitDate = '';
  visitTime = '';
  visitSuccess = false;
  visitLoading = false;
  activeTab: 'INFO' | 'CHAT' = 'INFO'; // Tabs in Visit drawer

  // Chat State
  messages: any[] = [];
  chatInput = '';
  chatPollingInterval: any;

  constructor(
    private route: ActivatedRoute,
    private propertyService: PropertyService,
    private authService: AuthService,
    private bookingService: BookingService,
    private visitService: VisitService,
    private messageService: MessageService,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.isLoggedIn = this.authService.isLoggedIn();
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.propertyService.getPropertyById(+id).subscribe({
        next: (data) => {
          this.property = data;
          this.loading = false;
          if (this.property.images && this.property.images.length > 0) {
            this.activeImageUrl = this.property.images[0].imageUrl;
          }
        },
        error: (err) => {
          console.error(err);
          this.loading = false;
        }
      });
    }
  }

  ngOnDestroy(): void {
    this.stopChatPolling();
  }

  setActiveImage(url: string): void {
    this.activeImageUrl = url;
  }

  // --- Drawers ---
  openBookingDrawer() {
    if (!this.isLoggedIn) {
      this.router.navigate(['/login']);
      return;
    }
    this.activeDrawer = 'BOOKING';
    this.bookingSuccess = false;
  }

  openVisitDrawer() {
    if (!this.isLoggedIn) {
      this.router.navigate(['/login']);
      return;
    }
    this.activeDrawer = 'VISIT';
    this.visitSuccess = false;
    this.activeTab = 'INFO';
  }

  closeDrawer() {
    this.activeDrawer = 'NONE';
    this.stopChatPolling();
  }

  // --- Booking ---
  calculateTotal() {
    if (!this.bookingStartDate || !this.bookingEndDate || !this.property) return;
    const start = new Date(this.bookingStartDate);
    const end = new Date(this.bookingEndDate);
    if (end <= start) {
      this.bookingTotalAmount = 0;
      return;
    }
    const diffTime = Math.abs(end.getTime() - start.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    // Asumimos price es mensual, calculamos por día para ser justos
    const dailyRate = this.property.price / 30;
    this.bookingTotalAmount = Math.round(diffDays * dailyRate);
  }

  submitBooking() {
    if (!this.bookingStartDate || !this.bookingEndDate || this.bookingTotalAmount <= 0) return;
    this.bookingLoading = true;
    this.bookingService.createBooking({
      propertyId: this.property.id,
      startDate: this.bookingStartDate,
      endDate: this.bookingEndDate,
      totalAmount: this.bookingTotalAmount,
      paymentMethod: this.bookingPaymentMethod
    }).subscribe({
      next: () => {
        this.bookingLoading = false;
        this.bookingSuccess = true;
      },
      error: (err) => {
        console.error(err);
        this.bookingLoading = false;
      }
    });
  }

  // --- Visit ---
  submitVisit() {
    if (!this.visitDate) return;
    this.visitLoading = true;
    this.visitService.scheduleVisit({
      propertyId: this.property.id,
      scheduledDate: this.visitDate,
      scheduledTime: this.visitTime ? this.visitTime : undefined
    }).subscribe({
      next: () => {
        this.visitLoading = false;
        this.visitSuccess = true;
      },
      error: (err) => {
        console.error(err);
        this.visitLoading = false;
      }
    });
  }

  // --- Chat ---
  openChatTab() {
    this.activeTab = 'CHAT';
    this.loadMessages();
    this.startChatPolling();
  }

  loadMessages() {
    if (!this.property?.agent?.id) return;
    this.messageService.getConversation(this.property.agent.id).subscribe({
      next: (data) => {
        this.messages = data;
        // Mark as read
        this.messageService.markAsRead(this.property.agent.id).subscribe();
      },
      error: (err) => console.error(err)
    });
  }

  sendMessage() {
    if (!this.chatInput.trim() || !this.property?.agent?.id) return;
    this.messageService.sendMessage({
      receiverId: this.property.agent.id,
      content: this.chatInput.trim(),
      propertyId: this.property.id
    }).subscribe({
      next: (msg) => {
        this.messages.push(msg);
        this.chatInput = '';
      },
      error: (err) => console.error(err)
    });
  }

  startChatPolling() {
    if (isPlatformBrowser(this.platformId) && !this.chatPollingInterval) {
      this.chatPollingInterval = setInterval(() => {
        this.loadMessages();
      }, 5000);
    }
  }

  stopChatPolling() {
    if (this.chatPollingInterval) {
      clearInterval(this.chatPollingInterval);
      this.chatPollingInterval = null;
    }
  }
}
