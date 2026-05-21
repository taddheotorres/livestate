import { Component, OnInit, OnDestroy, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { PropertyCardComponent } from '../../shared/components/property-card/property-card.component';
import { PropertyService } from '../../core/services/property.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, PropertyCardComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit, OnDestroy {
  featuredProperties: any[] = [];

  // Carrusel Hero — slides con propertyId para navegación directa
  // Los IDs corresponden al orden de inserción del DataInitializer: 1=GlamHouse, 2=Palmas, 3=ChulaVista
  heroSlides = [
    // Glam House
    { propertyId: 1, images: ['images/glam-house/ejemplo_casa1.jpg', 'images/glam-house/ejemplo_casa2.jpg'], name: 'Glam House', location: 'La Paz, BCS', tag: 'Destacado' },
    { propertyId: 1, images: ['images/glam-house/ejemplo_casa3.jpg', 'images/glam-house/ejemplo_casa4.jpg'], name: 'Glam House', location: 'La Paz, BCS', tag: 'Disponible' },
    { propertyId: 1, images: ['images/glam-house/ejemplo_casa5.jpg', 'images/glam-house/ejemplo_casa1.jpg'], name: 'Glam House', location: 'La Paz, BCS', tag: 'Diseño Único' },
    // Casa Palmas
    { propertyId: 2, images: ['images/palmas/ejemplo2_casa1.jpg', 'images/palmas/ejemplo2_casa2.jpg'], name: 'Casa Palmas', location: 'La Paz, BCS', tag: 'En Renta' },
    { propertyId: 2, images: ['images/palmas/ejemplo2_casa3.jpg', 'images/palmas/ejemplo2_casa4.jpg'], name: 'Casa Palmas', location: 'La Paz, BCS', tag: 'Exclusivo' },
    // Casa Chula Vista
    { propertyId: 3, images: ['images/chula-vista/ejemplo3_casa1.jpg', 'images/chula-vista/ejemplo3_casa2.jpg'], name: 'Casa Chula Vista', location: 'La Paz, BCS', tag: 'Nueva' },
    { propertyId: 3, images: ['images/chula-vista/ejemplo3_casa3.jpg', 'images/chula-vista/ejemplo3_casa4.jpg'], name: 'Casa Chula Vista', location: 'La Paz, BCS', tag: 'Arquitectura Moderna' },
    { propertyId: 3, images: ['images/chula-vista/ejemplo3_casa5.jpg', 'images/chula-vista/ejemplo3_casa6.jpg'], name: 'Casa Chula Vista', location: 'La Paz, BCS', tag: 'Vista Panorámica' }
  ];

  currentSlide = 0;
  private slideInterval: any;

  constructor(
    private propertyService: PropertyService,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngOnInit(): void {
    this.propertyService.getAllProperties().subscribe({
      next: (data) => {
        this.featuredProperties = data;
      },
      error: (error) => {
        console.error('Error al cargar propiedades:', error);
      }
    });
    this.startAutoplay();
  }

  ngOnDestroy(): void {
    if (this.slideInterval) clearInterval(this.slideInterval);
  }

  startAutoplay() {
    if (isPlatformBrowser(this.platformId)) {
      this.slideInterval = setInterval(() => {
        this.nextSlide();
      }, 4500);
    }
  }

  nextSlide() {
    this.currentSlide = (this.currentSlide + 1) % this.heroSlides.length;
  }

  prevSlide() {
    this.currentSlide = (this.currentSlide - 1 + this.heroSlides.length) % this.heroSlides.length;
  }

  goToSlide(index: number) {
    this.currentSlide = index;
  }

  goToProperty(propertyId: number) {
    this.router.navigate(['/properties', propertyId]);
  }
}

