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

import { Component, OnInit, Input, OnChanges, SimpleChanges, Output, EventEmitter } from '@angular/core';

@Component({
    selector: 'app-list-table',
    templateUrl: './list-table.component.html',
    styleUrls: ['./list-table.component.css']
})
export class ListTableComponent implements OnInit, OnChanges {
    @Input() tableData: any;
    @Input() tableHeaderData: any;
    @Input() searchText: any = '';
    @Input() orderByConfig;
    @Input() orderByProperty;
    @Output() error: EventEmitter<any> = new EventEmitter();
    @Output() navigatePage: EventEmitter<any> = new EventEmitter();
    public tableBodyData: any;

    constructor() {
    }

    ngOnInit() {
        this.finalTableData();
    }

    ngOnChanges(changes: SimpleChanges) {
        try {
          const graphDataChange = changes['tableData'];
          if (graphDataChange) {
            const cur  = JSON.stringify(graphDataChange.currentValue);
            const prev = JSON.stringify(graphDataChange.previousValue);
            if ((cur !== prev) && (this.tableData)) {
                this.finalTableData();
            }
          }
        } catch (error) {
          this.error.emit('jsError');
        }
      }

    finalTableData() {
        this.tableBodyData = this.tableData;
    }

    addTest(object) {
        if (object) {
            let count = 0;
            for (const i of object.severityinfo) {
                count += object.severityinfo[i].count;
            }
            return count;
        }
    }
    instructParentToNavigate (appName, colName ) {
        const emitData = {
            'Rowname': appName,
            'Colname': colName
        };
        this.navigatePage.emit(emitData);
    }
}
