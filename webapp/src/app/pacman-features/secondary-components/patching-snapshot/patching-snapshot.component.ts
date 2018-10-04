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

import { Component, OnInit, OnDestroy, Output, EventEmitter } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { AutorefreshService } from '../../services/autorefresh.service';
import { environment } from './../../../../environments/environment';
import { LoggerService } from '../../../shared/services/logger.service';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { GridOptions } from 'ag-grid';
import { PatchingSnapshotService } from '../../services/patching-snapshot.service';

@Component({
  selector: 'app-patching-snapshot',
  templateUrl: './patching-snapshot.component.html',
  styleUrls: ['./patching-snapshot.component.css'],
  providers: [PatchingSnapshotService, AutorefreshService]
})

export class PatchingSnapshotComponent implements OnInit, OnDestroy {

  selectedAssetGroup: string;
  private errorMessage = 'apiResponseError';

  getContextMenuItems: any;
  gridApi: any;
  gridColumnApi: any;
  columns: any = [];
  initComplete = false;

  private gridOptions: GridOptions;
  private subscriptionToAssetGroup: Subscription;
  private dataSubscription: Subscription;

  @Output() errorOccurred = new EventEmitter();

  errorValue = 0;

  constructor(  private patchingSnapshotService: PatchingSnapshotService,
          private assetGroupObservableService: AssetGroupObservableService,
          private logger: LoggerService) {

    this.gridOptions = <GridOptions>{};
    this.gridOptions.columnDefs = [];

    this.gridOptions.rowData = [];
        this.getContextMenuItems = function getContextMenuItems(params) {
            const result = [
              'toolPanel',
              'separator',
              'copy',
              'separator',
              'csvExport',
              'separator',
              'autoSizeAll',
              'resetColumns'
            ];
            return result;
        };

    this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(
      assetGroupName => {
          this.selectedAssetGroup = assetGroupName;
          if (this.initComplete) {
            this.updateComponent();
          }
    });

  }

  ngOnInit() {
    this.updateComponent();
    this.initComplete = true;
  }

  updateComponent() {
      this.errorValue = 0;
      this.getData();
  }

  downloadCsv() {
    this.gridApi.exportDataAsCsv();
  }

  getData() {

    if (this.dataSubscription) {
        this.dataSubscription.unsubscribe();
    }

    const payload = {
      'ag': this.selectedAssetGroup,
      'filter': {},
      'from': 0,
      'searchtext': '',
      'size': 0
    };

    this.errorValue = 0;
    const allPatchingTableUrl = environment.patchingSnapshot.url;
    const allPatchingTableMethod = environment.patchingSnapshot.method;
    this.errorValue = 0;

    this.dataSubscription = this.patchingSnapshotService.getData(allPatchingTableUrl, allPatchingTableMethod, payload).subscribe(
      response => {

        try {
            if (response.data.patchingProgress.length) {
              this.errorValue = 1;
              this.processData(response);
            } else {
              this.errorOccurred.emit();
              this.errorValue = -1;
              this.errorMessage = 'noDataAvailable';
            }

        } catch (e) {
            this.errorOccurred.emit();
            this.errorValue = -1;
            this.errorMessage = 'jsError';
            this.logger.log('error', e);
        }
    },
    error => {
      this.errorOccurred.emit();
      this.errorValue = -1;
      this.errorMessage = 'apiResponseError';
      this.logger.log('error', error);
     });
  }

  processData(data) {
   this.columns = [];
   const ObjArr = data.data.patchingProgress;
   let j = 0;
   while (ObjArr[j] == null ) {
    j++;
   }
   const columns = Object.keys(ObjArr[j]);
   this.columns = columns;

   let eachObj = {};
   this.gridOptions.columnDefs = [];
   this.gridOptions.rowData = [];
   const objProperties = {
      minWidth: 160,
      maxWidth: 800
  };

   for ( let i = 0; i < columns.length; i++) {
     if (columns[i].toLowerCase() === 'director') {
         eachObj = {
           pinned: 'left',
           lockPosition: true,
           field: columns[i],
           headerName: columns[i],
           minWidth: 160,
           maxWidth: 800,
           order: 1
         };
    } else if (columns[i].toLowerCase() === 'q2 scope') {
      eachObj = {
        field: columns[i],
        headerName: columns[i],
        order: 2
      };
      Object.assign(eachObj, objProperties);
    } else if (columns[i].toLowerCase() === 'patched') {
      eachObj = {
        field: columns[i],
        headerName: columns[i],
        order: 3
      };
      Object.assign(eachObj, objProperties);
    } else if (columns[i].toLowerCase() === 'unpatched') {
        eachObj = {
          field: columns[i],
          headerName: columns[i],
          order: 4
        };
        Object.assign(eachObj, objProperties);
      } else if (columns[i].toLowerCase() === '%patched') {
         eachObj = {
          field: columns[i],
          headerName: columns[i],
          minWidth: 160,
          maxWidth: 800,
          order: 5,
          cellStyle: function(params) {
              if (params.value === 100) {
                  return {fontFamily: 'ex2-light', color: '#008000', textShadow: '1px 0'};
              } else if (params.value < 100 && params.value > 49) {
                  return {fontFamily: 'ex2-light', color: '#ff8a43', textShadow: '1px 0'};
              } else if (params.value > -1 && params.value < 50) {
                  return {fontFamily: 'ex2-light', color: 'rgba(212,3,37,1)', textShadow: '1px 0'};
              } else {
                  return null;
              }
           }
         };
     } else {
       eachObj = {
           field: columns[i],
           headerName: columns[i],
           minWidth: 160,
           maxWidth: 800
         };
     }
      this.gridOptions.columnDefs.push(eachObj);
    }
    // sortobject as per 'order' property set.
    this.gridOptions.columnDefs.sort((a, b) => {
      return a['order'] - b['order'];
    });
    this.gridOptions.rowData = data.data.patchingProgress;
    if (this.gridApi) {
      this.gridApi.setColumnDefs(this.gridOptions.columnDefs);
      this.gridApi.setRowData(this.gridOptions.rowData);
      this.onresize();
    }
  }

  onresize() {
    if (this.columns.length < 6 && this.columns.length > 0) {
      setTimeout(() => {
        this.gridApi.sizeColumnsToFit();
      }, 3);
    } else {
      this.autoSizeAll();
    }
  }

  onGridReady(params) {
    this.gridApi = params.api;
    this.gridColumnApi = params.columnApi;
  }

  autoSizeAll() {
    const allColumnIds = [];
    if (this.gridColumnApi) {
      this.gridColumnApi.getAllColumns().forEach(function(column) {
        allColumnIds.push(column.colId);
      });
      this.gridColumnApi.autoSizeColumns(allColumnIds);
    }
  }

  ngOnDestroy() {
    try {
      if (this.subscriptionToAssetGroup) {
        this.subscriptionToAssetGroup.unsubscribe();
      }
      if (this.dataSubscription) {
        this.dataSubscription.unsubscribe();
      }
    } catch (error) {
      this.logger.log('error', '--- Error while unsubscribing ---');
    }
  }

}
