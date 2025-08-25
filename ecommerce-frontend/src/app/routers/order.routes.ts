import { Routes } from "@angular/router";
import { OrderDetailComponent } from "../pages/order-detail/order-detail";
import { OrderListComponent } from "../pages/order-list/order-list";
import { authGuard } from '../components/guards/auth.guard';

export const ORDER_ROUTES: Routes = [
  { path: '', canActivate: [authGuard], component: OrderListComponent },
  { path: ':id', canActivate: [authGuard], component: OrderDetailComponent },
];
