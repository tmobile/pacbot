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

import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { AutorefreshService } from '../../services/autorefresh.service';
import { MultilineChartServiceCpu } from '../../services/multilineCpu.service';
import { MultilineChartServiceDisk } from '../../services/multilinedisk.service';
import { MultilineChartServiceNetwork } from '../../services/multilineNetwork.service';
import { DomainTypeObservableService } from '../../../core/services/domain-type-observable.service';
import { UtilsService } from '../../../shared/services/utils.service';

@Component({
  selector: 'app-utilization-container',
  templateUrl: './utilization-container.component.html',
  styleUrls: ['./utilization-container.component.css'],
  providers: [ MultilineChartServiceCpu, MultilineChartServiceDisk, MultilineChartServiceNetwork, AutorefreshService]
})

export class UtilizationContainerComponent implements OnInit, OnDestroy {

  @ViewChild('widgetCpu') widgetContainer: ElementRef;
  @ViewChild('widgetNet') widgetContainerNetwork: ElementRef;
  @ViewChild('widgetDisk') widgetContainerDisk: ElementRef;

  widgetWidth: number;
  widgetHeight: number;
  widgetWidthNet: number;
  widgetHeightNet: number;
  widgetWidthDisk: number;
  widgetHeightDisk: number;
  selectedAssetGroup: string;
  errorMessages;
  durationParams: any;
  autoRefresh: boolean;

  private error = false;
  private dataLoaded = false;
  private autorefreshInterval;
  graphData: any;
  networkData: any;
  networkDatamassaged: any;
  diskData: any;
  diskDatamassaged: any;
  colorSetCpu: any = [];
  colorSetNetwork: any = [];
  colorSetDisk: any = [];
  countNetwork: any;
  countDisk: any;
  unitNetwork: any;
  unitDisk: any;
  errorMessage: any;
  showerrorCpu = false;
  showerrorNetwork = false;
  showerrorDisk = false;
  showloaderCpu = false;
  showloaderDisk = false;
  showloaderNetwork = false;
  targetType: any = 'default';

  private subscriptionToAssetGroup: Subscription;
  private multilineChartCpuSubscription: Subscription;
  private multilineChartDiscSubscription: Subscription;
  private multilineChartNetworkSubscription: Subscription;
  subscriptionDomain: Subscription;
  selectedDomain: any;

  constructor(
              private multilineChartServiceCpu: MultilineChartServiceCpu,
              private multilineChartServiceDisk: MultilineChartServiceDisk,
              private multilineChartServiceNetwork: MultilineChartServiceNetwork,
              private assetGroupObservableService: AssetGroupObservableService,
              private autorefreshService: AutorefreshService,
              private domainObservableService: DomainTypeObservableService,
              private utilService: UtilsService ) {


              this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(
                assetGroupName => {
                    this.selectedAssetGroup = assetGroupName;
              });

              this.subscriptionDomain = this.domainObservableService.getDomainType().subscribe(domain => {
                   this.selectedDomain = domain;
                   this.updateComponent();
             });

  }

  ngOnInit() {

    this.durationParams = this.autorefreshService.getDuration();
    this.durationParams = parseInt(this.durationParams, 10);
    this.autoRefresh = this.autorefreshService.autoRefresh;
    this.updateComponent();

    const afterLoad = this;
    if (this.autoRefresh !== undefined) {
      if ((this.autoRefresh === true ) || (this.autoRefresh.toString() === 'true')) {
        this.autorefreshInterval = setInterval(function(){
          afterLoad.getIssues();
        }, this.durationParams);
      }
    }
  }

  updateComponent() {
    this.graphData = false;
    this.networkDatamassaged = false;
    this.diskDatamassaged = false;
    this.showloaderCpu = false;
    this.showloaderNetwork = false;
    this.showloaderDisk = false;
    this.getIssues();
  }

