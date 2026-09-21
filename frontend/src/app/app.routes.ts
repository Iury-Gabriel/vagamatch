import { Routes } from '@angular/router';
import { adminGuard, authGuard } from './core/guards';
import { AdminComponent } from './pages/admin.component';
import { DashboardComponent } from './pages/dashboard.component';
import { LandingComponent } from './pages/landing.component';
import { LoginComponent } from './pages/login.component';

export const routes: Routes = [
  { path: '', component: LandingComponent, title: 'VagaMatch — vagas que combinam com você' },
  { path: 'login', component: LoginComponent, title: 'Entrar · VagaMatch' },
  {
    path: 'painel',
    component: DashboardComponent,
    canActivate: [authGuard],
    title: 'Painel · VagaMatch'
  },
  {
    path: 'admin',
    component: AdminComponent,
    canActivate: [authGuard, adminGuard],
    title: 'Admin · VagaMatch'
  },
  { path: '**', redirectTo: '' }
];
