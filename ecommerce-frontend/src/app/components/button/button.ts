import { Component, ChangeDetectionStrategy, input } from '@angular/core';

@Component({
  selector: 'app-button',
  standalone: true,
  imports: [],
  templateUrl: './button.html',
  styleUrl: './button.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})

export class ButtonComponent {
  variant = input<'primary' | 'secondary'>('primary');
  disabled = input<boolean>(false);
}
