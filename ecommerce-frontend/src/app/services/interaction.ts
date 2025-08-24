import { ApiService } from './api';
import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

export type InteractionEvent = {
  type: 'VIEW' | 'CLICK' | 'ADD_TO_CART' | 'REMOVE_FROM_CART' | 'CHECKOUT_START' | 'CHECKOUT_COMPLETE';
  productId?: number;
  metadata?: Record<string, any>;
  at?: string;
};

@Injectable({ providedIn: 'root' })
export class InteractionService {
  private apiService = inject(ApiService);
  private url = '/interactions';

  /**
   * Log an interaction.
   *
   * Accepts either:
   *  - an InteractionEvent object: log({ type: 'VIEW', productId: 1 })
   *  - or (type, payload): log('VIEW', { productId: 1, metadata: { foo: 'bar' } })
   *
   * This normalizes the payload and sends a fire-and-forget POST to the backend.
   */
  log(eventOrType: InteractionEvent | string, payload?: Partial<InteractionEvent>) {
    let evt: InteractionEvent;

    if (typeof eventOrType === 'string') {
      const type = eventOrType as InteractionEvent['type'];
      evt = {
        type,
        productId: payload?.productId,
        metadata: payload?.metadata ?? {},
        at: payload?.at ?? new Date().toISOString()
      };
    } else {
      evt = { ...eventOrType, at: eventOrType.at ?? new Date().toISOString() };
    }

    this.apiService.post<void>(this.url, evt).subscribe({ error: () => { /* swallow errors */ } });
  }

}
