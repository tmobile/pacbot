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

import { Component, OnInit, Input } from '@angular/core';
import { WorkflowService } from '../../core/services/workflow.service';
import { Router } from '@angular/router';
import { LoggerService } from '../../shared/services/logger.service';
import { RouterUtilityService } from '../../shared/services/router-utility.service';

@Component({
  selector: 'app-back-navigation',
  templateUrl: './back-navigation.component.html',
  styleUrls: ['./back-navigation.component.css']
})
export class BackNavigationComponent implements OnInit {

  public backButtonRequired;
  @Input() pageTitle?;

  constructor(
    private workflowService: WorkflowService,
    private router: Router,
    private logger: LoggerService,
    private routerUtilityService: RouterUtilityService
  ) {
    this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently();
  }

  ngOnInit() {
    if (!this.pageTitle) {
      this.pageTitle = this.routerUtilityService.getpageTitle(this.router.routerState.snapshot.root);
    }
  }

  navigateBack() {
    try {
      this.workflowService.goBackToLastOpenedPageAndUpdateLevel(this.router.routerState.snapshot.root);
    } catch (error) {
      this.logger.log('error', error);
    }
  }

}
