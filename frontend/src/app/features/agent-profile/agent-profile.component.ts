import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { PropertyService } from '../../core/services/property.service';
import { PropertyCardComponent } from '../../shared/components/property-card/property-card.component';

@Component({
  selector: 'app-agent-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, PropertyCardComponent],
  templateUrl: './agent-profile.component.html',
  styleUrl: './agent-profile.component.scss'
})
export class AgentProfileComponent implements OnInit {
  agent: any = null;
  agentProperties: any[] = [];
  loading = true;
  isOwnProfile = false;
  isEditing = false;
  isSaving = false;

  editForm = {
    name: '',
    bio: ''
  };

  reviews: any[] = [];

  // Reseñas estáticas premium del anfitrión Nelva Torres
  mockReviews = [
    {
      author: 'Eduardo Martínez',
      date: 'Mayo 2026',
      rating: 5,
      comment: 'Nelva fue sumamente profesional y atenta en todo el proceso. Nos hospedamos en Glam House y la experiencia superó toda expectativa. La atención al detalle de los espacios es increíble.'
    },
    {
      author: 'Sofía Valenzuela',
      date: 'Abril 2026',
      rating: 5,
      comment: 'Excelente comunicación y trato. Las propiedades que maneja son verdaderas obras de arte arquitectónico. Todo estaba impecable.'
    },
    {
      author: 'Carlos Mendoza',
      date: 'Febrero 2026',
      rating: 4.8,
      comment: 'Una gran anfitriona. Responde al instante y conoce perfectamente la zona de La Paz. Muy recomendada.'
    }
  ];

  constructor(
    private route: ActivatedRoute,
    private propertyService: PropertyService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const agentId = +params['id'];
      if (agentId) {
        this.loadAgentDetails(agentId);
      }
    });
  }

  loadAgentDetails(id: number) {
    this.loading = true;
    this.propertyService.getAgentById(id).subscribe({
      next: (data) => {
        this.agent = data;
        this.editForm.name = data.name;
        this.editForm.bio = data.bio || '';
        
        // Cargar opiniones de Nelva Torres, de lo contrario dejar vacío para otros usuarios
        if (data.email === 'nelva@livestate.com') {
          this.reviews = this.mockReviews;
        } else {
          this.reviews = [];
        }
        
        this.checkIfOwnProfile(id);
        this.loadAgentProperties(id);
      },
      error: (error) => {
        console.error('Error al cargar datos del agente:', error);
        this.loading = false;
      }
    });
  }

  checkIfOwnProfile(agentId: number) {
    this.propertyService.getCurrentUser().subscribe({
      next: (user) => {
        if (user && user.id === agentId) {
          this.isOwnProfile = true;
        }
      },
      error: () => {
        // Si no está autenticado, simplemente no es su propio perfil
        this.isOwnProfile = false;
      }
    });
  }

  loadAgentProperties(agentId: number) {
    this.propertyService.getAllProperties().subscribe({
      next: (properties) => {
        this.agentProperties = properties.filter(p => p.agent && p.agent.id === agentId);
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar propiedades del agente:', error);
        this.loading = false;
      }
    });
  }

  startEditing() {
    this.isEditing = true;
  }

  cancelEditing() {
    this.editForm.name = this.agent.name;
    this.editForm.bio = this.agent.bio || '';
    this.isEditing = false;
  }

  saveProfile() {
    if (!this.editForm.name.trim()) return;
    
    this.isSaving = true;
    this.propertyService.updateCurrentUser(this.editForm).subscribe({
      next: (res) => {
        this.agent.name = res.name;
        this.agent.bio = res.bio;
        this.isEditing = false;
        this.isSaving = false;
        
        // Disparar recarga parcial de ventana para navbar si es necesario
        window.location.reload();
      },
      error: (err) => {
        console.error('Error al actualizar el perfil:', err);
        alert('Hubo un error al guardar los cambios del perfil.');
        this.isSaving = false;
      }
    });
  }

  // Generar array para dibujar las estrellas en la UI
  getStars(rating: number): number[] {
    const stars = [];
    const floor = Math.floor(rating || 5);
    for (let i = 0; i < floor; i++) stars.push(1);
    return stars;
  }
}
