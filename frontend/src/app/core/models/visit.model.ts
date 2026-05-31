import { PropertySummary } from './property.model';
import { UserSummary } from './user.model';

export interface Visit {
  id: number;
  property: PropertySummary;
  visitor: UserSummary;
  agent: UserSummary;
  scheduledDate: string;
  scheduledTime?: string;
  status: 'PENDING' | 'CONFIRMED' | 'DONE' | 'CANCELLED';
  notes?: string;
  createdAt: string;
}

export interface VisitRequest {
  propertyId: number;
  scheduledDate: string;
  scheduledTime?: string;
  notes?: string;
}
