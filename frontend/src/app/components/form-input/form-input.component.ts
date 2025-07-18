import { CommonModule } from '@angular/common';
import {
  Component,
  forwardRef,
  Input,
  OnInit
} from '@angular/core';
import {
  ControlValueAccessor,
  NG_VALUE_ACCESSOR,
  ReactiveFormsModule
} from '@angular/forms';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';

export type InputTypes = 'text' | 'email' | 'password' | 'date' | 'tel' | 'number';

@Component({
  selector: 'app-form-input',
  templateUrl: './form-input.component.html',
  styleUrls: ['./form-input.component.scss'],
  standalone: true,
  imports: [
    ReactiveFormsModule,
     CommonModule,   
    NgxMaskDirective
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => FormInputComponent),
      multi: true
    },
    provideNgxMask()
  ]
})
export class FormInputComponent implements ControlValueAccessor {
  @Input() type: InputTypes = 'text';
  @Input() placeholder: string = '';
  @Input() label: string = '';
  @Input() inputName: string = '';
  @Input() mask: string = '';
  @Input() errorMessage: string = '';


  value: string = '';
  onChange: any = () => {};
  onTouched: any = () => {};
  isDisabled = false;

  customPatterns = { '0': { pattern: /\d/ } };

  onInput(event: Event) {
    const inputValue = (event.target as HTMLInputElement).value;
    this.value = inputValue;
    this.onChange(inputValue);
  }

  writeValue(value: any): void {
    this.value = value ?? '';
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.isDisabled = isDisabled;
  }
}
