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

import {Component, OnInit, OnDestroy, ElementRef, ViewChild, Input, OnChanges} from '@angular/core';
import {Subscription} from 'rxjs/Subscription';
import {AssetGroupObservableService} from '../../../core/services/asset-group-observable.service';
import {AutorefreshService} from '../../services/autorefresh.service';
import {SelectComplianceDropdown} from '../../services/select-compliance-dropdown.service';
import {MultilineChartService} from '../../services/multilinechart.service';
import {environment} from './../../../../environments/environment';
import {IssueFilterService} from './../../services/issue-filter.service';
import * as _ from 'lodash';
import {UtilsService} from '../../../shared/services/utils.service';
import { DomainTypeObservableService } from '../../../core/services/domain-type-observable.service';

@Component({
    selector: 'app-inventory-container',
    templateUrl: './inventory-container.component.html',
    styleUrls: ['./inventory-container.component.css'],
    providers: [MultilineChartService, AutorefreshService, IssueFilterService],
    // tslint:disable-next-line:use-host-property-decorator
    host: {
        '(window:resize)': 'onResize($event)'
    }
})

export class InventoryContainerComponent implements OnInit, OnChanges, OnDestroy {

    @ViewChild('widget') widgetContainer: ElementRef;
    @Input() targetType: any;

    widgetWidth: number;
    widgetHeight: number;

    selectedAssetGroup: string;
    errorMessages;
    durationParams: any;
    autoRefresh: boolean;

    complianceDropdowns: any = ['Applications'];
    searchDropdownData: any = {};
    selectedDD = '';
    currentObj: any = {};
    filterArr: any = [];
    selectedComplianceDropdown: any = {
        'Applications': ''
    };

    private error = false;
    private dataLoaded = false;
    graphData: any;
    colorSet: any = [];
    errorMessage: any;
    showerror = false;
    showloader = false;
    showdata = false;
    filterTypeOptions = [];
    filterTypeLabels = [];
    filterTagOptions = [];
    filterTagLabels = [];
    currentFilterType;
    filters = [];
    filtersObject = {};
    routeTo = 'asset-list';

    private complianceDropdownSubscription: Subscription;
    private subscriptionToAssetGroup: Subscription;
    private multilineChartSubscription: Subscription;
    private applicationSubscription: Subscription;
    private filterTypesSubscription: Subscription;
    subscriptionDomain: Subscription;
    selectedDomain: any;

    private autorefreshInterval;
    @Input() pageLevel: number;


    constructor(
                private utils: UtilsService,
                private multilineChartService: MultilineChartService,
                private assetGroupObservableService: AssetGroupObservableService,
                private autorefreshService: AutorefreshService,
                private selectComplianceDropdown: SelectComplianceDropdown,
                private issueFilterService: IssueFilterService,
                private domainObservableService: DomainTypeObservableService) {


        this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(
            assetGroupName => {
                this.selectedAssetGroup = assetGroupName;
            });

        this.subscriptionDomain = this.domainObservableService.getDomainType().subscribe(domain => {
                   this.selectedDomain = domain;
                   this.getApplications();
                   this.deleteFilters();
             });

        this.complianceDropdownSubscription = this.selectComplianceDropdown.getCompliance().subscribe(
            filtersObject => {
                this.filtersObject = filtersObject;
                this.updateComponent();
            });
    }


    ngOnChanges() {
        this.updateComponent();
    }

    onResize() {
        const element = document.getElementById('inv');
        if (element) {
            this.widgetWidth = parseInt((window.getComputedStyle(element, null).getPropertyValue('width')).split('px')[0], 10);
        }
    }

    ngOnInit() {

        this.durationParams = this.autorefreshService.getDuration();
        this.durationParams = parseInt(this.durationParams, 10);
        this.autoRefresh = this.autorefreshService.autoRefresh;

        const afterLoad = this;
        if (this.autoRefresh !== undefined) {
          if ((this.autoRefresh === true ) || (this.autoRefresh.toString() === 'true')) {

            this.autorefreshInterval = setInterval(function() {
              afterLoad.getIssues();
            }, this.durationParams);
          }
        }

        this.getApplications();
        setTimeout(() => {
            this.widgetWidth = parseInt(window.getComputedStyle(this.widgetContainer.nativeElement, null).getPropertyValue('width'), 10);
            this.widgetHeight = parseInt(window.getComputedStyle(this.widgetContainer.nativeElement, null).getPropertyValue('height'), 10);
            this.getIssues();
        }, 0);
    }

