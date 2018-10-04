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

import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import {WorkflowService} from '../../core/services/workflow.service';
import {ThemeObservableService} from '../../core/services/theme-observable.service';
import {DomainTypeObservableService} from '../../core/services/domain-type-observable.service';
import {AssetGroupObservableService} from '../../core/services/asset-group-observable.service';
import {Subscription} from 'rxjs/Subscription';
import { LoggerService } from '../services/logger.service';

@Component({
  selector: 'app-contextual-menu',
  templateUrl: './contextual-menu.component.html',
  styleUrls: ['./contextual-menu.component.css'],
  providers: []
})
export class ContextualMenuComponent implements OnInit, OnDestroy {

  constructor(
              private workflowService: WorkflowService,
              private themeObservableService: ThemeObservableService,
              private assetGroupObservableService: AssetGroupObservableService,
              private domainObservableService: DomainTypeObservableService,
              private logger: LoggerService) { }

  @Input() pageTitle;
  @Input() listOfMenus;
  private assetGroupSubscription: Subscription;
  private domainSubscription: Subscription;
  public agAndDomain = {};
  public theme: any;

  selectedMenu;

  ngOnInit() {
    this.subscribeForThemeChange();
    this.subscribeToAgAndDomainChange();
  }

  subscribeForThemeChange() {
    this.themeObservableService
        .getTheme()
        .subscribe((theme) => {
            this.theme = theme;
        });
  }

  onSelect(menu): void {
    this.selectedMenu = menu;
    /**
     * added by Trinanjan on 09/02/2018 for back button functionality
     * To clear page levels
     */
    this.workflowService.clearAllLevels();
  }

  subscribeToAgAndDomainChange() {
    this.assetGroupSubscription = this.assetGroupObservableService.getAssetGroup().subscribe(assetGroup => {
      this.agAndDomain['ag'] = assetGroup;
    });
    this.domainSubscription = this.domainObservableService.getDomainType().subscribe(domain => {
      this.agAndDomain['domain'] = domain;
    });
  }

  ngOnDestroy() {
    try {
      this.assetGroupSubscription.unsubscribe();
      this.domainSubscription.unsubscribe();
    } catch (error) {
      this.logger.log('error', 'js error - ' + error);
    }
  }

}
