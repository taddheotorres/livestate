import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { PropertyService } from '../../core/services/property.service';

@Component({
  selector: 'app-property-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './property-form.component.html',
  styleUrl: './property-form.component.scss'
})
export class PropertyFormComponent implements OnInit {
  property: any = {
    title: '',
    location: 'La Paz, BCS', // Valor por defecto sugerido
    type: 'HOUSE',
    price: null,
    bedrooms: null,
    bathrooms: null,
    areaSqm: null,
    description: '',
    status: 'AVAILABLE'
  };

  uploadedImages: Array<{ imageUrl: string; isPrimary: boolean }> = [];
  imageUrlInput = '';
  isSubmitting = false;
  activePreviewImageIndex = 0;
  isDragging = false;
  currentUser: any = null;

  // Presets de fotos de ejemplo para testing rápido
  photoPresets = [
    {
      name: 'Moderno Brutalista',
      images: [
        'images/glam-house/ejemplo_casa1.jpg',
        'images/glam-house/ejemplo_casa2.jpg',
        'images/glam-house/ejemplo_casa3.jpg',
        'images/glam-house/ejemplo_casa4.jpg'
      ]
    },
    {
      name: 'Residencia Palmas',
      images: [
        'images/palmas/ejemplo2_casa1.jpg',
        'images/palmas/ejemplo2_casa2.jpg',
        'images/palmas/ejemplo2_casa3.jpg',
        'images/palmas/ejemplo2_casa4.jpg'
      ]
    },
    {
      name: 'Bahía Chula Vista',
      images: [
        'images/chula-vista/ejemplo3_casa1.jpg',
        'images/chula-vista/ejemplo3_casa2.jpg',
        'images/chula-vista/ejemplo3_casa3.jpg',
        'images/chula-vista/ejemplo3_casa4.jpg'
      ]
    }
  ];

  constructor(
    private propertyService: PropertyService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.propertyService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUser = user;
      },
      error: (err) => {
        console.warn('Usuario no autenticado para previsualización, usando Nelva Torres como fallback.', err);
      }
    });
  }

  // Agregar imagen por URL escrita
  addImageUrl() {
    if (this.imageUrlInput.trim()) {
      const isFirst = this.uploadedImages.length === 0;
      this.uploadedImages.push({
        imageUrl: this.imageUrlInput.trim(),
        isPrimary: isFirst
      });
      this.imageUrlInput = '';
      if (isFirst) {
        this.activePreviewImageIndex = 0;
      }
    }
  }

  // Carga de imágenes locales y lectura a Base64
  onFileSelected(event: any) {
    const files = event.target.files;
    if (files) {
      this.processFiles(files);
    }
  }

  // Manejo de arrastrar archivos (Drag & Drop)
  onDragOver(event: DragEvent) {
    event.preventDefault();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    this.isDragging = false;
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    this.isDragging = false;
    const files = event.dataTransfer?.files;
    if (files) {
      this.processFiles(files);
    }
  }

  private processFiles(files: FileList) {
    for (let i = 0; i < files.length; i++) {
      const file = files[i];
      if (file.type.startsWith('image/')) {
        const reader = new FileReader();
        reader.onload = (e: any) => {
          const isFirst = this.uploadedImages.length === 0;
          this.uploadedImages.push({
            imageUrl: e.target.result,
            isPrimary: isFirst
          });
          if (isFirst) {
            this.activePreviewImageIndex = 0;
          }
        };
        reader.readAsDataURL(file);
      }
    }
  }

  // Cargar preset de fotos de ejemplo
  loadPreset(presetImages: string[]) {
    this.uploadedImages = [];
    presetImages.forEach((img, idx) => {
      this.uploadedImages.push({
        imageUrl: img,
        isPrimary: idx === 0
      });
    });
    this.activePreviewImageIndex = 0;
  }

  // Establecer como foto de portada
  setPrimary(index: number) {
    this.uploadedImages.forEach((img, idx) => {
      img.isPrimary = idx === index;
    });
  }

  // Remover foto
  removeImage(index: number) {
    const wasPrimary = this.uploadedImages[index].isPrimary;
    this.uploadedImages.splice(index, 1);
    
    // Si borramos la foto primaria y quedan otras, poner la primera como primaria
    if (wasPrimary && this.uploadedImages.length > 0) {
      this.uploadedImages[0].isPrimary = true;
    }
    
    // Corregir índice de previsualización activo
    if (this.activePreviewImageIndex >= this.uploadedImages.length) {
      this.activePreviewImageIndex = Math.max(0, this.uploadedImages.length - 1);
    }
  }

  setActivePreview(index: number) {
    this.activePreviewImageIndex = index;
  }

  onSubmit() {
    this.isSubmitting = true;

    // Asignar lista de imágenes formateada para el modelo del backend
    this.property.images = this.uploadedImages.map(img => ({
      imageUrl: img.imageUrl,
      isPrimary: img.isPrimary
    }));

    // Asegurar que si no hay imágenes, al menos inicialice un arreglo vacío
    if (!this.property.images) {
      this.property.images = [];
    }

    this.propertyService.createProperty(this.property).subscribe({
      next: (res) => {
        this.isSubmitting = false;
        this.router.navigate(['/properties', res.id]);
      },
      error: (err) => {
        console.error('Error al publicar espacio:', err);
        this.isSubmitting = false;
        alert('Hubo un error al publicar el espacio. Revisa los datos de entrada.');
      }
    });
  }
}
