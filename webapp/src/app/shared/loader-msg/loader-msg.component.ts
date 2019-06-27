import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-loader-msg',
  templateUrl: './loader-msg.component.html',
  styleUrls: ['./loader-msg.component.css']
})
export class LoaderMsgComponent implements OnInit {
  @Input() response;
  @Output() actionClick = new EventEmitter();
  @Input() ifCancelRequired = true;

  constructor(private router: Router) { }

  ngOnInit() {
  }

  takeAction(clickType, $event) {
    this.actionClick.emit(clickType);
  }
}
