import { UserSummary } from './user.model';
import { PropertySummary } from './property.model';

export interface Message {
  id: number;
  sender: UserSummary;
  receiver: UserSummary;
  property?: PropertySummary;
  content: string;
  read: boolean;
  createdAt: string;
}

export interface MessageRequest {
  receiverId: number;
  content: string;
  propertyId?: number;
}
