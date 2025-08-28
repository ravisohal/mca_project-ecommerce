import { Routes } from '@angular/router';
import { AdminLayoutComponent } from '../components/admin/layout/layout';
import { authGuard } from '../guards/auth.guard';

export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    component: AdminLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('../components/admin/dashboard/dashboard').then(m => m.AdminDashboardComponent) },
      { path: 'products', loadComponent: () => import('../components/admin/products/products').then(m => m.AdminProductsComponent) },
      { path: 'categories', loadComponent: () => import('../components/admin/categories/categories').then(m => m.AdminCategoriesComponent) },
      { path: 'orders', loadComponent: () => import('../components/admin/admin-orders/admin-orders').then(m => m.AdminOrdersComponent) },
      { path: 'users', loadComponent: () => import('../components/admin/users/users').then(m => m.AdminUsersComponent) },
    ]
  }
];
