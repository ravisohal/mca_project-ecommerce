import { Routes } from '@angular/router';
import { LayoutComponent } from './components/layout/layout';

export const routes: Routes = [
    {
        path: '',
        component: LayoutComponent,
        children: [
            { path: '', redirectTo: 'products', pathMatch: 'full' },
            { 
                path: 'auth', 
                loadChildren: () => import('./routers/auth.routes').then(m => m.AUTH_ROUTES) 
            },
            { 
                path: 'products', 
                loadChildren: () => import('./routers/product.routes').then(m => m.PRODUCT_ROUTES) 
            },
            { 
                path: 'cart', 
                loadChildren: () => import('./routers/cart.routes').then(m => m.CART_ROUTES) 
            },
            { 
                path: 'orders', 
                loadChildren: () => import('./routers/order.routes').then(m => m.ORDER_ROUTES) 
            },
            { 
                path: 'user', 
                loadChildren: () => import('./routers/user.routes').then(m => m.USER_ROUTES) 
            },
            { 
                path: 'admin', 
                loadChildren: () => import('./routers/admin.routes').then(m => m.ADMIN_ROUTES) 
            },
        ]
    },
    { path: '**', redirectTo: 'products' } // catch-all route
];
