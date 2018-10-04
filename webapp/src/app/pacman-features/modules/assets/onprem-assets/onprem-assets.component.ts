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

import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { GridOptions } from 'ag-grid';
import { Subscription } from 'rxjs/Subscription';
import { Router } from '@angular/router';
import { DataCacheService } from '../../../../core/services/data-cache.service';
import { CommonResponseService } from '../../../../shared/services/common-response.service';
import { AssetGroupObservableService } from '../../../../core/services/asset-group-observable.service';
import { WorkflowService } from '../../../../core/services/workflow.service';
import { environment } from '../../../../../environments/environment';
import { RefactorFieldsService } from '../../../../shared/services/refactor-fields.service';
import { LoggerService } from '../../../../shared/services/logger.service';
import { UtilsService } from '../../../../shared/services/utils.service';

@Component({
  selector: 'app-onprem-assets',
  templateUrl: './onprem-assets.component.html',
  styleUrls: ['./onprem-assets.component.css'],
  providers: [ CommonResponseService]
})

export class OnpremAssetsComponent implements OnInit, OnDestroy {
  @Input() pageLevel = 0;

  private subscriptionToAssetGroup: Subscription;
  private targetSubscription: Subscription;
  private updateSubscription: Subscription;
  private dataSubscription: Subscription;
  private gridOptions: GridOptions;
  public backButtonRequired;
  pageTitle = 'Update Asset Data';
  breadcrumbArray: any = ['Assets'];
  breadcrumbLinks: any = ['asset-dashboard'];
  breadcrumbPresent: any;
  getContextMenuItems: any;
  gridApi: any;
  gridColumnApi: any;
  selectedAssetGroup = '';
  urlToRedirect = '';
  targetTypeSelected = '';
  savedTarget = '';
  userName = 'Hi Guest';
  showTargets = true;
  showSidebar = false;
  errorTabValue = -2;
  errorValue = 0;
  carouselState = 0;
  updateState = 0;
  paginatorSize = 3000;
  bucketNumber = 0;
  currentTotal = -1;
  activeIndex = -1;
  identifier: any = '';
  rowSelectData: any = [];
  ObjArr: any = [];
  targetTiles: any = [];
  editableFields: any = [];
  cbModel: any = [];
  ipModel: any = [];
  updateColData: any = [];
  currentData: any = [];
  selectFields: any = [];
  selObj: any = {};
  updatePayload: any = {};
  activeRadio = '';
  filterValues: any = [{id: 1, text: '1'},
                       {id: 2, text: '2'},
                       {id: 3, text: '3'},
                       {id: 4, text: '4'},
                       {id: 5, text: '5'},
                       {id: 6, text: '6'},
                       {id: 7, text: '7'},
                       {id: 8, text: '8'},
                       {id: 9, text: '9'},
                       {id: 10, text: '10'},
                       {id: 11, text: '11'},
                       {id: 12, text: '12'},
                       {id: 13, text: '13'}
                      ];

  constructor(
              private router: Router,
              private assetGroupObservableService: AssetGroupObservableService,
              private dataStore: DataCacheService,
              private workflowService: WorkflowService,
              private commonResponseService: CommonResponseService,
              private logger: LoggerService,
              private refactorFieldsService: RefactorFieldsService,
              private utils: UtilsService) {

        this.gridOptions = <GridOptions>{};
        this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(
            assetGroupName => {
                this.backButtonRequired = this.workflowService.checkIfFlowExistsCurrently(this.pageLevel);
                this.selectedAssetGroup = assetGroupName;
                this.getTargetTypes();
            });
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
   }

  ngOnInit() {

    this.breadcrumbPresent = 'Update Asset Data';
    this.urlToRedirect = this.router.routerState.snapshot.url;
  }

