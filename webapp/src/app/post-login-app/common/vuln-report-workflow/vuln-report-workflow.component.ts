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

import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CONFIGURATIONS } from './../../../../config/configurations';

@Component({
  selector: 'app-vuln-report-workflow',
  templateUrl: './vuln-report-workflow.component.html',
  styleUrls: ['./vuln-report-workflow.component.css']
})
export class VulnReportWorkflowComponent implements OnInit {

  config;
  logoName;
  workflowData;
  @Output() closeWf = new EventEmitter();

  constructor() {
    this.config = CONFIGURATIONS;
  }

  ngOnInit() {

    if (this.config && this.config.required && this.config.required.APP_NAME) {
      this.logoName = this.config.required.APP_NAME.toLowerCase() + '-color-text-logo.svg';
    } else {
      this.logoName = 'application-logo.svg';
    }

    this.workflowData = [
      {
        title: 'ASSET COLLECTION',
        leftTxt: [ 'AWS & Azure Inventory APIs' ],
        rightTxt: 'ServiceNow \'Servers\' Table'
      },
      {
        title: 'MAP ASSETS TO APPLICATION',
        leftTxt: ['\'Application\' Tag Available for Cloud Assets'],
        rightTxt: '\'Business Service\' Field in \'Servers\' Table'
      },
      {
        title: 'APPLICATION TO OWNER MAPPING',
        leftTxt: ['Jira Workload API'],
        rightTxt: 'EAL Spreadsheet (Temporarily)'
      },
      {
        title: 'VULNERABILITY ASSESSMENT',
        leftTxt: [ 'Results Pushed to QualysGuard by Agent'],
        rightTxt: 'Results Pushed to QualysGuard by Agent or Remote Scan'
      },
      {
        title: 'EXTRACT VULNERABILITY DATA',
        leftTxt: ['QualysGuard API'],
        rightTxt: 'QualysGuard API'
      },
      {
        title: 'REPORT SCOPE',
        leftTxt: ['Exclude Unscanned Assets That Live 7 Days or Less', 'Exclude Cloud Provider Managed Assets'],
        rightTxt: 'N/A'
      }
    ];
  }

  closeWorkflow() {
    this.closeWf.emit();
  }

}
