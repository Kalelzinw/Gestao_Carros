import { Component, forwardRef, Input } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';

export type InputTypes = 'text' | 'email' | 'password' | 'date' | 'tel' | 'number';

@Component({
  selector: 'app-primary-input',
  standalone: true,
  templateUrl: './primary-input.component.html',
  styleUrls: ['./primary-input.component.scss'],
  imports: [
    NgxMaskDirective 
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => PrimaryInputComponent),
      multi: true
    },
    provideNgxMask() 
  ]
})
export class PrimaryInputComponent implements ControlValueAccessor {
  @Input() type: InputTypes = 'text';
  
  @Input() placeholder: string = '';
  @Input() label: string = '';
  @Input() inputName: string = '';
  @Input() mask: string = '';
   customPatterns = { '0': { pattern: /\d/ } };
   get inputId(): string {
  return this.inputName || 'input-' + this.type;
}

  value: string = '';
  onChange: any = () => {};
  onTouched: any = () => {};

  onInput(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.onChange(value);
  }

  writeValue(value: any): void {
    this.value = value;
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }
  

  setDisabledState(isDisabled: boolean): void {
 
  }
}
