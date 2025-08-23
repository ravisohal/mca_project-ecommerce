import { Routes } from "@angular/router";
import { AddressComponent } from "../pages/address/address";
import { ProfileComponent } from "../pages/profile/profile";

export const USER_ROUTES: Routes = [
  { path: 'profile', component: ProfileComponent },
  { path: 'address', component: AddressComponent },
];
