import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  // Paso 1: selección de rol
  selectedRole: 'USER' | 'AGENT' | null = null;
  roleSelected = false;

  // Paso 2: formulario
  user = { name: '', email: '', password: '', role: '' };
  error = '';
  isLoading = false;

  constructor(private authService: AuthService, private router: Router) {}

  selectRole(role: 'USER' | 'AGENT') {
    this.selectedRole = role;
    this.user.role = role;
    // Pequeño delay para la animación de salida antes de mostrar el formulario
    setTimeout(() => {
      this.roleSelected = true;
    }, 280);
  }

  onSubmit() {
    this.isLoading = true;
    this.error = '';
    this.authService.register(this.user).subscribe({
      next: () => {
        this.isLoading = false;
        // Los anfitriones van directo a publicar, los inquilinos al home
        if (this.selectedRole === 'AGENT') {
          this.router.navigate(['/properties/new']);
        } else {
          this.router.navigate(['/']);
        }
      },
      error: (err) => {
        this.isLoading = false;
        this.error = err.status === 400
          ? 'Este correo ya está registrado.'
          : 'Error al registrarse. Intenta de nuevo.';
      }
    });
  }
}
