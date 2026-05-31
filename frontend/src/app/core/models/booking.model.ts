import { PropertySummary } from './property.model';
import { UserSummary } from './user.model';

export interface Booking {
  id: number;
  property: PropertySummary;
  tenant: UserSummary;
  startDate: string;
  endDate: string;
  totalAmount: number;
  status: 'PENDING' | 'CONFIRMED' | 'CANCELLED';
  paymentMethod: 'CARD' | 'TRANSFER';
  notes?: string;
  createdAt: string;
}

export interface BookingRequest {
  propertyId: number;
  startDate: string;
  endDate: string;
  totalAmount: number;
  paymentMethod: 'CARD' | 'TRANSFER';
  notes?: string;
}
