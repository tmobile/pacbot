import { Component, OnInit, Input } from '@angular/core';
import { ToastObservableService } from '../services/toast-observable.service';
import {CopyElementService} from '../services/copy-element.service';


@Component({
  selector: 'app-copy-element',
  templateUrl: './copy-element.component.html',
  styleUrls: ['./copy-element.component.css'],
})
export class CopyElementComponent implements OnInit {


  constructor(private copyElementService: CopyElementService ,private toastObservableService: ToastObservableService) { }

  ngOnInit() {
  }

  @Input() CopyElement;
  @Input() iconSize;

  CopyTextToClipboard(text, event) {
    event.stopPropagation();
    var textArea = document.createElement("textarea");
    textArea.style.position = 'fixed';
    textArea.style.left = '0';
    textArea.style.top = '0';
    textArea.style.opacity = '0';
    textArea.value = text;
    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();
  
    try {
      if (text !== '' && text !== undefined ) {
          var successful = document.execCommand('copy');
          var msg = successful ? 'successful' : 'unsuccessful';
          if(msg === 'successful') {
            this.copyElementService.textCopyMessage(
              'Element has been copied ' , 2, 'Info' , 'Info.svg'
            );
          } else {
            this.copyElementService.textCopyMessage(
              'Element Copied failed' , 2, 'Info', 'Info.svg'
            );
          }
      } else {
          this.copyElementService.textCopyMessage(
            'No Data Available' , 2, 'Error', 'Error.svg'
          );
      }
     
    } catch (err) {
        this.copyElementService.textCopyMessage(
          'Copying failed! Please try later.' , 3 , 'Error' , 'Error.svg'
        );
    }
  
    document.body.removeChild(textArea);
  }
  
}
