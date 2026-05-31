import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FavoriteService } from '../../../core/services/favorite.service';
import { Property } from '../../../core/models/property.model';

@Component({
  selector: 'app-property-card',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './property-card.component.html',
  styleUrl: './property-card.component.scss'
})
export class PropertyCardComponent implements OnInit {
  @Input() property?: Property;
  isFavorite = false;

  constructor(private favoriteService: FavoriteService) {}

  ngOnInit() {
    if (this.property && this.property.id) {
      this.favoriteService.checkFavorite(this.property.id).subscribe({
        next: (res) => this.isFavorite = res.isFavorite,
        error: () => this.isFavorite = false
      });
    }
  }

  toggleFavorite(event: Event) {
    event.stopPropagation(); // Prevenir navegación
    event.preventDefault();
    if (!this.property) return;

    this.favoriteService.toggleFavorite(this.property.id).subscribe({
      next: (res) => this.isFavorite = res.isFavorite,
      error: (err) => console.error('Error toggling favorite', err)
    });
  }
}