  resetPage() {
    this.showSidebar = false;
    this.gridOptions.columnDefs = [];
    this.gridOptions.rowData = [];
    this.rowSelectData = [];
    this.errorTabValue = -2;
    this.identifier = '';
    this.editableFields = [];
    this.activeRadio = '';
    this.ObjArr = [];
    this.cbModel = [];
    this.ipModel = [];
    this.updateColData = [];
    this.currentTotal = 0;
    this.currentData = [];
    this.bucketNumber = 0;
    this.carouselState = 0;
    this.updateState = 0;
    this.updatePayload = {};

    if (this.dataSubscription) {
      this.dataSubscription.unsubscribe();
    }
    if (this.gridOptions.api) {
      this.gridOptions.api.setColumnDefs(this.gridOptions.columnDefs);
      this.gridOptions.api.setRowData(this.gridOptions.rowData);
    }
  }

  onRowSelected() {
    this.rowSelectData = this.gridOptions.api.getSelectedRows();
    if (this.rowSelectData.length === 0) {
      this.showSidebar = false;
    }
  }

  onFilterChanged() {
      // Remove selected row on filter change so user will have to select rows which he wants to update again.
      this.gridOptions.api.deselectAll();
  }

  downloadCsv() {
    this.gridApi.exportDataAsCsv();
  }

  getTargetTypes() {
    if (this.targetSubscription) {
      this.targetSubscription.unsubscribe();
    }

    const payload = {};
    const queryParam = {
      'ag': this.selectedAssetGroup
    };
    this.targetTypeSelected = '';
    this.savedTarget = '';
    this.errorValue = 0;
    this.targetTiles = [];
    this.showTargets = true;
    this.resetPage();
    const url = environment.resourceCount.url;
    const method = environment.resourceCount.method;
    this.targetSubscription = this.commonResponseService.getData(url, method, payload, queryParam).subscribe(
      response => {
        try {
            this.errorValue = 1;
            if (response.assetcount.length === 0 ) {
              this.errorValue = -1;
            }
            this.targetTiles = response.assetcount;
        } catch (e) {
            this.errorValue = -1;
            this.logger.log('error', e);
        }
    },
    error => {
      this.errorValue = -1;
    });
  }

  getTableData() {
    this.resetPage();
    if (this.targetTypeSelected) {
        this.showTargets = false;
        this.savedTarget = this.targetTypeSelected;
    }
    this.getData();
  }

  getData() {
    if (this.savedTarget) {
      const payload = {
        'ag': this.selectedAssetGroup,
        'filter': {'resourceType': this.savedTarget},
        'targetType': this.savedTarget,
        'from': this.bucketNumber * this.paginatorSize,
        'searchtext': '',
        'size': this.paginatorSize
      };

      const queryParam = {};
      this.errorTabValue = 0;
      const url = environment.onpremData.url;
      const method = environment.onpremData.method;
      this.dataSubscription = this.commonResponseService.getData(url, method, payload, queryParam).subscribe(
        response => {
          try {
              if ( response.data.response.length === 0 && this.currentData.length === 0 ) {
                this.errorTabValue = -3;
              } else {
                this.identifier = response.data.identifier.key;
                this.editableFields = [];
                const editableKeys = Object.keys(response.data.editableFields);
                for (let x = 0; x < editableKeys.length; x++) {
                  const pushObj = {
                    key: editableKeys[x],
                    displayName: this.refactorFieldsService.getDisplayNameForAKey(editableKeys[x].toLowerCase()) || editableKeys[x],
                    type: response.data.editableFields[editableKeys[x]]
                  };
                  this.editableFields.push(pushObj);
                }
                this.currentTotal = response.data.total;
                const dataResponse = response.data.response;
                for (let i = 0; i < dataResponse.length; i++) {
                  this.currentData.push(dataResponse[i]);
                }

                if (this.paginatorSize === 0 && this.bucketNumber === 0) {
                  this.errorTabValue = 1;
                  this.processData(this.currentData);
                } else {
                  this.bucketNumber++;
                  if ( (this.bucketNumber * this.paginatorSize) < this.currentTotal ) {
                    this.getData();
                  } else {
                    this.errorTabValue = 1;
                    this.processData(this.currentData);
                  }
                }
              }
          } catch (e) {
            this.errorTabValue = -1;
            this.logger.log('error', e);
          }
      },
      error => {
        if (this.currentData.length === 0) {
          this.errorTabValue = -1;
        } else {
          this.errorTabValue = 1;
          this.processData(this.currentData);
        }
        this.logger.log('error', error);
      });
    }

  }

