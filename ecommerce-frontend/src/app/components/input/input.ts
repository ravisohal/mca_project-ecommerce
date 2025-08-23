import { Component, ChangeDetectionStrategy, input, signal } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { sign } from 'crypto';

@Component({
  selector: 'app-input',
  standalone: true,
  imports: [],
  templateUrl: './input.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: InputComponent,
      multi: true
    }
  ],
  styleUrl: './input.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})

export class InputComponent implements ControlValueAccessor {
  label = input<string>('');
  type = input<('text' | 'password' | 'email')>('text');
  placeholder = input<string>('');
  disabled = signal<boolean>(false);

  value = '';
  onChange = (value: string) => {};
  onTouched = () => {};

  writeValue(value: string): void {
    this.value = value || '';
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
      this.disabled.set(isDisabled);
  }
}