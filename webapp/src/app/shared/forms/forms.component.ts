import { Component, OnInit, Input, forwardRef, Output, EventEmitter, } from '@angular/core';
import { FormGroup , FormControl, Validators, NG_VALUE_ACCESSOR, ControlValueAccessor} from '@angular/forms';

export const CUSTOM_INPUT_CONTROL_VALUE_ACCESSOR: any = {
  provide: NG_VALUE_ACCESSOR,
  useExisting: forwardRef(() => FormsComponent),
  multi: true
};

@Component({
  selector: 'app-forms',
  templateUrl: './forms.component.html',
  styleUrls: ['./forms.component.css'],
  providers: [CUSTOM_INPUT_CONTROL_VALUE_ACCESSOR]
})
export class FormsComponent implements OnInit, ControlValueAccessor  {

  // Input field type eg:text,password

  @Input() inputType = 'input'; // input | desc

  @Input()  type = 'text';

  // ID attribute for the field and for attribute for the label
  @Input()  id = '';
  // placeholder input
  @Input()  placeHolderValue = '';

  @Input() inputLabel;
  // current form control input. helpful in validating and accessing form control
  @Input() formControlName = new FormControl();

  // Form errors will be passed here
  @Input() formErrors;

  @Input() parentForm;

  @Input() _value  = '';

  // metadata for the field
  @Input() metadata;
  onChange: any = () => { };
  onTouched: any = () => { };


  constructor() { }

  ngOnInit() {
  }

  get value() {
    return this._value;
  }

  set value(val) {
    this._value = val;
    this.onChange(val);
    this.onTouched(val);
  }

  writeValue(value): void {
    if (value) {
      this.value = value;
    }
  }
  registerOnChange(fn): void {
    this.onChange = fn;
  }
  registerOnTouched(fn) {
    this.onTouched = fn;
  }
  setDisabledState?(isDisabled): void {
    throw new Error('Method not implemented.');
  }
}
