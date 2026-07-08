import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PropertyCardComponent } from '../../shared/components/property-card/property-card.component';
import { PropertyService } from '../../core/services/property.service';
import { Property } from '../../core/models/property.model';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, PropertyCardComponent],
  templateUrl: './catalog.component.html',
  styleUrl: './catalog.component.scss'
})
export class CatalogComponent implements OnInit {
  allProperties: Property[] = [];
  filteredProperties: Property[] = [];
  loading = true;

  // Filtros
  filters = {
    type: '',        // 'HOUSE' | 'APARTMENT' | ''
    minPrice: null as number | null,
    maxPrice: null as number | null,
    minBedrooms: null as number | null,
  };

  constructor(private propertyService: PropertyService) {}

  ngOnInit(): void {
    this.propertyService.getAllProperties().subscribe({
      next: (data) => {
        this.allProperties = data;
        this.applyFilters();
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  applyFilters() {
    this.filteredProperties = this.allProperties.filter(p => {
      if (this.filters.type && p.type !== this.filters.type) return false;
      if (this.filters.minPrice && p.price < this.filters.minPrice) return false;
      if (this.filters.maxPrice && p.price > this.filters.maxPrice) return false;
      if (this.filters.minBedrooms && p.bedrooms < this.filters.minBedrooms) return false;
      return true;
    });
  }

  resetFilters() {
    this.filters = { type: '', minPrice: null, maxPrice: null, minBedrooms: null };
    this.applyFilters();
  }

  get hasActiveFilters(): boolean {
    return !!(this.filters.type || this.filters.minPrice || this.filters.maxPrice || this.filters.minBedrooms);
  }
}
