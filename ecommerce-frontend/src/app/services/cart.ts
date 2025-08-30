import { InteractionService } from './interaction';
import { Injectable, computed, signal, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { CartItem } from '../models/cart-item';
import { Product } from '../models/product';
import { ApiService } from './api';
import { OrderService } from './order';
import { tap } from 'rxjs/operators';
import { AuthService } from './auth';
import { Observable, of } from 'rxjs';
import { InteractionType } from '../models/interaction-type';

const CART_KEY = 'app_cart_v1';
const EXPIRATION_KEY = 'cart_expiration';
const EXPIRATION_DURATION_MS = 1 * 24 * 60 * 60 * 1000; // 1 day

@Injectable({ providedIn: 'root' })
export class CartService {
  private apiService = inject(ApiService);
  private orderService = inject(OrderService);
  private authService = inject(AuthService);
  private interactionService = inject(InteractionService);

  private router = inject(Router);
  private cartUrl = '/carts';

  private platformId = inject(PLATFORM_ID);

  private _items = signal<CartItem[]>(this.load());
  readonly items = this._items.asReadonly();

  readonly total = computed(() =>
    this._items().reduce((sum, i) => sum + i.product.price * i.quantity, 0)
  );

  readonly itemCount = computed(() =>
    this._items().reduce((sum, i) => sum + i.quantity, 0)
  );

  add(product: Product, qty = 1) {
    const items = [...this._items()];
    const idx = items.findIndex((i) => i.product.id === product.id);
    if (idx >= 0) items[idx] = { ...items[idx], quantity: items[idx].quantity + qty };
    else items.push({
      product, quantity: qty,
      id: '',
      priceAtAddition: 0,
      discountAtAddition: 0,
      total: 0
    });
    this.interactionService.log(InteractionType.ADD_TO_CART, product.id);
    this._items.set(items);
    this.persist();
  }

  setQuantity(productId: number, qty: number) {
    const items = this._items().map((i) =>
      i.product.id === productId ? { ...i, quantity: Math.max(0, qty) } : i
    ).filter(i => i.quantity > 0); // Remove if qty <= 0
    this._items.set(items);
    this.persist();
  }

  remove(productId: number) {
    this._items.set(this._items().filter((i) => i.product.id !== productId));
    this.interactionService.log(InteractionType.REMOVE_FROM_CART, productId);
    this.persist();
  }

  clear() {
    this._items.set([]);
    this.persist();
  }

  persistCartItem(userId: number, productId: number, quantity: number): Observable<any> {
     if (this.authService.isAuthenticated()) {
       return this.apiService.post(`${this.cartUrl}/add`, { userId,productId, quantity });
     }
     return of(null);
  }


  checkout(): Observable<any> {
    if (this.authService.isAuthenticated()) {
      const userId = this.authService.user()?.id || 0;
      const shippingAddressId = this.authService.user()?.shippingAddress?.id || 0;

      for (const item of this._items()) {
        this.persistCartItem(userId, item.product.id, item.quantity).subscribe();
        this.interactionService.log(InteractionType.PURCHASE, item.product.id);
      }

      return this.orderService.create(userId, shippingAddressId).pipe(
        tap(() => {
          this.clear();
          this.router.navigate(['/orders']);
        })
      );
    } else {
      this.router.navigate(['/auth/login'], { queryParams: { returnUrl: this.router.url } });
    }

    return of(null);
  }

  private load(): CartItem[] {
    if (isPlatformBrowser(this.platformId)) {
      const expirationTime = localStorage.getItem(EXPIRATION_KEY);
      const currentTime = new Date().getTime();

      // Check if the expiration time exists and if it has passed
      if (expirationTime && (currentTime > parseInt(expirationTime, 10))) {
        localStorage.removeItem(CART_KEY);
        localStorage.removeItem(EXPIRATION_KEY);
        return [];
      }

      const json = localStorage.getItem(CART_KEY);
      return json ? JSON.parse(json) : [];
    }
    return [];
  }

  private persist() {
    if (isPlatformBrowser(this.platformId)) {
      const expirationTimestamp = new Date().getTime() + EXPIRATION_DURATION_MS;
      localStorage.setItem(EXPIRATION_KEY, expirationTimestamp.toString());
      localStorage.setItem(CART_KEY, JSON.stringify(this._items()));
    }
  }

}