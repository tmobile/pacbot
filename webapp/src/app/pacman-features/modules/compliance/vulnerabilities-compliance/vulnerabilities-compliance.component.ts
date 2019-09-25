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

import {
  Component,
  OnInit,
  ElementRef,
  ViewChild,
  OnDestroy
} from '@angular/core';
import { AssetGroupObservableService } from '../../../../core/services/asset-group-observable.service';
import { SelectComplianceDropdown } from './../../../services/select-compliance-dropdown.service';
import { Subscription } from 'rxjs';
import { environment } from './../../../../../environments/environment';
import { Router } from '@angular/router';
import { IssueFilterService } from './../../../services/issue-filter.service';
import * as _ from 'lodash';
import { UtilsService } from '../../../../shared/services/utils.service';
import { LoggerService } from '../../../../shared/services/logger.service';
import { WorkflowService } from '../../../../core/services/workflow.service';

@Component({
  selector: 'app-vulnerabilities-compliance',
  templateUrl: './vulnerabilities-compliance.component.html',
  styleUrls: ['./vulnerabilities-compliance.component.css'],
  providers: [
    IssueFilterService,
    LoggerService
  ]
})
export class VulnerabilitiesComplianceComponent implements OnInit, OnDestroy {
  @ViewChild('widget') widgetContainer: ElementRef;

  pageTitle = 'Vulnerabilities';
  widgetWidth: number;
  widgetHeight: number;

  /*variables for breadcrumb data*/

  breadcrumbArray: any = ['Compliance'];
  breadcrumbLinks: any = ['compliance-dashboard'];
  breadcrumbPresent: any;

  complianceDropdowns: any = [];
  searchDropdownData: any = {};
  selectedDD = '';
  currentObj: any = {};
  subscriptionToAssetGroup: Subscription;
  selectedAssetGroup: string;
  selectedComplianceDropdown: any;
  filterTypeOptions = [];
  filterTypeLabels = [];
  currentFilterType;
  filterTagOptions = [];
  filterTagLabels = [];
  filters = [];
  private filterTypesSubscription: Subscription;

  constructor(
    private assetGroupObservableService: AssetGroupObservableService,
    private selectComplianceDropdown: SelectComplianceDropdown,
    private router: Router,
    private utils: UtilsService,
    private issueFilterService: IssueFilterService,
    private logger: LoggerService,
    private workflowService: WorkflowService
  ) {
    this.subscriptionToAssetGroup = this.assetGroupObservableService
      .getAssetGroup()
      .subscribe(assetGroupName => {
        this.selectedAssetGroup = assetGroupName;
        this.deleteFilters();
        this.getFilters();
      });
  }

  ngOnInit() {
    try {
      this.breadcrumbPresent = 'Vulnerabilities Compliance';
      this.widgetWidth = parseInt(
        window
          .getComputedStyle(this.widgetContainer.nativeElement, null)
          .getPropertyValue('width'),
        10
      );
      // this.workflowService.clearAllLevels();
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  getFilters() {
    const queryParams = {
      filterId: 2
    };
    const issueFilterUrl = environment.issueFilter.url;
    const issueFilterMethod = environment.issueFilter.method;
    this.filterTypesSubscription = this.issueFilterService
      .getFilters(queryParams, issueFilterUrl, issueFilterMethod)
      .subscribe(response => {
        this.filterTypeLabels = _.map(response[0].response, 'optionName');
        this.filterTypeOptions = response[0].response;
      });
  }

  changeFilterType(value) {
    this.currentFilterType = _.find(this.filterTypeOptions, {
      optionName: value.id
    });
    this.filterTypesSubscription = this.issueFilterService
      .getFilters(
        {
          ag: this.selectedAssetGroup
        },
        environment.base +
          this.utils.getParamsFromUrlSnippet(this.currentFilterType.optionURL)
            .url,
        'GET'
      )
      .subscribe(response => {
        this.filterTagOptions = response[0].response;
        this.filterTagLabels = _.map(response[0].response, 'name');
      });
  }

  changeFilterTags(value) {
    if (this.currentFilterType) {
      const filterTag = _.find(this.filterTagOptions, { name: value.id });
      this.utils.addOrReplaceElement(
        this.filters,
        {
          key: this.currentFilterType.optionName,
          value: filterTag.id,
          filterkey: this.currentFilterType.optionValue.trim(),
          compareKey: this.currentFilterType.optionValue.toLowerCase().trim()
        },
        el => {
          return (
            el.compareKey ===
            this.currentFilterType.optionValue.toLowerCase().trim()
          );
        }
      );
      this.selectComplianceDropdown.updateCompliance(
        this.utils.arrayToObject(this.filters, 'filterkey', 'value')
      );
      this.filterTagOptions = [];
      this.filterTagLabels = [];
      this.currentFilterType = null;
    }
    this.utils.clickClearDropdown();
  }

  deleteFilters(event?) {
    try {
      if (!event) {
        this.filters = [];
      } else {
        if (event.clearAll) {
          this.filters = [];
        } else {
          this.filters.splice(event.index, 1);
        }
        this.selectComplianceDropdown.updateCompliance(
          this.utils.arrayToObject(this.filters, 'filterkey', 'value')
        );
      }
    } catch (error) {
      this.logger.log('error', error);
    }
  }

  ngOnDestroy() {
    try {
      if (this.filterTypesSubscription) {
        this.filterTypesSubscription.unsubscribe();
      }
      if (this.subscriptionToAssetGroup) {
        this.subscriptionToAssetGroup.unsubscribe();
      }
    } catch (error) {
      this.logger.log('error', '--- Error while unsubscribing ---');
    }
  }
}