  getIssues() {
    const queryParams = {
          'ag': this.selectedAssetGroup,
          'domain': this.selectedDomain
    };

      if (this.multilineChartDiscSubscription) { this.multilineChartDiscSubscription.unsubscribe(); }
      if (this.multilineChartCpuSubscription ) { this.multilineChartCpuSubscription.unsubscribe(); }
      if (this.multilineChartNetworkSubscription) { this.multilineChartNetworkSubscription.unsubscribe(); }

    this.colorSetCpu = ['#f75c03', '#26ba9d', '#645ec5'];
    this.colorSetNetwork = ['#ffb00d', '#645ec5', '#f75c03'];
    this.colorSetDisk = ['#3c5079', '#ffb00d', '#645ec5'];

    this.multilineChartCpuSubscription = this.multilineChartServiceCpu.getData(queryParams).subscribe(
      response => {
        try {
          this.showloaderCpu = true;
          if (!this.utilService.checkIfAPIReturnedDataIsEmpty(response[0].values)) {
            this.showerrorCpu = false;
            this.graphData = response;
            this.widgetWidth = parseInt(window.getComputedStyle(this.widgetContainer.nativeElement, null).getPropertyValue('width'), 10);
            this.widgetHeight = parseInt(window.getComputedStyle(this.widgetContainer.nativeElement, null).getPropertyValue('height'), 10);
          } else {
            this.showerrorCpu = true;
            this.errorMessage = 'noDataAvailable';
          }
        } catch (error) {
          this.showerrorCpu = true;
          this.showloaderCpu = true;
          this.handleError(error);
        }
      },
      error => {
        this.showerrorCpu = true;
        this.showloaderCpu = true;
        this.errorMessage = 'apiResponseError';
      }
    );

    this.multilineChartDiscSubscription = this.multilineChartServiceDisk.getData(queryParams).subscribe(
      response => {
        try {
          this.showloaderDisk = true;
          if (!this.utilService.checkIfAPIReturnedDataIsEmpty(response[0][0].values)) {
            this.showerrorDisk = false;
            this.diskData = response[0];
            this.handleUnitDisk(response[0]);
            this.widgetWidthDisk = parseInt(window.getComputedStyle(this.widgetContainerDisk.nativeElement, null).getPropertyValue('width'), 10);
            this.widgetHeightDisk = parseInt(window.getComputedStyle(this.widgetContainerDisk.nativeElement, null).getPropertyValue('height'), 10);
          } else {
            this.showerrorDisk = true;
            this.errorMessage = 'noDataAvailable';
          }

        } catch (error) {
          this.showerrorDisk = true;
          this.showloaderDisk = true;
          this.errorMessage = 'apiResponseError';
        }
      },
      error => {
        this.showerrorDisk = true;
        this.showloaderDisk = true;
        this.errorMessage = 'apiResponseError';
      }
    );

    this.multilineChartNetworkSubscription = this.multilineChartServiceNetwork.getData(queryParams).subscribe(
      response => {
        try {
          this.showloaderNetwork = true;
          if (!this.utilService.checkIfAPIReturnedDataIsEmpty(response[0][0].values)) {
            this.showerrorNetwork = false;
            this.networkData = response[0];
            this.handleUnit(response[0]);
            this.widgetWidthNet = parseInt(window.getComputedStyle(this.widgetContainerNetwork.nativeElement, null).getPropertyValue('width'), 10);
            this.widgetHeightNet = parseInt(window.getComputedStyle(this.widgetContainerNetwork.nativeElement, null).getPropertyValue('height'), 10);
          } else {
            this.showerrorNetwork = true;
            this.errorMessage = 'noDataAvailable';
          }
        } catch (error) {
          this.showerrorNetwork = true;
          this.showloaderNetwork = true;
          this.errorMessage = 'apiResponseError';
        }
      },
      error => {
        this.showerrorNetwork = true;
        this.showloaderNetwork = true;
        this.errorMessage = 'apiResponseError';
      }
    );
  }

  handleUnit(data) {

    for ( let i = 0 ; i < data.length; i++) {
      for ( let j = 0 ; j < data[i].values.length; j++) {
        const value = data[i].values[j].value;
        const checkValue =  this.reduceValue(value);
        this.networkData[i].values[j].value = Math.round(checkValue);
      }
    }
    this.networkDatamassaged = this.networkData;
    const unit = ['bytes', 'KB', 'MB', 'GB', 'TB', 'PB'];
    this.unitNetwork = unit[this.countNetwork];
  }

  handleUnitDisk(data) {
    for ( let i = 0; i < data.length; i++) {
      for ( let j = 0; j < data[i].values.length; j++) {
        const value = data[i].values[j].value;
        const checkValue =  this.reduceValueDisk(value);
        this.diskData[i].values[j].value = Math.round(checkValue);
      }
    }
    this.diskDatamassaged = this.diskData;
    const unitDisk = ['bytes', 'KB', 'MB', 'GB', 'TB', 'PB'];
    this.unitDisk = unitDisk[this.countDisk];
  }

  reduceValue(data) {


    const valueToBeChkd = data;
    if (valueToBeChkd === 0) {
       return valueToBeChkd;
    } else {
      const i = (Math.floor(Math.log(valueToBeChkd) / Math.log(1024)));
      if (i === 0) { return valueToBeChkd; }
      this.countNetwork = i;
      return (valueToBeChkd / Math.pow(1024, i)).toFixed(2);
    }
  }

  reduceValueDisk(data) {

    const valueToBeChkd = data;
    if (valueToBeChkd === 0) {
       return valueToBeChkd;
    } else {
      const i = (Math.floor(Math.log(valueToBeChkd) / Math.log(1024)));
      if (i === 0) { return valueToBeChkd; }
      this.countDisk = i;
      return (valueToBeChkd / Math.pow(1024, i)).toFixed(2);
    }
  }

  handleError(error) {

    this.dataLoaded = false;
    this.errorMessage = 'apiResponseError';
    this.error = true;
  }

  ngOnDestroy() {
    try {
      this.subscriptionToAssetGroup.unsubscribe();
      this.multilineChartDiscSubscription.unsubscribe();
      this.multilineChartCpuSubscription.unsubscribe();
      this.multilineChartNetworkSubscription.unsubscribe();
      this.subscriptionDomain.unsubscribe();
      clearInterval(this.autorefreshInterval);
    } catch (error) {
    }
  }

}
