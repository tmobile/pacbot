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

@Component({
  selector: 'app-asset-summary',
  templateUrl: './asset-summary.component.html',
  styleUrls: ['./asset-summary.component.css']
})
export class AssetSummaryComponent implements OnInit {

  @Input() dataArray: any;
  dataValue: any;
  colors: any = ['#d40325', '#1c5066', '#000', '#000'];
  complianceColors = {
    'non-compliant' : '#D40325',
    'compliant'     : '#00B946',
    'intermediate'  : '#F75C03'
  };
  percent: any = [true, true, false, false];
  outOf: any= [false, false, false, false];
  text: any = [];
  data = {
    'overallCompliance': 100,
    'utilizationScore': 80,
    'running': 'Running',
    'instanceId': '34625-13',
    'runningGif': '../assets/icons/instanceState.gif'
  };

  constructor() { }

  ngOnInit() {
    this.processData(this.dataArray);
  }


  processData(data) {
    const response = [];
    let overallCompliance;
    let utilizationScore;
    let running;
    let instanceId;

    for (let i = 0; i < this.dataArray.length; i++) {

      if (this.dataArray[i].name === 'resourceId') {
        instanceId = {
         'value': this.dataArray[i].value,
         'text': 'Resource Id',
         'styling': {
            'cursor': 'pointer'
          }
        };
        this.colors[i] = '#000';
        this.percent[i] = false;
        this.text[i] = true;

        response.push(instanceId);
      }

      if (this.dataArray[i].name === 'Overall Compliance') {
        overallCompliance = {
         'value': parseInt(this.dataArray[i].value, 10),
         'text': 'Overall Compliance',
         'styling': {
            'cursor': ''
          }
        };
        this.colors[i] = this.dataArray[i].value === 100 ? this.complianceColors.compliant : (this.dataArray[i].value >= 50 ? this.complianceColors.intermediate : this.complianceColors['non-compliant']);
        this.percent[i] = true;
        this.text[i] = false;

        response.push(overallCompliance);
      }

      if (this.dataArray[i].name === 'Utilization Score') {
        utilizationScore = {
         'value': this.dataArray[i].value,
         'text': 'Utilization Score',
         'styling': {
            'cursor': ''
          }
        };
        this.percent[i] = false;
        this.outOf[i] = true;
        this.text[i] = false;

        response.push(utilizationScore);
      }

      if (this.dataArray[i].name === 'statename') {
        if (this.dataArray[i].value === 'running') {
          running = {
           'value': this.dataArray[i].value,
           'text': 'Instance State',
           'image': '../assets/icons/instanceState.gif',
           'styling': {
              'cursor': 'pointer'
            }
          };
        } else {
          running = {
           'value': this.dataArray[i].value,
           'text': 'Instance State',
           'image': '../assets/icons/Terminated.svg',
           'styling': {
              'cursor': 'pointer'
            }
          };
        }

        this.colors[i] = '#000';
        this.percent[i] = false;
        this.text[i] = true;

        response.push(running);
      }

    }

    this.dataValue = {
      'response': response
    };

  }

}
