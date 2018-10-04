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
  OnDestroy,
  Output,
  EventEmitter,
  OnChanges,
  SimpleChanges,
  Input,
  AfterViewInit,
  ViewChild
} from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { CommonResponseService } from '../../../shared/services/common-response.service';
import { environment } from './../../../../environments/environment';
import { LoggerService } from '../../../shared/services/logger.service';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { VulnTrendGraphComponent } from '../vuln-trend-graph/vuln-trend-graph.component';
import { PermissionGuardService } from '../../../core/services/permission-guard.service';
import { UtilsService } from '../../../shared/services/utils.service';
import { AssetTilesService } from '../../../core/services/asset-tiles.service';


@Component({
  selector: 'app-vuln-report-trend',
  templateUrl: './vuln-report-trend.component.html',
  styleUrls: ['./vuln-report-trend.component.css'],
  providers: []
})

export class VulnReportTrendComponent implements OnDestroy, OnChanges, AfterViewInit {

  @ViewChild( VulnTrendGraphComponent ) child;

  vulnReportSubscription: Subscription;
  subscriptionToAssetGroup: Subscription;
  vulnNotesSubscription: Subscription;
  selectedAssetGroup;
  agName;
  selectedMonth = 0;
  graphResponse = 0;
  notesData = [];
  notesStatus = 'Please wait, we\'re fetching your notes';
  notesResponse = 0;
  adminAccess = false;
  months = [1, 2, 3, 6, 12];
  graphData;
  currentFilters = '3,4,5';
  parentWidth;
  infoActive: true;

  maxVal: any = [0, 0];
  axisValues = {
    y0: [],
    y1: []
  };

  @Input() filter: any;
  @Output() emitError = new EventEmitter();
  @Output() infoState = new EventEmitter();

  constructor(
    private commonResponseService: CommonResponseService,
    private logger: LoggerService,
    private assetGroupObservableService: AssetGroupObservableService,
    private permissions: PermissionGuardService,
    private utils: UtilsService,
    private assetTileService: AssetTilesService
  ) {
    this.adminAccess = this.permissions.checkAdminPermission();
    this.subscriptionToAssetGroup = this.assetGroupObservableService
      .getAssetGroup()
      .subscribe(assetGroupName => {
        if (assetGroupName) {
          this.selectedAssetGroup = assetGroupName;
          this.setAgNameFromSelectedAssetGroup(this.selectedAssetGroup);
          this.selectedMonth = 0;
          this.updateComponent();
        }
      });
  }

