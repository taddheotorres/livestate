import { UserSummary } from './user.model';

export interface PropertyImage {
  id?: number;
  imageUrl: string;
  isPrimary: boolean;
}

export interface Property {
  id: number;
  title: string;
  description: string;
  price: number;
  location: string;
  bedrooms: number;
  bathrooms: number;
  areaSqm: number;
  type: 'HOUSE' | 'APARTMENT' | 'COMMERCIAL' | 'LAND';
  status: 'AVAILABLE' | 'SOLD' | 'RENTED';
  agent: UserSummary;
  images: PropertyImage[];
  createdAt: string;
  updatedAt: string;
}

export interface PropertySummary {
  id: number;
  title: string;
  location: string;
  price: number;
  imageUrl: string;
}

export interface PropertyRequest {
  title: string;
  description?: string;
  price: number;
  location: string;
  bedrooms?: number;
  bathrooms?: number;
  areaSqm?: number;
  type: 'HOUSE' | 'APARTMENT' | 'COMMERCIAL' | 'LAND';
  status: 'AVAILABLE' | 'SOLD' | 'RENTED';
  images?: { imageUrl: string; isPrimary: boolean }[];
}
