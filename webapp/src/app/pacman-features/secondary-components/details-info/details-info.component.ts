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
import {RefactorFieldsService} from './../../../shared/services/refactor-fields.service';

@Component({
  selector: 'app-details-info',
  templateUrl: './details-info.component.html',
  styleUrls: ['./details-info.component.css']
})
export class DetailsInfoComponent implements OnInit {

  @Input() resource: any;
  @Input() headers: any;
  showHeader = false;
  showData = false;
  labels: any;
  dataArray: any= [];

  constructor(private refactorFieldsService: RefactorFieldsService) { }

  ngOnInit() {

    const keys = Object.keys(this.resource);
    this.dataArray = [];

    for (let i = 0; i < keys.length; i++ ) {
      let obj = {};
      if ((keys[i] === 'discoverydate') || (keys[i] === '_lastUpdated') || (keys[i] === 'firstdiscoveredon') || (keys[i] === 'launchtime')) {
          const dateValues = this.resource[keys[i]].split(' ');
          const discDate = dateValues[0];
          obj = {
            'name': keys[i],
            'values': discDate
          };
      } else if ((keys[i] === 'createdDate') || (keys[i] === 'modifiedDate') || (keys[i] === 'CreatedOn') || (keys[i] === 'ModifiedOn')) {
          const dateValues = this.resource[keys[i]].split('T');
          const discDate = dateValues[0];
          obj = {
            'name': keys[i],
            'values': discDate
          };
      } else {
          obj = {
            'name': keys[i],
            'values': this.resource[keys[i]]
          };
      }

      this.dataArray.push(obj);
    }

    const dataValue = this.dataArray;
    const refactoredService = this.refactorFieldsService;
    dataValue.map(function(data){
        data.name = refactoredService.getDisplayNameForAKey(data.name) || data.name;
        return data;
    });

    this.checkData(this.dataArray);

    this.headers = this.headers.toUpperCase();
    if (this.headers === 'FALSE') {
      this.showHeader = false;
    } else {
      this.showHeader = true;
    }

  }

  getVal(val) {
    if ((val.values || val.values === 0 || val.values === false) && JSON.stringify(val.values).match('href') && JSON.stringify(val.values).match('http')) {
      val.isLink = true;
      val.values = JSON.parse(val.values)[0].href;
    } else if (!JSON.stringify(val.values).match('href') && JSON.stringify(val.values).match('http')) {
      val.isLink = true;
    }
    return val.values;
  }

  checkData(data) {
    for (let i = 0; i < data.length; i++) {
      if ((data[i].values == null) || (data[i].values === '')) {
        this.dataArray.splice(i, 1);
      }
      if (data[i].name === 'nonDisplayableAttributes') {
        this.dataArray.splice(i, 1);
      }
    }
    this.showData = true;
  }

}
