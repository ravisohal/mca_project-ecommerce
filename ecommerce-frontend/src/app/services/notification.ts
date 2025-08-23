import { Injectable, signal } from '@angular/core';

export type NotificationType = 'success' | 'error' | 'info' | 'warning';

export interface Notification {
  type: NotificationType;
  message: string;
  id: number;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private nextId = 1;
  notifications = signal<Notification[]>([]);

  // generic show
  show(notification: Omit<Notification, 'id'>) {
    const newNotification: Notification = {
      ...notification,
      id: this.nextId++
    };

    this.notifications.update((list) => [...list, newNotification]);

    // auto-dismiss after 3s
    setTimeout(() => this.dismiss(newNotification), 3000);
  }

  // shorthand helpers
  success(message: string) {
    this.show({ type: 'success', message });
  }

  error(message: string) {
    this.show({ type: 'error', message });
  }

  info(message: string) {
    this.show({ type: 'info', message });
  }

  warning(message: string) {
    this.show({ type: 'warning', message });
  }

  dismiss(notification: Notification) {
    this.notifications.update((list) =>
      list.filter((n) => n.id !== notification.id)
    );
  }
}
