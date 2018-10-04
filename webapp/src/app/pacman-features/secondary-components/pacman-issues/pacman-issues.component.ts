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

import { Component, OnInit, ElementRef, ViewChild, OnDestroy, Input } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { ActivatedRoute, Router } from '@angular/router';
import { PacmanIssuesService } from '../../services/pacman-issues.service';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { AutorefreshService } from '../../services/autorefresh.service';
import { environment } from './../../../../environments/environment';
import { ErrorHandlingService } from '../../../shared/services/error-handling.service';
import { UtilsService } from '../../../shared/services/utils.service';
import { WorkflowService } from '../../../core/services/workflow.service';
import { RefactorFieldsService } from '../../../shared/services/refactor-fields.service';
import { DomainTypeObservableService } from '../../../core/services/domain-type-observable.service';

@Component({
  selector: 'app-pacman-issues',
  templateUrl: './pacman-issues.component.html',
  styleUrls: ['./pacman-issues.component.css'],
  providers: [ ErrorHandlingService, PacmanIssuesService, AutorefreshService]
})

export class PacmanIssuesComponent implements OnInit, OnDestroy {

  @ViewChild('countval') varcount: ElementRef;
  pacmanIssues: any;
  subscriptionToAssetGroup: Subscription;
  subscriptionDomain: Subscription;
  selectedAssetGroup: string;
  errorMessages;
  dataSubscriber;
  durationParams: any;
  autoRefresh: boolean;
  transit: any;
  widthValue: any = 0;
  loaded: boolean;
  notloadedfull: boolean;
  borderSet: boolean;
  error = false;
  showdata = false;
  errorMessage: any;
  seekdata = false;
  routeTo  = 'issue-listing';
  pacmanCategories: any = [];
  selectedDomain: any;

  private autorefreshInterval;
  @Input() pageLevel: number;
  private urlToRedirect;


  constructor(private pacmanIssuesService: PacmanIssuesService,
              private assetGroupObservableService: AssetGroupObservableService,
              private autorefreshService: AutorefreshService,
              private errorHandling: ErrorHandlingService,
              private activatedRoute: ActivatedRoute,
              private router: Router,
              private utils: UtilsService,
              private workflowService: WorkflowService,
              private refactorFieldsService: RefactorFieldsService,
              private domainObservableService: DomainTypeObservableService) {

              this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(
                assetGroupName => {
                    this.selectedAssetGroup = assetGroupName;
              });

              this.subscriptionDomain = this.domainObservableService.getDomainType().subscribe(domain => {
                   this.selectedDomain = domain;
                   this.updateComponent();
              });
   }

   ngOnInit() {
        this.urlToRedirect = this.router.routerState.snapshot.url;
        this.durationParams = this.autorefreshService.getDuration();
        this.durationParams = parseInt(this.durationParams, 10);
        this.autoRefresh = this.autorefreshService.autoRefresh;

        const afterLoad = this;
        if (this.autoRefresh !== undefined) {
            if ((this.autoRefresh === true ) || (this.autoRefresh.toString() === 'true')) {
                this.autorefreshInterval = setInterval(() => {
                    afterLoad.transit = false;
                    afterLoad.getPacmanIssues();
                }, this.durationParams);
            }
        }
    }

    /* Function to repaint component */
    updateComponent() {

        /* All functions variables which are required to be set for component to be reloaded should go here */
        if (this.dataSubscriber) {
          this.dataSubscriber.unsubscribe();
        }
        this.loaded = false;
        this.showdata = false;
        this.error = false;
        this.notloadedfull = false;
        this.borderSet = false;
        this.transit = false;

        this.getData();
    }


    navigateTo() {
      this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
      if (this.routeTo !== undefined) {
        const eachParams = {'include_exempt': 'no'};
        const newParams = this.utils.makeFilterObj(eachParams);
        this.router.navigate(['../', this.routeTo], {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
      }
    }

    navigateToCritical() {

      this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
      if (this.routeTo !== undefined) {
        const eachParams = {'severity.keyword': 'critical', 'include_exempt': 'no'};
        const newParams = this.utils.makeFilterObj(eachParams);
        this.router.navigate(['../', this.routeTo], {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
      }
    }

    /* Function to get Data */
    getData() {

        /* All functions to get data should go here */
        this.getPacmanIssues();
    }

    getPacmanIssues() {

      if (this.dataSubscriber) { this.dataSubscriber.unsubscribe(); }

      const queryParams = {
          'ag': this.selectedAssetGroup,
          'domain': this.selectedDomain
      };

      const pacmanIssuesUrl = environment.pacmanIssues.url;
      const pacmanIssuesMethod = environment.pacmanIssues.method;

      this.widthValue = 0;

      try {
        this.dataSubscriber = this.pacmanIssuesService.getData(queryParams, pacmanIssuesUrl, pacmanIssuesMethod).subscribe(
        response => {
          try {
              this.pacmanIssues = response;

              this.pacmanCategories = [];
              for ( let i = 0; i < this.pacmanIssues.category.length; i++) {
                const obj = {
                  displayName: this.refactorFieldsService.getDisplayNameForAKey(Object.keys(this.pacmanIssues.category[i])[0].toLowerCase()) || Object.keys(this.pacmanIssues.category[i])[0],
                  key: Object.keys(this.pacmanIssues.category[i])[0],
                  value: this.pacmanIssues.category[i][Object.keys(this.pacmanIssues.category[i])[0]]
                };
                this.pacmanCategories.push(obj);
              }

              this.getCalculatedValues();
              this.loaded = true;
              this.showdata = true;
              this.error = false;
          } catch (e) {
              this.errorMessage = this.errorHandling.handleJavascriptError(e);
              this.getErrorValues();
          }
        },
        error => {
          this.errorMessage = error;
          this.getErrorValues();
        });
      } catch (error) {
        this.errorMessage = this.errorHandling.handleJavascriptError(error);
        this.getErrorValues();
      }
   }

   // assign error values...

   getErrorValues(): void {
     this.loaded = true;
     this.error = true;
     this.seekdata = true;
   }

   // calculate the required values to plot.....

   getCalculatedValues(): void {

        const x = this;

          if ((x.pacmanIssues[`valuePercent`] !== 100) && (x.pacmanIssues[`valuePercent`] !== 0)) {
            x.pacmanIssues[`valuePercent`] = x.pacmanIssues[`valuePercent`] + 3;
            x.notloadedfull = true;
          }

          if (x.pacmanIssues[`valuePercent`] !== 0) {
            x.borderSet = true;
          }

          x.widthValue = x.pacmanIssues[`valuePercent`];

          x.transit = true;

   }

   navigateToKey(key) {
      this.workflowService.addRouterSnapshotToLevel(this.router.routerState.snapshot.root);
      if (this.routeTo !== undefined) {
        const eachParams = {'ruleCategory.keyword': key, 'include_exempt': 'no'};
        const newParams = this.utils.makeFilterObj(eachParams);
        this.router.navigate(['../', this.routeTo], {relativeTo: this.activatedRoute, queryParams: newParams, queryParamsHandling: 'merge'});
      }
   }

   ngOnDestroy() {
    try {
      if (this.subscriptionToAssetGroup) { this.subscriptionToAssetGroup.unsubscribe(); }
      if (this.dataSubscriber) {this.dataSubscriber.unsubscribe(); }
      if (this.subscriptionDomain) {this.subscriptionDomain.unsubscribe(); }
      clearInterval(this.autorefreshInterval);
    } catch (error) {
        this.errorMessage = this.errorHandling.handleJavascriptError(error);
        this.getErrorValues();
    }
  }

}
