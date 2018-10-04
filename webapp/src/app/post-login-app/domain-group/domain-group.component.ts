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

import { Component, OnInit } from '@angular/core';
import { LoggerService } from '../../shared/services/logger.service';
import { DataCacheService } from '../../core/services/data-cache.service';
import { DomainTypeObservableService } from '../../core/services/domain-type-observable.service';
import { ThemeObservableService } from '../../core/services/theme-observable.service';
import { DomainMappingService } from '../../core/services/domain-mapping.service';
import { ActivatedRoute, NavigationExtras, Router } from '@angular/router';
import { RouterUtilityService } from '../../shared/services/router-utility.service';
import {WorkflowService} from '../../core/services/workflow.service';

@Component({
  selector: 'app-domain-group',
  templateUrl: './domain-group.component.html',
  styleUrls: ['./domain-group.component.css']
})

export class DomainGroupComponent implements OnInit {
  /*
   * This component consists of the domain selector box and the doamin dropdown
   */

  showUserInfo = false;
  selectedDomain: string;
  DomainListString: string;

  constructor(private dataStore: DataCacheService,
              private domainTypeObservableService: DomainTypeObservableService,
              private logger: LoggerService,
              private themeObservableService: ThemeObservableService,
              private domainMappingService: DomainMappingService,
              private router: Router,
              private activatedRoute: ActivatedRoute,
              private routingUtilityService: RouterUtilityService,
              private workflowService: WorkflowService ) {}

  ngOnInit() {
    try {
      this.subscribeToDomainListUpdate();
      this.subscribeToCurrentSelectedDomainUpdate();
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  /*
   *This function closes the dropdown onclick of inside the dropdown and outside anywhere
   */

  closeUserInfo() {
    try {
      setTimeout(() => {
        this.showUserInfo = false;
      }, 5);
    } catch (error) {

    }
  }

  updateDropdownSelection(updatedDomainValue) {

    const currentAssetGroup = this.dataStore.getCurrentSelectedAssetGroup();
    const currentDomain = this.dataStore.getCurrentSelectedDomain(currentAssetGroup);
    if (updatedDomainValue !== currentDomain && currentDomain && currentDomain !== 'undefined') {
      const navigationExtras: NavigationExtras = {
        queryParams: {
          'domain': updatedDomainValue
        },
        queryParamsHandling: 'merge'
      };

      const currentModule = this.routingUtilityService.getModuleNameFromCurrentRoute(this.activatedRoute.snapshot);

      const landingPageUrl = this.routingUtilityService.getLandingPageInAModule(currentModule);

      /* Clears the saved url from module as different module may not have that page */
      this.workflowService.clearDataOfOpenedPageInModule();
      this.router.navigate([landingPageUrl], navigationExtras);
    }
  }

  subscribeToDomainListUpdate() {
    try {

      this.domainTypeObservableService.getDomainListForAAssetGroup().subscribe((domainList) => {
        this.DomainListString = domainList;
      });

    } catch (error) {
      this.logger.log('error', 'JS Error' + error);
    }
  }

  subscribeToCurrentSelectedDomainUpdate() {
    try {

      this.domainTypeObservableService.getDomainType().subscribe((domainName) => {
        this.selectedDomain = domainName;
        const theme = this.domainMappingService.getThemeForADomain(domainName);
        this.updateThemeSubscription(theme);
      });

    } catch (error) {
      this.logger.log('error', 'JS Error' + error);
    }
  }

  updateThemeSubscription(theme) {
    this.themeObservableService.updateTheme(theme);
  }

}
