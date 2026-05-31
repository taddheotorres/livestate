import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent) },
  { path: 'catalog', loadComponent: () => import('./features/catalog/catalog.component').then(m => m.CatalogComponent) },
  { path: 'dashboard', loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent), canActivate: [authGuard] },
  { path: 'login', loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent) },
  { path: 'properties/new', loadComponent: () => import('./features/property-form/property-form.component').then(m => m.PropertyFormComponent), canActivate: [authGuard] },
  { path: 'properties/:id/edit', loadComponent: () => import('./features/property-form/property-form.component').then(m => m.PropertyFormComponent), canActivate: [authGuard] },
  { path: 'properties/:id', loadComponent: () => import('./features/property-detail/property-detail.component').then(m => m.PropertyDetailComponent) },
  { path: 'agents/:id', loadComponent: () => import('./features/agent-profile/agent-profile.component').then(m => m.AgentProfileComponent) },
  { path: '**', redirectTo: '' }
];
