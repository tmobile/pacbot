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

import {Component, OnInit, Input, Output, EventEmitter, OnChanges, SimpleChanges, ViewChildren} from '@angular/core';
import * as _ from 'lodash';
import {LoggerService} from '../services/logger.service';
import {animate, group, query, style, transition, trigger} from '@angular/animations';
import { RefactorFieldsService } from '../../shared/services/refactor-fields.service';

@Component({
    selector: 'app-data-table',
    templateUrl: './data-table.component.html',
    styleUrls: ['./data-table.component.css'],
    animations: [
        trigger('tableCarousel', [
            transition('* => right', [
                group([
                    query('[currentTable]', [
                        style({
                            transform: 'translateX(100%)',
                        }),
                        animate('.35s ease-in-out',
                            style({
                                transform: 'translateX(0%)',
                            }))
                    ], {optional: true}),
                    query('[previousTable]', [
                        style({
                            transform: 'translateX(0%)',
                        }),
                        animate('.35s ease-in-out',
                            style({
                                transform: 'translateX(-100%)',
                            }))
                    ], {optional: true})
                ])
            ]),
            transition('* => left', [
                group([
                    query('[currentTable]', [
                        style({
                            transform: 'translateX(-100%)',
                            position: 'absolute'
                        }),
                        animate('.35s ease-in-out',
                            style({
                                transform: 'translateX(0%)',
                            }))
                    ], {optional: true}),
                    query('[previousTable]', [
                        style({
                            transform: 'translateX(0%)',
                        }),
                        animate('.35s ease-in-out',
                            style({
                                transform: 'translateX(100%)',
                            }))
                    ], {optional: true})
                ])
            ]),
        ])
    ]
})

export class DataTableComponent implements OnInit, OnChanges {

    constructor(
        private refactorFieldsService: RefactorFieldsService,
        private logger: LoggerService) {
    }

    @ViewChildren('filteredArray') filteredItems = null;
    @Input() dataHead;
    @Input() searchableHeader;
    @Input() heightValue;
    @Input() outerArr: any = [];
    @Input() showingArr: any = [];
    @Input() allColumns: any = [];
    @Input() totalRows: any;
    @Input() firstPaginator: number;
    @Input() lastPaginator: number;
    @Input() currentPointer: number;
    @Input() headerColName = '';
    @Input() direction = 0;
    @Input() paginatorAbsent = false;
    @Input() tabFilterProperty;
    @Input() cbModel;
    @Input() checkBox;
    @Input() columnWhiteList;
    @Input() buttonsArr;
    @Input() searchTextValues = '';
    @Input() popRows: any;
    @Input() parentName: any;
    @Input() buttonColumn;
    @Input() errorValue: any;
    @Input() showGenericMessage: any;
    @Input() tableIdAppend: any;
    @Input() searchPassed = '';
    @Input() checkedList = [];
    @Output() selectedRow = new EventEmitter<any>();
    @Output() menuClick = new EventEmitter<any>();
    @Output() searchRowTxt = new EventEmitter<any>();
    @Output() noScrollDetected = new EventEmitter<any>();
    @Output() searchTriggerred = new EventEmitter<any>();
    @Output() previousPageCalled = new EventEmitter<any>();
    @Output() tabSelected = new EventEmitter<any>();
    @Output() nextPageCalled = new EventEmitter<any>();
    @Output() cbClicked = new EventEmitter<any>();
    @Output() rowClickText = new EventEmitter<string>();

    errorMessage = 'apiResponseError';
    indexSelected: number;
    sortArr: any = [];
    rowDetails: any = [];
    rowIndex = -1;
    noMinHeight = false;
    rowObj: any = {};
    searchVal = '';
    loaded = false;

    seekdata = false;
    showError = false;
    scrollEnabled = false;
    private rowAccessProperty = 'text';
    private allTableData;
    private tabsData;
    public filteredColumns = [];
    public animationState;
    public previousTableData = [];
    public currentTableData = [];

    ngOnInit() {
        this.noMinHeight = false;
        this.seekdata = false;
        if ((this.outerArr !== undefined) && (this.outerArr.length !== 0)) {

            this.currentTableData = this.outerArr;
            this.allTableData = this.outerArr;
            if (this.tabFilterProperty) {
                this.tabsData = _(this.allTableData)
                    .map((row: any) => {
                        return row[this.tabFilterProperty];
                    })
                    .filter((row) => {
                        return !!row[this.rowAccessProperty];
                    })
                    .uniqBy((row) => {
                        return row[this.rowAccessProperty];
                    })
                    .value();

                for (let index = 0; index < this.tabsData.length; index++ ) {
                    this.tabsData[index]['displayName'] = this.refactorFieldsService.getDisplayNameForAKey(this.tabsData[index]['text'].toLowerCase()) || this.tabsData[index]['text'];
                }
            }
            this.restrictShownColumns(this.columnWhiteList);

            for (let column_index = 0; column_index < this.allColumns.length; column_index++) {
                this.sortArr[column_index] = {
                    'showUp': undefined,
                    'direction': 0
                };
            }

            this.loaded = true;

        } else {
            this.seekdata = true;
            this.showError = true;

            if (this.showGenericMessage === true) {
                this.errorMessage = 'apiResponseError';
            } else {

                if (this.searchTextValues === '') {
                    this.errorMessage = this.parentName;
                } else {
                    this.errorMessage = 'dataTableMessage';
                }
            }
        }
    }

