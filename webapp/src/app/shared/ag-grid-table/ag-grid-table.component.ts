import { Component, OnInit, Input, Output, OnChanges, EventEmitter, SimpleChanges } from '@angular/core';
import { GridOptions } from 'ag-grid';
import { RefactorFieldsService } from '../services/refactor-fields.service';
import { LoggerService } from '../services/logger.service';

@Component({
  selector: 'app-ag-grid-table',
  templateUrl: './ag-grid-table.component.html',
  styleUrls: ['./ag-grid-table.component.css'],
  providers: [LoggerService]
})

export class AgGridTableComponent implements OnInit, OnChanges {
  @Input() data;
  @Input() totalRows;
  @Input() firstPaginator;
  @Input() lastPaginator: number;
  @Input() identifier;
  @Input() currentPointer: number;
  @Output() nextPageCalled = new EventEmitter<any>();
  @Output() previousPageCalled = new EventEmitter<any>();
  @Output() searchTriggerred = new EventEmitter<any>();
  @Output() searchRowTxt = new EventEmitter<any>();
  private gridOptions: GridOptions;
  gridApi: any;
  popRows = ['Download Data'];
  searchTxt = '';
  getContextMenuItems: any;
  searchVal = '';
  gridColumnApi: any;
  rowSelectData: any = [];

  ObjArr: any = [];
  showtables = false;
  showSidebar = false;
  selected: any;
  rowData;
  columnDefs;
  constructor(
    private logger: LoggerService,
    private refactorFieldsService: RefactorFieldsService,
  ) {
    this.gridOptions = <GridOptions>{
    };
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
    // this.columnDefs = Object.keys(this.data[0]);
  }

  ngOnChanges(changes: SimpleChanges) {
    try {

      const DataChange = changes['data'];

      if (DataChange && DataChange.currentValue) {
        this.rowData = DataChange.currentValue;
        // this.columnDefs = Object.keys(this.rowData[0]);
        this.processData(this.rowData);
      }
    } catch (e) {
      // this.logger.log('error', e);
    }
  }
  // selectTab(tab, index) {
  //   if (tab === this.selected) {
  //       return;
  //   }
  //   const prevIndex = !!this.selected ? _.findIndex(this.tabsData, (tabData) => {
  //       return tabData === this.selected;
  //   }) + 1 : 0;

  //   this.selected = tab;
  //   this.setGridRowData();

  // }
  onRowSelected() {
    this.rowSelectData = this.gridOptions.api.getSelectedRows();
    if (this.rowSelectData.length === 0) {
      this.showSidebar = false;
    }
  }
  triggerSearch() {

    this.searchTriggerred.emit();
  }
  searchCalled(searchQuery) {
    this.searchRowTxt.emit(searchQuery);
    this.searchVal = searchQuery;
  }

  autoSizeAll() {
    const allColumnIds = [];
    this.gridColumnApi.getAllColumns().forEach(function (column) {
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

  processData(data) {
    try {
      this.gridOptions.columnDefs = [];
      this.gridOptions.api.setRowData([]);
      this.gridApi.setColumnDefs(this.gridOptions.columnDefs);
      this.gridOptions.rowData = data;
      let currentObj = {};
      for (let x = 0; x < data.length; x++) {
        currentObj = Object.assign(currentObj, data[x]);
      }
      // this.selected = this.tabsData[0].name;
      const ObjArr = Object.keys(currentObj);
      this.ObjArr = ObjArr;
      const extraFeatures = {
        checkboxSelection: true,
        pinned: 'left',
        lockPosition: true,
        headerCheckboxSelection: true,
        headerCheckboxSelectionFilteredOnly: true,
        maxWidth: 600,
        minWidth: 210
      };
      for (let i = 0; i < ObjArr.length; i++) {
        let eachObj = {
          field: ObjArr[i],
          headerName: this.refactorFieldsService.getDisplayNameForAKey(ObjArr[i]),
          minWidth: 210,
          maxWidth: 600
          // filterParams: { selectAllOnMiniFilter: true }
        };

        if (ObjArr[i] === this.identifier) {
          eachObj = Object.assign(eachObj, extraFeatures);
        }
        this.gridOptions.columnDefs.push(eachObj);
      }
      if (this.gridApi) {
        const self = this;
        setTimeout(() => {
          self.gridApi.setColumnDefs(self.gridOptions.columnDefs);
          self.gridOptions.api.setRowData(self.rowData);
        }, 100);

        // this.setGridRowData();
        this.onresize();
      }

      //  this.gridOptions.api.setColumnDefs(this.gridOptions.columnDefs);
      // this.onresize();
    } catch (e) {
      this.logger.log('error', e);
    }
  }
  // setGridRowData() {
  //   this.gridOptions.rowData = this.rowData[this.selected];
  //   this.gridApi.setRowData(this.gridOptions.rowData);
  // }

  onGridReady(params) {
    this.gridApi = params.api;
    this.gridColumnApi = params.columnApi;
  }
  nextPage() {
    if (this.lastPaginator < this.totalRows) {
      this.nextPageCalled.emit();
    }
  }
  prevPage() {
    if (this.currentPointer > 0) {
      this.previousPageCalled.emit();
    }
  }
  emitRowText(row) {
    this.gridApi.exportDataAsCsv();
}

}


