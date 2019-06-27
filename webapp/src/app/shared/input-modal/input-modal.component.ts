import { Component, OnInit, Output, Input, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-input-modal',
  templateUrl: './input-modal.component.html',
  styleUrls: ['./input-modal.component.css']
})
export class InputModalComponent implements OnInit {

  constructor() { }

  @Output() emitClose = new EventEmitter();
  @Output() emitSave = new EventEmitter();
  @Input() dropdownArray;
  @Input() errorValue;
  @Input() errorMessage;
  createObject = {};
  key: any;
  value: any;

  ngOnInit() {
  }

  cancel() {
    this.emitClose.emit();
  }

  save() {
    this.createObject[this.key] = this.value;
    this.emitSave.emit(this.createObject);
  }

  selected(key) {
    this.key = key.value;
  }
}
