export interface User {
  id: number;
  name: string;
  email: string;
  role: 'USER' | 'AGENT' | 'ADMIN';
  rating: number;
  reviewsCount: number;
  recommended: boolean;
  bio: string;
  phone: string;
  createdAt: string;
}

export interface UserSummary {
  id: number;
  name: string;
  email: string;
  rating: number;
  reviewsCount: number;
  bio: string;
  phone: string;
}