  ngAfterViewInit() {
    const ele = document.getElementById('graphParent');
    if (ele) {
      this.parentWidth = ele.clientWidth;
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    try {
      const DataChange = changes['filter'];

      if (DataChange && DataChange.currentValue ) {
        this.currentFilters  = DataChange.currentValue.severity;
        const prev = DataChange.previousValue;
        if ((JSON.stringify(DataChange.currentValue) !== JSON.stringify(prev)) && !DataChange.firstChange) {
          this.updateComponent();
        }
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  updateComponent() {

    if (this.vulnReportSubscription) {
      this.vulnReportSubscription.unsubscribe();
    }
    if (this.vulnNotesSubscription) {
      this.vulnNotesSubscription.unsubscribe();
    }
    this.notesData = [];
    this.graphResponse = 0;
    this.notesStatus = 'Please wait, we\'re fetching your notes';
    this.notesResponse = 0;
    this.maxVal = [0, 0];
    this.axisValues = {
      y0: [],
      y1: []
    };
    this.getData();
  }

  getData() {
    const todayDate = new Date();
    const previousDate = new Date();
    previousDate.setMonth(previousDate.getMonth() - this.months[this.selectedMonth]);
    const graphPayload = {
      'ag': this.selectedAssetGroup,
      'filter': { 'severity': this.currentFilters },
      'from': previousDate.toISOString(),
      'to': todayDate.toISOString()
    };
    const graphQueryParam = {};

    try {
      const vulnReportGraphUrl = environment.vulnReportGraph.url;
      const vulnReportGraphMethod = environment.vulnReportGraph.method;
      this.vulnReportSubscription = this.commonResponseService.getData(
       vulnReportGraphUrl, vulnReportGraphMethod, graphPayload, graphQueryParam).subscribe(
        response => {

          try {
              if (!response.data.trend.length) {
                this.graphResponse = -1;
                this.emitError.emit();
                if (this.vulnNotesSubscription) {
                  this.vulnNotesSubscription.unsubscribe();
                }
              } else {
                this.graphData = response.data.trend;
                this.graphResponse = 1;
                this.processData(this.graphData);
                if (this.notesData.length) {
                  this.updateNotePlot();
                }
              }
          } catch (e) {
              this.graphResponse = -1;
              if (this.vulnNotesSubscription) {
                this.vulnNotesSubscription.unsubscribe();
              }
              this.emitError.emit();
              this.logger.log('error', e);
          }
        },
        error => {
          this.graphResponse = -1;
          this.emitError.emit();
          if (this.vulnNotesSubscription) {
            this.vulnNotesSubscription.unsubscribe();
          }
          this.logger.log('error', error);
        });
      } catch (e) {
        this.logger.log('error', e);
        this.graphResponse = -1;
        if (this.vulnNotesSubscription) {
          this.vulnNotesSubscription.unsubscribe();
        }
        this.emitError.emit();
      }

      this.fetchNotesData();

  }

  fetchNotesData() {
    try {

      if (this.vulnNotesSubscription) {
        this.vulnNotesSubscription.unsubscribe();
      }
      this.notesData = [];
      this.infoState.emit(true);
      this.notesStatus = 'Please wait, we\'re fetching your notes';
      this.notesResponse = 0;

      const previousDate = new Date();
      previousDate.setMonth(previousDate.getMonth() - this.months[this.selectedMonth]);

      const notesQueryParam = {
        'ag': this.selectedAssetGroup,
        'from': previousDate.toISOString().substring(0, 10)
      };
      const notesPayload = {};

      const vulnReportNotesUrl = environment.getVulnTrendNotes.url;
      const vulnReportNotesMethod = environment.getVulnTrendNotes.method;
      this.vulnNotesSubscription = this.commonResponseService.getData(
        vulnReportNotesUrl, vulnReportNotesMethod, notesPayload, notesQueryParam).subscribe(
        response => {

          try {
            this.notesResponse = 1;
            this.notesStatus = 'Click on the graph to add or edit notes';
            this.processNotes(response.notes);
            if (this.graphResponse === 1 ) {
              this.updateNotePlot();
            }
          } catch (e) {
            this.logger.log('error', e);
            this.notesResponse = -1;
            this.notesStatus = 'We\'re unable to fetch your notes';
            this.infoState.emit(false);
          }
      },
      error => {
        this.logger.log('error', error);
        this.notesResponse = -1;
        this.notesStatus = 'We\'re unable to fetch your notes';
        this.infoState.emit(false);
      });
    } catch (e) {
      this.logger.log('error', e);
      this.notesResponse = -1;
      this.notesStatus = 'We\'re unable to fetch your notes';
      this.infoState.emit(false);
    }
  }

  processNotes(notes) {
    const notesArr = [];
    let cnt = 0;
    for (let i = 0; i < notes.length; i++ ) {
      for ( let j = 0; j < notes[i].data.length; j++ ) {
        const obj = {type: notes[i].type};
        Object.assign(obj, notes[i].data[j]);
        notesArr.push(obj);
      }
    }
    notesArr.sort( function (a, b) {
      return (new Date(a.date)).getTime() - (new Date(b.date)).getTime();
    });
    this.notesData = [];
    const notesObj = {};
    for (let p = 0; p < notesArr.length; p++) {
      notesArr[p].label = ++cnt;
      notesObj[notesArr[p].date] = { data: [], date: notesArr[p].date, dateStr: (notesArr[p].date).substring(0, 5) };
    }
    for (let x = 0; x < notesArr.length; x++) {
      notesObj[notesArr[x].date]['data'].push(notesArr[x]);
    }
    for (let y = 0; y < Object.keys(notesObj).length; y++) {
      this.notesData[y] = notesObj[Object.keys(notesObj)[y]];
    }
  }

  processData(data) {
     // Make the display label of date
     const newData = data;

     for ( let i = 0; i < newData.length ; i++ ) {
      const date = this.utils.getDateAndTime(newData[i].date); //new Date(newData[i].date);
      newData[i].date = date;
      let day = '0' + date.getDate().toString();
      day = day.slice(-2);
      let month = '0' + (1 +  date.getMonth()).toString();
      month = month.slice(-2);
      newData[i].dateStr = month + '/' + day;
    }
    // Sort data wrt increasing date
    newData.sort(function(a, b) {return (a.date.getTime() > b.date.getTime()) ? 1 : ((b.date.getTime() > a.date.getTime()) ? -1 : 0); } );

  }

  onResize() {
    try {
      const ele = document.getElementById('graphParent');
      if (ele) {
        this.parentWidth = ele.clientWidth;
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  setAgNameFromSelectedAssetGroup(selectedAssetGroup) {
    this.assetTileService
      .getAssetGroupDisplayName(selectedAssetGroup)
      .subscribe(
        response => {
          setTimeout(() => {
            this.agName = response as string;
          }, 0);
        },
        error => {
          this.logger.log(
            'error',
            'error in getting asset group display name - ' + error
          );
        }
      );
  }

  ngOnDestroy() {
    if (this.vulnReportSubscription) {
      this.vulnReportSubscription.unsubscribe();
    }
    if (this.vulnNotesSubscription) {
      this.vulnNotesSubscription.unsubscribe();
    }
  }

  updateNotePlot() {
    if (this.notesData.length) {
      this.infoActive = true;
      this.infoState.emit(this.infoActive);
      this.processData(this.notesData);
    }
  }

}
