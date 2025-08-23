import { Routes } from '@angular/router';
import { AdminLayoutComponent } from '../pages/admin/layout/layout';
import { authGuard } from '../components/guards/auth.guard';

export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    component: AdminLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('../pages/admin/dashboard/dashboard').then(m => m.AdminDashboardComponent) },
      { path: 'products', loadComponent: () => import('../pages/admin/products/products').then(m => m.AdminProductsComponent) },
      { path: 'categories', loadComponent: () => import('../pages/admin/categories/categories').then(m => m.AdminCategoriesComponent) },
      { path: 'orders', loadComponent: () => import('../pages/admin/orders/orders').then(m => m.AdminOrdersComponent) },
      { path: 'users', loadComponent: () => import('../pages/admin/users/users').then(m => m.AdminUsersComponent) },
    ]
  }
];
