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

import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HelpObservableService } from '../../post-login-app/common/services/help-observable.service';
import { Subscription } from 'rxjs/Subscription';
import { environment } from '../../../environments/environment';
import { LoggerService } from '../services/logger.service';

@Component({
  selector: 'app-help-text',
  templateUrl: './help-text.component.html',
  styleUrls: ['./help-text.component.css']
})
export class HelpTextComponent implements OnInit, OnDestroy {
  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private helpService: HelpObservableService,
    private logger: LoggerService
  ) {}

  questionAns: any = [];
  relatedArr: any = [];
  slideNum = 0;
  errorVal = 0;
  fullQueryParams: {};

  private helpSubscription: Subscription;

  ngOnInit() {
    try {
      this.fullQueryParams = this.activatedRoute.snapshot.queryParams;
      if (!this.fullQueryParams['widgetId']) {
        this.errorVal = -1;
      } else {
        const queryParams = {
          widgetId: this.fullQueryParams['widgetId']
        };

        const helpRequest = {};

        const helpUrl = environment.help.url;
        const helpMethod = environment.help.method;

        this.helpSubscription = this.helpService
          .getData(queryParams, helpUrl, helpMethod, helpRequest)
          .subscribe(
            response => {
              try {
                this.errorVal = 1;
                const obj = response.response;

                this.questionAns = [];
                this.relatedArr = [];

                this.questionAns = obj.faq;
                this.relatedArr = obj.releventFaq;
              } catch (error) {
                this.errorVal = -1;
              }
            },
            error => {
              this.errorVal = -1;
            }
          );
      }
    } catch (error) {
      this.errorVal = -1;
    }
  }

  prevClicked() {
    if (this.slideNum > 0) {
      this.slideNum--;
    } else {
      this.slideNum = this.relatedArr.length - 1;
    }
  }

  nextClicked() {
    if (this.slideNum < this.relatedArr.length - 1) {
      this.slideNum++;
    } else {
      this.slideNum = 0;
    }
  }

  closeModal() {
    let updatedQueryParams = {};
    updatedQueryParams = JSON.parse(JSON.stringify(this.fullQueryParams));
    updatedQueryParams['widgetId'] = undefined;

    this.router.navigate(
      [
        {
          outlets: {
            helpTextModal: null
          }
        }
      ],
      {
        relativeTo: this.activatedRoute.parent,
        queryParams: updatedQueryParams
      }
    );
  }

  ngOnDestroy() {
    try {
      if (this.helpSubscription) {
        this.helpSubscription.unsubscribe();
      }
    } catch (error) {
      this.logger.log('error', 'js error - ' + error);
    }
  }
}