    ngOnChanges(changes: SimpleChanges) {

        const graphDataChange = changes['allColumns'];
        const dataChange = changes['outerArr'];
        const errorChange = changes['showGenericMessage'];
        const errorMsg = changes['parentName'];
        const errorValueChange = changes['errorValue'];
        const searchTextChnage = changes['searchTextValues'];

        if (dataChange) {
            const cur = JSON.stringify(dataChange.currentValue);
            const prev = JSON.stringify(dataChange.previousValue);
            if ((cur !== prev) && (this.allColumns)) {
                this.ngOnInit();
            }
        }

        if (graphDataChange) {
            const cur = JSON.stringify(graphDataChange.currentValue);
            const prev = JSON.stringify(graphDataChange.previousValue);
            if ((cur !== prev) && (this.allColumns)) {
                this.ngOnInit();
            }
        }

        if (errorChange) {
            const cur = JSON.stringify(errorChange.currentValue);
            const prev = JSON.stringify(errorChange.previousValue);
            if ((cur !== prev)) {
                this.ngOnInit();
            }
        }

        if (searchTextChnage) {
            const cur = JSON.stringify(searchTextChnage.currentValue);
            const prev = JSON.stringify(searchTextChnage.previousValue);
            if ((cur !== prev)) {
                this.ngOnInit();
            }
        }

        if (errorMsg) {
            const cur = errorMsg.currentValue;
            this.errorMessage = cur;
        }

        if (errorValueChange) {
            if (errorValueChange && (errorValueChange.currentValue === 0 || this.errorValue.currentValue === -1)) {
                this.currentTableData = [];
            }
        }

        this.isSeeMore();
    }

    changeTabSelection(event) {
        const tab = event.tab;
        this.tabSelected.emit(tab);
        if (event.direction === 'right') {
            this.animationState = event.direction;
        }
        this.previousTableData = this.currentTableData;
        if (tab) {
            const list = _.filter(this.allTableData, (row) => {
                return tab[this.rowAccessProperty] === row[this.tabFilterProperty][this.rowAccessProperty];
            });
            this.currentTableData = list;
        } else {
            this.currentTableData = this.allTableData;
        }
        this.scrollEnabled = true;
        setTimeout(() => {
            this.animationState = null;
        }, 400);
    }


    headerClicked(index, header) {

        this.rowIndex = -1;
        for (let i = 0; i < this.sortArr.length; i++) {
            if (i !== index) {
                this.sortArr[i].showUp = undefined;
                this.sortArr[i].direction = 0;
            } else {
                if (this.sortArr[i].direction === 0) {
                    this.sortArr[i].direction = 1;
                } else {
                    this.sortArr[i].direction = this.sortArr[i].direction * -1;
                }
                this.direction = this.sortArr[i].direction;
            }
        }
        this.indexSelected = index;
        this.headerColName = header;
    }

    tableRowClicked(rowDetails, index) {
        this.rowDetails = [];
        this.rowObj = {};
        this.rowObj = rowDetails;
        this.rowDetails = Object.keys(this.rowObj);
        this.rowIndex = index;
    }

    goToDetails(thisRow, thisCol) {
        const details = {
            row: '',
            col: ''
        };
        details.row = thisRow;
        details.col = thisCol;
        this.selectedRow.emit(details);
    }

    searchCalled(searchQuery) {
        this.searchRowTxt.emit(searchQuery);
        this.searchVal = searchQuery;
    }

    callCheckbox(i, row) {
        const data = {};
        data['index'] = i;
        data['data'] = row;
        this.cbClicked.emit(data);
    }

    triggerSearch() {
        this.noMinHeight = false;
        this.searchTriggerred.emit();
    }

    prevPage() {
        if (this.currentPointer > 0) {
            this.previousPageCalled.emit();
        }
    }

    nextPage() {
        if (this.lastPaginator < this.totalRows) {
            this.nextPageCalled.emit();
        }
    }

    isSeeMore() {
        try {
            setTimeout(() => {
                const a = document.getElementsByClassName('data-table-inner-content data-table-current');
                const b = document.getElementsByClassName('data-table-inner-wrap relative');

                if (a[0] && b[0]) {
                    if (a[0].clientHeight && b[0].clientHeight) {
                        if (a[0].clientHeight > b[0].clientHeight) {
                            this.scrollEnabled = false;
                        } else {
                            this.scrollEnabled = true;
                            if (this.currentTableData.length) {
                                this.noMinHeight = true;
                                this.noScrollDetected.emit(this.noMinHeight);
                            }
                        }
                    }
                }
            }, 20);

        } catch (e) {
            this.logger.log('error', 'js error - ' + e);
        }
    }

    restrictShownColumns(columnNames) {

        if (columnNames) {
            const list = _.filter(columnNames, (whiteListedColumn) => {
                return _.find(this.allColumns, (column) => {
                    return whiteListedColumn.toLowerCase() === column.toLowerCase();
                });
            });
            this.filteredColumns = list;
        } else {
            this.filteredColumns = this.allColumns;
        }
    }

    emitRowText(rowClickText) {
        this.rowClickText.emit(rowClickText);
    }

    menuClicked(row, menu) {
        const obj = {};
        obj['data'] = row;
        obj['type'] = menu;
        this.menuClick.emit(obj);
        row.dropDownShown = false;
    }

}
