import { Routes } from "@angular/router";
import { OrderDetailComponent } from "../components/order-detail/order-detail";
import { OrderListComponent } from "../components/order-list/order-list";
import { authGuard } from '../components/guards/auth.guard';

export const ORDER_ROUTES: Routes = [
  { path: '', canActivate: [authGuard], component: OrderListComponent },
  { path: ':id', canActivate: [authGuard], component: OrderDetailComponent },
];
