import { Cart } from './../models/cart';
import { Injectable, computed, signal, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { CartItem } from '../models/cart-item';
import { Product } from '../models/product';
import { Address } from '../models/address';
import { OrderService } from './order';
import { CreateOrderRequest } from '../models/create-order-request';
import { User } from '../models/user';
import { tap } from 'rxjs/operators';


const CART_KEY = 'app_cart_v1';

@Injectable({ providedIn: 'root' })
export class CartService {
  private orderService = inject(OrderService);
  private platformId = inject(PLATFORM_ID);

  private _items = signal<CartItem[]>(this.load());
  readonly items = this._items.asReadonly();

  currentUser = signal<User | null>(null);
  currentAddress = signal<Address | null>(null);

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
    this._items.set(items);
    this.persist();
  }

  setQuantity(productId: number, qty: number) {
    const items = this._items().map((i) =>
      i.product.id === productId ? { ...i, quantity: Math.max(1, qty) } : i
    );
    this._items.set(items);
    this.persist();
  }

  remove(productId: number) {
    this._items.set(this._items().filter((i) => i.product.id !== productId));
    this.persist();
  }

  clear() {
    this._items.set([]);
    this.persist();
  }

  checkout() {
    const req: CreateOrderRequest = {
      items: this._items().map((i) => ({ 
                productId: i.product.id, 
                productName: i.product.name, 
                quantity: i.quantity, 
                price: i.product.price, 
                discount: i.product.discount })),
      totalAmount: this.total(),
      customerEmail: this.currentUser()?.email,
    };
    return this.orderService.create(req).pipe(
      tap(() => this.clear())
    );
  }

  private load(): CartItem[] {
    if (isPlatformBrowser(this.platformId)) {
      const json = localStorage.getItem(CART_KEY);
      return json ? JSON.parse(json) : [];
    }
    return [];
  }

  private persist() {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem(CART_KEY, JSON.stringify(this._items()));
    }
  }
}