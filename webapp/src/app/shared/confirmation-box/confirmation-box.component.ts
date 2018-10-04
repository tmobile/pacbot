import { Component, OnInit, Output, Input, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-confirmation-box',
  templateUrl: './confirmation-box.component.html',
  styleUrls: ['./confirmation-box.component.css']
})
export class ConfirmationBoxComponent implements OnInit {

  constructor() { }

  @Output() emitClose = new EventEmitter();
  @Output() emitConfirm = new EventEmitter();
  @Input() text;
  @Input() errorValue;
  ngOnInit() {
  }

  closeBox() {
    this.emitClose.emit();
  }

  confirmBox() {
    this.emitConfirm.emit();
  }

}
