/*
 *Copyright 2018 T Mobile, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not use
 * this file except in compliance with the License. A copy of the License is located at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or
 * implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { ToastObservableService } from '../services/toast-observable.service';

@Component({
  selector: 'app-toast-notification',
  templateUrl: './toast-notification.component.html',
  styleUrls: ['./toast-notification.component.css']
})
export class ToastNotificationComponent implements OnInit, OnDestroy {
  constructor(private toastObservableService: ToastObservableService) { }

  @Input() toastState = 0;
  @Input() toastMsg = '';
  toastHeading;
  toastImg;
  toastSubscription: Subscription;
  private timeoutCall;

  ngOnInit() {
    this.toastSubscription = this.toastObservableService
      .getMessage()
      .subscribe(obj => {
        clearTimeout(this.timeoutCall);
        this.toastImg = obj.image;
        this.toastMsg = obj.msg;
        this.toastHeading = obj.category;
        this.toastState = 1;
        const x = this;

        if (!obj.duration || obj.duration < 3 || isNaN(obj.duration)) {
          this.timeoutCall = setTimeout(function () {
            x.toastState = 0;
            x.toastHeading = undefined;
            x.toastMsg = undefined;
            x.toastImg = undefined;
          }, 3000);
        } else {
          this.timeoutCall = setTimeout(function () {
            x.toastState = 0;
            x.toastHeading = undefined;
            x.toastMsg = undefined;
          }, obj.duration * 1000);
        }
      });
  }

  ngOnDestroy() {
    if (this.toastSubscription) {
      this.toastSubscription.unsubscribe();
    }
  }
}
