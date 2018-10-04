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

import { Component, OnInit, Input, Output, EventEmitter, OnDestroy } from '@angular/core';

import {Subscription} from 'rxjs/Subscription';

import {FilterManagementService} from '../services/filter-management.service';
import {AssetGroupObservableService} from '../../core/services/asset-group-observable.service';
import {DomainTypeObservableService} from '../../core/services/domain-type-observable.service';
import {UtilsService} from '../services/utils.service';
import {LoggerService} from '../services/logger.service';

import * as _ from 'lodash';

@Component({
  selector: 'app-generic-page-filter',
  templateUrl: './generic-page-filter.component.html',
  styleUrls: ['./generic-page-filter.component.css']
})
export class GenericPageFilterComponent implements OnInit, OnDestroy {

  filterSubscription: Subscription;
  assetGroupSubscription: Subscription;
  domainSubscription: Subscription;

  @Input() filterId;
  @Input() filterArray;
  @Output() onFilterValueChange = new EventEmitter();
  agAndDomain = {};

  filterValues = {
    filterTypeLabels: [], // contains all filter labels available to show user
    filterTypeOptions: [], // contians filter id, url to get values corresponsing to that filter
    filterValuesLabels: [], // contains values of a particular filter which is selected
    filterValuesOptions: [], // contains filter vaules id and name
    currentSelectedFilterType: {},
    currentSelectedFilterValue: {},
  };

  constructor(private assetGroupObservableService: AssetGroupObservableService,
              private domainObservableService: DomainTypeObservableService,
              private utils: UtilsService,
              private logger: LoggerService,
              private filterManagementService: FilterManagementService) {

    this.subscribeToAssetGroup();
    this.subscribeToDomain();

  }

  ngOnInit() {
    this.reset();
    this.init();
  }

  subscribeToAssetGroup() {
    this.assetGroupSubscription = this.assetGroupObservableService.getAssetGroup().subscribe(assetGroup => {
      if (assetGroup) {
        this.agAndDomain['ag'] = assetGroup;
      }
    });
  }

  subscribeToDomain() {
    this.domainSubscription = this.domainObservableService.getDomainType().subscribe(domain => {
      if (domain) {
        this.agAndDomain['domain'] = domain;
      }

      this.reset();
      this.init();
    });
  }

  reset() {
    /* Reset the values */

  }

  init() {
    this.getFilters();
  }

  getFilters() {
    try {
      if (this.filterId) {
        this.filterSubscription = this.filterManagementService.getApplicableFilters(this.filterId)
            .subscribe(response => {
              this.filterValues['filterTypeLabels'] = _.map(response[0].response, 'optionName');
              this.filterValues['filterTypeOptions'] = response[0].response;
            });
      }
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  changeFilterType(value) {
    try {
      this.filterValues.currentSelectedFilterType = _.find(this.filterValues.filterTypeOptions, {
        optionName: value.id
      });

      const queryParams = {
        ag: this.agAndDomain['ag'],
        domain: this.agAndDomain['domain']
      };

      this.filterSubscription = this.filterManagementService.getValuesForFilterType(this.filterValues.currentSelectedFilterType, queryParams).subscribe(response => {
        this.filterValues.filterValuesLabels = _.map(response[0].response, 'name');
        this.filterValues.filterValuesOptions = response[0].response;
      });
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  changeFilterValue(value) {
    try {
      if (this.filterValues.currentSelectedFilterType) {
        const filterTag = _.find(this.filterValues.filterValuesOptions, { name: value.id });
        this.utils.addOrReplaceElement(
            this.filterArray,
            {
              key: this.filterValues.currentSelectedFilterType['optionName'],
              value: filterTag.id,
              filterkey: this.filterValues.currentSelectedFilterType['optionValue'].trim(),
              compareKey : this.filterValues.currentSelectedFilterType['optionValue'].toLowerCase().trim()
            },
            el => {
              return el.compareKey === this.filterValues.currentSelectedFilterType['optionValue'].toLowerCase().trim();
            }
        );
        this.filterValues.filterValuesOptions = [];
        this.filterValues.filterValuesLabels = [];
        this.filterValues.currentSelectedFilterType = {};
      }
      /* Emit value to parent to notify change */
      this.onFilterValueChange.emit(this.filterArray);
      this.utils.clickClearDropdown();
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  ngOnDestroy() {
    try {
      if (this.assetGroupSubscription) {
        this.assetGroupSubscription.unsubscribe();
      }
      if (this.filterSubscription) {
        this.filterSubscription.unsubscribe();
      }
    } catch (error) {
      this.logger.log('error', 'js error - ' + error);
    }
  }
}