  openTargets() {
    if (this.savedTarget) {
      this.targetTypeSelected = this.savedTarget;
    }
    this.showTargets = true;
  }

  updateCols() {
    try {
      if (this.rowSelectData.length > 0) {
        this.showSidebar = true;
      }
      this.selectFields = [];
      for (let x = 0; x < this.editableFields.length; x++) {
        if (this.editableFields[x].key.toLowerCase() === 'u_projection_week') {
          this.selObj = {
            type: 'weekDropdown',
            values: this.filterValues,
            placeholder: 'Select Projection Week'
          };
        } else if (this.editableFields[x].type.toLowerCase() === 'boolean') {
          this.selObj = {
            type: 'radio',
            values: ['true', 'false'],
            placeholder: ''
          };
        } else {
          this.selObj = {
            type: 'textField',
            values: [],
            placeholder: ''
          };
        }
        this.selectFields.push(this.selObj);
      }
    } catch (e) {
      this.logger.log('error', e);
    }

  }

  processData(data) {
    try {
      this.gridOptions.rowData = data;
      let currentObj = {};
      for (let x = 0 ; x < data.length ; x++ ) {
        currentObj = Object.assign(currentObj, data[x]);
      }
      const ObjArr = Object.keys(currentObj);
      this.ObjArr = ObjArr;
      const extraFeatures = {
          checkboxSelection: true,
          pinned: 'left',
          lockPosition: true,
          headerCheckboxSelection: true,
          headerCheckboxSelectionFilteredOnly: true,
          maxWidth: 400,
          minWidth: 210
      };
      for (let i = 0; i < ObjArr.length; i++) {
        let eachObj = {
          field: ObjArr[i],
          headerName: this.refactorFieldsService.getDisplayNameForAKey(ObjArr[i]),
          minWidth: 160,
          maxWidth: 800,
          filterParams: { selectAllOnMiniFilter: true }
        };

        if (ObjArr[i] === this.identifier) {
            eachObj = Object.assign(eachObj, extraFeatures);
        }
        this.gridOptions.columnDefs.push(eachObj);
      }

      this.gridOptions.api.setColumnDefs(this.gridOptions.columnDefs);
      this.gridOptions.api.setRowData(data);
      this.onresize();
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  updateColSave() {
    try {
      this.updateColData = [];
      for (let k = 0; k < this.editableFields.length; k++ ) {
        if (this.cbModel[k]) {
          if (this.ipModel[k] === undefined ) {
            this.ipModel[k] = '';
          }
          const obj = {
            'key': this.editableFields[k].key,
            'value': this.ipModel[k]
          };
          this.updateColData.push(obj);
        }
      }
      let userId;
      if (this.updateColData.length > 0) {
        if (this.dataStore.get('currentUserLoginDetails')) {
          userId = (this.dataStore.getUserDetailsValue().getUserId());
        }

        const rawPayload = {
            'ag': this.selectedAssetGroup,
            'targettype': this.savedTarget,
            'update_by': userId,
            'resources': {
                'key': '',
                'values': []
            },
            'updates': []
        };

        for (let i = 0; i < this.rowSelectData.length; i++) {
            const identifier = this.identifier;
            const value = this.rowSelectData[i][this.identifier];
            rawPayload.resources.key = identifier;
            rawPayload.resources.values.push(value);
        }
        for (let j = 0; j < this.updateColData.length; j++) {
          const keyValObj = {
          'key': this.updateColData[j].key,
          'value': this.updateColData[j].value
          };
          rawPayload.updates.push(keyValObj);
        }
        this.carouselState++;
        this.updatePayload = rawPayload;
      }

    } catch (e) {
        this.logger.log('error', e);
    }
  }

  updateTable() {
    if (this.updateSubscription) {
      this.updateSubscription.unsubscribe();
    }
    this.carouselState++;
    this.updateState = 0;
    const url = environment.onpremDataUpdate.url;
    const method = environment.onpremDataUpdate.method;
    const payload = this.updatePayload;
    this.updateSubscription = this.commonResponseService.getData(url, method, payload, {}).subscribe(
        response => {
          try {
              this.updateState = 1;
              this.cbModel = [];
              this.ipModel = [];
              this.errorTabValue = 0;
              this.bucketNumber = 0;
              this.updateColData = [];
              this.currentTotal = 0;
              this.gridOptions.columnDefs = [];
              this.gridOptions.rowData = [];
              this.currentData = [];
              this.updatePayload = {};
              this.utils.clickClearDropdown();

              setTimeout(() => {
                this.showSidebar = false;
                this.onFilterChanged();
                this.rowSelectData = [];
                this.carouselState = 0;
              }, 2000);
              this.getData();
          } catch (e) {
              this.updateState = -1;
              this.logger.log('error', e);
          }
      },
      error => {
        this.updateState = -1;
      });
  }

  closeUpdateSidebar() {
    if (this.updateState === 1 ) {
      this.onFilterChanged();
    }
  }

  changeFilterTags(data, i) {
      this.ipModel[i] = parseInt(data.text, 10);
      if (data.text === undefined) {
        this.ipModel[i] = '';
      }
  }

  verifyIfChecked() {
    let flag = false;
    for (let i = 0; i < this.cbModel.length; i++ ) {
      if (this.cbModel[i]) {
        flag = true;
        break;
      }
    }
    if (flag) {
      return false;
    } else {
      return true;
    }
  }

  navigateBack() {
    try {
        this.workflowService.goBackToLastOpenedPageAndUpdateLevel(this.router.routerState.snapshot.root);
    } catch (error) {
        this.logger.log('error', error);
    }
  }

  onGridReady(params) {
    this.gridApi = params.api;
    this.gridColumnApi = params.columnApi;
  }

  autoSizeAll() {
    const allColumnIds = [];
    this.gridColumnApi.getAllColumns().forEach(function(column) {
      allColumnIds.push(column.colId);
    });
    this.gridColumnApi.autoSizeColumns(allColumnIds);
  }

  onresize() {
    if (this.ObjArr.length < 6 && this.ObjArr.length > 0) {
      this.gridApi.sizeColumnsToFit();
    } else {
      this.autoSizeAll();
    }
  }

  setRadioInput(val, index)  {
    if (val === 'true') {
      this.ipModel[index] = true;
    } else if (val === 'false') {
      this.ipModel[index] = false;
    } else if (val === undefined) {
      this.ipModel[index] = '';
    } else {
      this.ipModel[index] = val;
    }
  }

  typeof(val) {
    return typeof(val);
  }

  checkboxClicked(obj, i) {
      if (obj.type === 'boolean' && this.activeRadio === '') {
        this.activeRadio = 'true';
        this.ipModel[i] = true;
      }
  }

  ngOnDestroy() {
    if (this.subscriptionToAssetGroup) {
      this.subscriptionToAssetGroup.unsubscribe();
    }
    if (this.targetSubscription) {
      this.targetSubscription.unsubscribe();
    }
    if (this.updateSubscription) {
      this.updateSubscription.unsubscribe();
    }
    if (this.dataSubscription) {
      this.dataSubscription.unsubscribe();
    }
  }

}
