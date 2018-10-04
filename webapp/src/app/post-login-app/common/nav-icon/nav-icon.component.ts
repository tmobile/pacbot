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

import {Component, Input, OnInit, OnDestroy } from '@angular/core';
import {AssetGroupObservableService} from '../../../core/services/asset-group-observable.service';
import {DomainTypeObservableService} from '../../../core/services/domain-type-observable.service';
import {Subscription} from 'rxjs/Subscription';
import { LoggerService } from '../../../shared/services/logger.service';
import { RoutingService } from '../../../core/services/routing.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-nav-icon',
  templateUrl: './nav-icon.component.html',
  styleUrls: ['./nav-icon.component.css']
})
export class NavIconComponent implements OnInit, OnDestroy {

  constructor(private assetGroupObservableService: AssetGroupObservableService,
              private domainObservableService: DomainTypeObservableService,
              private logger: LoggerService,
              private routingService: RoutingService,
              public router: Router) { }

  @Input() iconImage: string;
  @Input() linkToRoute: string;
  @Input() moduleName: string;
  @Input() activeIconImage: string;

  private assetGroupSubscription: Subscription;
  private domainSubscription: Subscription;
  public agAndDomain = {};

  ngOnInit() {
    this.subscribeToAgAndDomainChange();
  }

  subscribeToAgAndDomainChange() {
    try {
      this.assetGroupSubscription = this.assetGroupObservableService.getAssetGroup().subscribe(assetGroup => {
        this.agAndDomain['ag'] = assetGroup;
      });
      this.domainSubscription = this.domainObservableService.getDomainType().subscribe(domain => {
        this.agAndDomain['domain'] = domain;
      });
    } catch (error) {
      this.logger.log('error', 'js error - ' + error);
    }
  }

  navigateToModule(moduleName, agAndDomain) {
    try {
      this.routingService.redirectToRespectiveLatestPath(moduleName, agAndDomain);
    } catch (error) {
      this.logger.log('error', 'js error - ' + error);
    }
  }

  ngOnDestroy() {
    this.assetGroupSubscription.unsubscribe();
    this.domainSubscription.unsubscribe();
  }

}
