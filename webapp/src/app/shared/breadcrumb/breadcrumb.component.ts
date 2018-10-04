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

import { Component, OnInit, Input, OnDestroy} from '@angular/core';
import { AssetGroupObservableService } from '../../core/services/asset-group-observable.service';
import { Subscription } from 'rxjs/Subscription';
import { Router } from '@angular/router';
import {WorkflowService} from '../../core/services/workflow.service';
import {DomainTypeObservableService} from '../../core/services/domain-type-observable.service';
import {LoggerService} from '../services/logger.service';

@Component({
  selector: 'app-breadcrumb',
  templateUrl: './breadcrumb.component.html',
  styleUrls: ['./breadcrumb.component.css']
})
export class BreadcrumbComponent implements OnInit, OnDestroy {

  @Input() breadcrumbArray: any;
  @Input() breadcrumbLinks: any;
  @Input() breadcrumbPresent: any;
  @Input() asset: any;
  @Input() isCustomParentRoute: boolean;
  @Input() parentRouteName: any;

  private assetGroupSubscription: Subscription;
  private domainSubscription: Subscription;
  private agAndDomain = {};

  constructor(private assetGroupObservableService: AssetGroupObservableService,
              private router: Router,
              private workflowService: WorkflowService,
              private domainObservableService: DomainTypeObservableService,
              private logger: LoggerService) {


  }

  ngOnInit() {
    this.initializeSubscriptions();
  }

  initializeSubscriptions() {
    this.assetGroupSubscription = this.assetGroupObservableService.getAssetGroup().subscribe(assetGroupName => {
      this.agAndDomain['ag'] = assetGroupName;
    });
    this.domainSubscription = this.domainObservableService.getDomainType().subscribe(domain => {
      this.agAndDomain['domain'] = domain;
    });
  }

  ngOnDestroy() {
      try {
        if (this.assetGroupSubscription) {
          this.assetGroupSubscription.unsubscribe();
        }
        if (this.domainSubscription) {
          this.domainSubscription.unsubscribe();
        }
      }catch (error) {
        this.logger.log('error', error);
      }
  }

  navigateRespective(index): any {
      if (this.asset === true) {
        this.router.navigate(['/pl/assets', this.breadcrumbLinks[index]], {queryParams: this.agAndDomain});
      } else if (this.isCustomParentRoute === true && this.parentRouteName !== undefined) {
        this.router.navigate(['/pl/' + this.parentRouteName, this.breadcrumbLinks[index]], {queryParams: this.agAndDomain});
      } else {
        this.router.navigate(['/pl/compliance', this.breadcrumbLinks[index]], {queryParams: this.agAndDomain});
      }
    /**
     * added by Trinanjan on 09/02/2018 for back button functionality
     * To clear page levels
     */
      this.workflowService.clearAllLevels();
  }

}
