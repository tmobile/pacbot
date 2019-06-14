import { Component, OnInit, Output, Input, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-confirmation-box',
  templateUrl: './confirmation-box.component.html',
  styleUrls: ['./confirmation-box.component.css']
})
export class ConfirmationBoxComponent implements OnInit, OnChanges {

  constructor() { }

  @Output() emitClose = new EventEmitter();
  @Output() emitConfirm = new EventEmitter();
  @Output() emitTransactionClose = new EventEmitter();
  @Input() text;
  @Input() errorValue;
  @Input() transactionInProgress;
  @Input() transactionResponse;
  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    const transactionInProgressChanges = changes['transactionInProgress'];
    const transactionResponseChanges = changes ['transactionResponse'];
    const textChanges = changes['text'];

    if (transactionInProgressChanges) {
      console.log('transactionInProgressChanges - ' + transactionInProgressChanges);
      if (transactionInProgressChanges.currentValue !== transactionInProgressChanges.previousValue) {
        this.transactionInProgress = transactionInProgressChanges.currentValue;
      }
    }
    if (transactionResponseChanges) {
      console.log('transactionResponseChanges - ');
      console.log(transactionResponseChanges);
      if (transactionResponseChanges.currentValue !== transactionResponseChanges.previousValue) {
        this.transactionResponse = transactionResponseChanges.currentValue;
      }
    }
    if (textChanges) {
      console.log('transactionResponseChanges - ');
      console.log(textChanges);
      if (textChanges.currentValue !== textChanges.previousValue) {
        this.text = textChanges.currentValue;
      }
    }
  }

  closeBox() {
    this.emitClose.emit();
  }

  confirmBox() {
    this.emitConfirm.emit();
  }

  takeActionPostTransaction(clickType) {
    // Click type shows whether back is clicked or continue is clicked
    this.emitTransactionClose.emit();
  }

}
