import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../services/notification';

@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification.html',
  styleUrl: './notification.scss'
})
export class NotificationComponent {
  private readonly notificationService = inject(NotificationService);
  notifications = this.notificationService.notifications;
  
  dismiss(notification: any) {
    this.notificationService.dismiss(notification);
  }
}