    updateComponent() {
        this.showdata = false;
        this.showloader = false;
        this.error = false;
        this.getIssues();

    }

    getIssues() {

        if (this.multilineChartSubscription) {
                this.multilineChartSubscription.unsubscribe();
            }


        const queryParameters = {
            'ag': this.selectedAssetGroup,
            'type': this.targetType,
            'filter': this.filtersObject,
            'domain': this.selectedDomain
        };

        this.colorSet = ['#645ec5', '#26ba9d', '#289cf7'];

        if (queryParameters.type !== undefined) {

            this.multilineChartSubscription = this.multilineChartService.getDataDiffNew(queryParameters).subscribe(
                response => {
                    try {

                        if (response[0][0].values.length < 1) {
                            this.showerror = true;
                            this.showloader = true;
                            this.error = true;
                            this.errorMessage = 'noDataAvailable';
                        } else {
                            this.showerror = false;
                            this.showloader = true;
                            this.error = false;
                            this.graphData = response[0];
                            this.showdata = true;
                        }

                    } catch (error) {
                        this.errorMessage = 'jsError';
                        this.handleError(error);
                    }
                },
                error => {
                    this.handleError(error);
                    this.showerror = true;
                    this.showloader = true;
                    this.errorMessage = 'apiResponseError';
                }
            );

        }

    }

    handleError(error) {
        this.dataLoaded = false;
        this.error = true;
    }

    getApplications(): void {

        if (this.applicationSubscription) {
            this.applicationSubscription.unsubscribe();
        }

        const queryParams = {
            'filterId': 3
        };
        const issueFilterUrl = environment.issueFilter.url;
        const issueFilterMethod = environment.issueFilter.method;
        this.applicationSubscription = this.issueFilterService.getFilters(queryParams, issueFilterUrl, issueFilterMethod).subscribe(
            (response) => {
                this.filterTypeLabels = _.map(response[0].response, 'optionName');
                this.filterTypeOptions = response[0].response;
            });
    }

    changeFilterType(filterType) {
        this.currentFilterType = filterType;
        this.filterTypesSubscription = this.issueFilterService.getFilters({
                'ag': this.selectedAssetGroup
            },
            (environment.base + this.utils.getParamsFromUrlSnippet(this.currentFilterType.optionURL).url),
            'GET')
            .subscribe((response) => {
                this.filterTagOptions = response[0].response;
                this.filterTagLabels = _.map(response[0].response, 'name');
            });
    }

    changeFilterTag(filterTag) {
        if (this.currentFilterType) {
            this.utils.addOrReplaceElement(this.filters, {
                typeName: this.currentFilterType.optionName,
                typeValue: this.currentFilterType.optionValue,
                tagName: filterTag.name,
                tagValue: filterTag.id,
                key: this.currentFilterType.optionName,
                value: filterTag.name
            }, (el) => {
                return el.key === this.currentFilterType.optionName;
            });
            this.selectComplianceDropdown.updateCompliance(this.utils.arrayToObject(this.filters, 'typeValue', 'tagValue'));
            this.currentFilterType = null;
        }
        this.utils.clickClearDropdown();
    }

    changedDropdown(val) {
        let option = _.find(this.filterTypeOptions, {optionName: val.id});

        if (option) {

            this.changeFilterType(option);

        } else {
            option = _.find(this.filterTagOptions, {name: val.id});
            this.changeFilterTag(option);
        }
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
                this.selectComplianceDropdown.updateCompliance(this.utils.arrayToObject(this.filters, 'typeValue', 'tagValue'));
            }
        } catch (error) {

        }
    }

    ngOnDestroy() {
        try {
            this.subscriptionToAssetGroup.unsubscribe();
            this.complianceDropdownSubscription.unsubscribe();
            if (this.multilineChartSubscription) {
                this.multilineChartSubscription.unsubscribe();
            }
            if (this.applicationSubscription) {
                this.applicationSubscription.unsubscribe();
            }
            this.subscriptionDomain.unsubscribe();
            if (this.filterTypesSubscription) {
                this.filterTypesSubscription.unsubscribe();
            }
            clearInterval(this.autorefreshInterval);
        } catch (error) {
        }
    }

}
