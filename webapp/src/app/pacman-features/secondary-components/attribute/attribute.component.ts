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

import { Component, OnInit, Input } from '@angular/core';
import { LoggerService } from '../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../shared/services/error-handling.service';
import {ActivatedRoute, UrlSegment, Router} from '@angular/router';
import {UtilsService} from '../../../shared/services/utils.service';
import {WorkflowService} from '../../../core/services/workflow.service';

@Component({
  selector: 'app-attribute',
  templateUrl: './attribute.component.html',
  styleUrls: ['./attribute.component.css'],
  providers: [
    LoggerService,
    ErrorHandlingService
  ]
})
export class AttributeComponent implements OnInit {
  @Input() data: any;
  @Input() pageLevel: number;
  @Input() dataObj: any;
  urlToRedirect: any = '';

  dataShow = false;
  dataShowforAssets = false;
  showData = false;

  dataObjArray = [];
  constructor(
    private logger: LoggerService, private errorHandling: ErrorHandlingService, private router: Router,
    private activatedRoute: ActivatedRoute,
    private utilityService: UtilsService, private workflowService: WorkflowService) {
}

  ngOnInit() {
    this.urlToRedirect = this.router.routerState.snapshot.url;
    this.massageData(this.dataObj);
  }

/* Function for massaging the raw data into array */

  massageData(data) {
    let dataObjContainer = [];
    const keys = Object.keys(data);
    let obj = {};
    for (let i = 0; i < keys.length; i++) {
      obj = {
        'name': keys[i],
        'values': data[keys[i]]
      };

      dataObjContainer.push(obj);
    }

    dataObjContainer = this.testData(dataObjContainer);
    this.dataObjArray = dataObjContainer;
  }

/* Function for removing non-array values from the data
   and massaging the raw date as well*/

  testData(data) {
    try {

      let dateData;

      for (let i = 0; i < data.length; i++ ) {
        const listData = data[i].values;

        for (let j = 0; j < listData.length; j++) {
          if (!Array.isArray(listData[j].value)) {
            listData.splice(j, 1);
          }
          if (listData[j] !== undefined) {
            if ((listData[j].name === 'Load Date') || (listData[j].name === 'Instance create time') || (listData[j].name === 'Snapshot createtime') || (listData[j].name === 'latestrestorabletime') || (listData[j].name === 'createtime') || (listData[j].name.toLowerCase() === 'starttime')) {
              dateData = listData[j].value;
              dateData = this.utilityService.calculateDate(dateData);
              listData[j].value = [dateData];
            }
            if ((listData[j].name.toLowerCase() === 'last vuln scan')) {
              dateData = listData[j].value;
              dateData = this.utilityService.calculateDateAndTime(dateData);
              listData[j].value = [dateData];
            }
            if (listData[j].name.toLowerCase() === 'list') {
              const value = JSON.parse(listData[j].value[0]);
              listData[j].value = value;
            }
          }
        }
      }

      return data;

    } catch (error) {

    }

  }

  /**
   * This function navigates the page mentioned  with a ruleID
   */
  navigatePage(event) {
    try {
          this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
          const clickText = event;
          const resourceType = 'volume';
          const resourceID = event;
          this.router.navigate(['../../', resourceType, resourceID], {relativeTo: this.activatedRoute});
    } catch (error) {
      this.logger.log('error', error);
    }
  }
  /* navigatePage function ends here */
}
