import { Routes } from "@angular/router";
import { AddressComponent } from "../components/address/address";
import { ProfileComponent } from "../components/profile/profile";
import { authGuard } from '../guards/auth.guard';

export const USER_ROUTES: Routes = [
  { path: 'profile', canActivate: [authGuard], component: ProfileComponent },
  { path: 'address', canActivate: [authGuard],component: AddressComponent },
  { path: 'dashboard', canActivate: [authGuard], loadComponent: () => import('../components/customer-dashboard/customer-dashboard').then(m => m.CustomerDashboardComponent) },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
];
