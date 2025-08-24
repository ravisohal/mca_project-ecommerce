import { Routes } from "@angular/router";
import { CheckoutComponent } from "../pages/checkout/checkout";
import { OrderDetailComponent } from "../pages/order-detail/order-detail";
import { OrderListComponent } from "../pages/order-list/order-list";

import { authGuard } from '../components/guards/auth.guard';
export const ORDER_ROUTES: Routes = [
  { path: '', component: OrderListComponent },
  { path: ':id', component: OrderDetailComponent },
  { path: 'checkout', component: CheckoutComponent, canActivate: [authGuard] }
];
