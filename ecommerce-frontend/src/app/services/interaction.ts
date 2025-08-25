import { inject, Injectable } from '@angular/core';
import { ApiService } from './api';
import { AuthService } from './auth';
import { InteractionEvent } from '../models/interaction-event';
import { InteractionType } from '../models/interaction-type';

@Injectable({ providedIn: 'root' })
export class InteractionService {
  private apiService = inject(ApiService);
  private authService = inject(AuthService);
  private url = '/interactions';

  /**
   * Log an interaction.
   *
   * Accepts either:
   *  - an InteractionEvent object: log({ integrationType: 'VIEW', productId: 1 })
   *
   * This normalizes the payload and sends a fire-and-forget POST to the backend.
   */
  log(integrationType: InteractionType, productId: number) {
    if (this.authService.isAuthenticated()) {
      const userId = this.authService.user()?.id || 0;
      const event: InteractionEvent = {
        userId,
        interactionType: integrationType,
        productId,
        timestamp: new Date()
      };

      this.apiService.post<void>(`${this.url}/log`, { ...event }).subscribe({ error: () => { /* swallow errors */ } });
    }
  }
  

}
