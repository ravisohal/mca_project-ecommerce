import { ErrorHandler, Injectable } from '@angular/core';
import { NotificationService } from './services/notification';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  constructor(private notify: NotificationService) {}
  handleError(error: unknown): void {
    console.error('[GlobalErrorHandler]', error);
    this.notify.error('An unexpected error occurred.');
  }
}
