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

import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LoggerService } from '../../../shared/services/logger.service';
import { ErrorHandlingService } from '../../../shared/services/error-handling.service';
import { UtilsService } from '../../../shared/services/utils.service';
import { WorkflowService } from '../../../core/services/workflow.service';

@Component({
  selector: 'app-state-table',
  templateUrl: './state-table.component.html',
  styleUrls: ['./state-table.component.css']
})
export class StateTableComponent implements OnInit, OnChanges {

  @Input() data: any;
  @Input() headers: any;
  @Input() top: any;
  @Input() bottom: any;
  count;
  title;
  showMoreDirectors: number;
  showMoreSponsors: number;
  viewDirectorsRequired = false;
  viewSponsorsRequired = false;

  @Input() pageLevel: number;
  private urlToRedirect: string;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private logger: LoggerService, private errorHandling: ErrorHandlingService,
    private utils: UtilsService,
    private workflowService: WorkflowService
  ) { }

  ngOnChanges(changes: SimpleChanges) {
    try {
      const DataChange = changes['data'];
      if (DataChange) {
        const cur  = JSON.stringify(DataChange.currentValue);
        const prev = JSON.stringify(DataChange.previousValue);
        if ((cur !== prev) && (this.data)) {
          this.ngOnInit();
        }
      }
    } catch (error) {

    }
  }

  ngOnInit() {
    this.urlToRedirect = this.router.routerState.snapshot.url;
    this.processData();
  }

  processData() {
    this.count = this.data.response.length;
    this.title = this.headers;
    let dataObj;

    if (this.bottom) {

      if (this.data.response.length < 5) {
        this.viewSponsorsRequired = false;
        this.showMoreSponsors = this.data.response.length;
      } else {
        this.viewSponsorsRequired = true;
        this.showMoreSponsors = 5;
      }

      for ( let i = 0; i < this.data.response.length; i++) {
        if (this.data.response[i][`Executive Sponsor`] !== undefined) {
          dataObj = {
            'nonCompliantNumber': this.data.response[i].nonCompliantNumber,
            'execSponsor': this.data.response[i][`Executive Sponsor`]
          };
        } else {
          dataObj = {
            'nonCompliantNumber': this.data.response[i].nonCompliantNumber,
            'execSponsor': this.data.response[i].execSponsor
          };
        }

        this.data.response[i] = dataObj;
      }
    } else {
      if (this.data.response.length < 5) {
        this.viewDirectorsRequired = false;
        this.showMoreDirectors = this.data.response.length;
      } else {
        this.viewDirectorsRequired = true;
        this.showMoreDirectors = 5;
      }
    }
  }

   /**
   * This function navigates the page mentioned in the routeTo variable with a querypareams
   */

  navigatePage(data1, data2, data3?) {
    try {
      this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
      const secondaryQueryparams = data1.toLowerCase();
      const apiTarget = {'TypeAsset' : 'patchable'};
      const appType = data2;
      if (data3) {
        const appName = data3;
      }

      if ( (secondaryQueryparams === 'director') ) {
        const eachParams = {'patched': false, 'director' : appType, 'application': data3};
          let newParams = this.utils.makeFilterObj(eachParams);
          newParams = Object.assign(newParams, apiTarget);
          this.router.navigate(['../../', 'assets' , 'asset-list'] , {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
      } else if (secondaryQueryparams === 'executivesponsor') {
        const eachParams = {'patched': false , 'executiveSponsor' : appType};
          let newParams = this.utils.makeFilterObj(eachParams);
          newParams = Object.assign(newParams, apiTarget);
          this.router.navigate(['../../', 'assets' , 'asset-list'] , {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
      }
    } catch (error) {
      this.logger.log('error', error);
    }
  }
  /* navigatePage function ends here */

  showMore() {
    this.showMoreDirectors = this.data.response.length;
  }

  showLess() {
    this.showMoreDirectors = 5;
  }

  showMoreSponsorsList() {
    this.showMoreSponsors = this.data.response.length;
  }

  showLessSponsorsList() {
    this.showMoreSponsors = 5;
  }

}
