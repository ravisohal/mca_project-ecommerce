import { Routes } from "@angular/router";
import { ProductDetailComponent } from "../components/product-detail/product-detail";
import { ProductListComponent } from "../components/product-list/product-list";

export const PRODUCT_ROUTES: Routes = [
  { path: '', component: ProductListComponent },
  { path: ':id', component: ProductDetailComponent },
];
