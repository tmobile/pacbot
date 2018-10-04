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
import {DomainTypeObservableService} from '../../../core/services/domain-type-observable.service';
import {DomainMappingService} from '../../../core/services/domain-mapping.service';
import {LoggerService} from '../../../shared/services/logger.service';
import {Subscription} from 'rxjs/Subscription';


@Component({
  selector: 'app-compliance-issues',
  templateUrl: './compliance-issues.component.html',
  styleUrls: ['./compliance-issues.component.css'],
  providers: []
})

export class ComplianceIssuesComponent implements OnInit, OnDestroy {

  public pageLevel = 0;
  public selectedAssetGroup;

  private subscriptionToDomainType: Subscription;
  private selectedDomainName = '';
  private listOfContextualMenuItems;
  public tileMapping;

  constructor(private domainTypeObservableService: DomainTypeObservableService,
              private domainMappingService: DomainMappingService,
              private logger: LoggerService) {

    this.initialiseSubscriptions();

  }

  ngOnInit() {
    try {
      this.tileMapping = {};
      this.getMenuListOnDomainUpdate();
    } catch (error) {
      this.logger.log('error', 'JS error - ' + error);
    }
  }

  initialiseSubscriptions() {
    try {

      this.subscriptionToDomainType = this.domainTypeObservableService
          .getDomainType()
          .subscribe(DomainName => {
            if (DomainName) {
              this.selectedDomainName = DomainName;
              this.resetTilesMapping();
              this.getMenuListOnDomainUpdate();
            }
          });
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  getMenuListOnDomainUpdate() {
    try {
      const moduleName = 'compliance';
      const domainName = this.selectedDomainName || '';
      this.listOfContextualMenuItems = this.domainMappingService.getDashboardsApplicableForADomain(domainName, moduleName);
      this.updateTileMapping(this.listOfContextualMenuItems);
    } catch (error) {
      this.logger.log('JS Error ', error);
    }
  }

  updateTileMapping(menuItems) {

    menuItems.forEach(eachItem => {
      if (eachItem && eachItem.data && eachItem.data.tileName) {
        this.tileMapping[eachItem.data.tileName] = true;
      }
    });
  }

  resetTilesMapping() {
    this.tileMapping = {};
  }

  ngOnDestroy() {
    this.subscriptionToDomainType.unsubscribe();
  }
}
