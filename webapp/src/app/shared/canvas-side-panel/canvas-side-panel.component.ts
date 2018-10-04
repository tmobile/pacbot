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

import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import {Subscription} from 'rxjs/Subscription';
import {AssetGroupObservableService} from '../../core/services/asset-group-observable.service';
import {DomainTypeObservableService} from '../../core/services/domain-type-observable.service';
import { RoutingService } from '../../core/services/routing.service';
import { LoggerService } from '../services/logger.service';

@Component({
  selector: 'app-canvas-side-panel',
  templateUrl: './canvas-side-panel.component.html',
  styleUrls: ['./canvas-side-panel.component.css']
})
export class CanvasSidePanelComponent implements OnInit {

  constructor(private assetGroupObservableService: AssetGroupObservableService,
              private domainObservableService: DomainTypeObservableService,
              private routingService: RoutingService,
              private logger: LoggerService) { }

  @Input() rightPointing: boolean;
  @Input() link: string;
  @Output() navigating: EventEmitter<any> = new EventEmitter();
  @Input() displayProperty = true;
  @Input() moduleName: string;


  private assetGroupSubscription: Subscription;
  private domainSubscription: Subscription;
  public agAndDomain = {};

  ngOnInit() {
    this.link = '../' + this.link;
    this.subscribeToAgAndDomainChange();
  }

  navigateToModule(moduleName, agAndDomain) {
    this.routingService.redirectToRespectiveLatestPath(moduleName, agAndDomain);
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

}
