import { Routes } from "@angular/router";
import { AddressComponent } from "../pages/address/address";
import { ProfileComponent } from "../pages/profile/profile";
import { authGuard } from '../components/guards/auth.guard';

export const USER_ROUTES: Routes = [
  { path: 'profile', canActivate: [authGuard], component: ProfileComponent },
  { path: 'address', canActivate: [authGuard],component: AddressComponent },
  { path: 'dashboard', canActivate: [authGuard], loadComponent: () => import('../pages/customer-dashboard/customer-dashboard').then(m => m.CustomerDashboardComponent) },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
];
