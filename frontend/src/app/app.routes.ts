import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { PropertyDetailComponent } from './features/property-detail/property-detail.component';
import { PropertyFormComponent } from './features/property-form/property-form.component';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { AgentProfileComponent } from './features/agent-profile/agent-profile.component';
import { CatalogComponent } from './features/catalog/catalog.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'catalog', component: CatalogComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'login', component: LoginComponent },

  { path: 'register', component: RegisterComponent },
  { path: 'properties/new', component: PropertyFormComponent, canActivate: [authGuard] },
  { path: 'properties/:id/edit', component: PropertyFormComponent, canActivate: [authGuard] },
  { path: 'properties/:id', component: PropertyDetailComponent },
  { path: 'agents/:id', component: AgentProfileComponent },
  { path: '**', redirectTo: '' }
];
