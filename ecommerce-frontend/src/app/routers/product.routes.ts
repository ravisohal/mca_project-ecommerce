import { Routes } from "@angular/router";
import { ProductDetailComponent } from "../pages/product-detail/product-detail";
import { ProductListComponent } from "../pages/product-list/product-list";

export const PRODUCT_ROUTES: Routes = [
  { path: '', component: ProductListComponent },
  { path: ':id', component: ProductDetailComponent },
];
