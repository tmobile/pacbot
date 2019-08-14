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
import { ActivatedRoute, Router } from '@angular/router';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { DomainTypeObservableService } from '../../../core/services/domain-type-observable.service';
import { Subscription } from 'rxjs/Subscription';
import { WorkflowService } from '../../../core/services/workflow.service';
import { LoggerService } from '../../../shared/services/logger.service';

@Component({
  selector: 'app-policy-violation-desc',
  templateUrl: './policy-violation-desc.component.html',
  styleUrls: ['./policy-violation-desc.component.css'],
  providers: [LoggerService]
})

export class PolicyViolationDescComponent implements OnInit {
  @Input() violationData;
  @Input() autofixData;
  @Input() pageLevel: number;
  urlToRedirect = '';
  private subscriptionToAssetGroup: Subscription;
  private domainSubscription: Subscription;
  selectedAssetGroup: string;
  selectedDomain: string;
  accordionData: any;
  labelData: any;
  showAccordion = false;
  testData;
  public agAndDomain = {};
  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private assetGroupObservableService: AssetGroupObservableService,
    private domainObservableService: DomainTypeObservableService,
    private workflowService: WorkflowService,
    private logger: LoggerService
  ) {
    this.subscriptionToAssetGroup = this.assetGroupObservableService
      .getAssetGroup()
      .subscribe(assetGroupName => {
        this.selectedAssetGroup = assetGroupName;
        this.agAndDomain['ag'] = this.selectedAssetGroup;
      });
    // domain subscription
    this.domainSubscription = this.domainObservableService
      .getDomainType()
      .subscribe(domain => {
        this.selectedDomain = domain;
        this.agAndDomain['domain'] = this.selectedDomain;
      });
    // processData for accordion
  }

  ngOnInit() {
    this.urlToRedirect = this.router.routerState.snapshot.url;
    this.processDataForAccordion(this.violationData);
  }
  /**
   * @func processDataForAccordion
   * @param data data to process
   * @desc processes the policy violation data(mainly the accordion data)
   */

  processDataForAccordion(data) {
    try {

      let checkifJsonString;
      this.accordionData = [];
      this.labelData = [];
      const dataToBeChecked = data.violationDetails[0];
      // check if there are nested obj inside
      Object.keys(dataToBeChecked).forEach(element => {
        if (dataToBeChecked[element]) {
          checkifJsonString = dataToBeChecked[element].search('{');
          // .search returns returns -1 if string doesn't exists
          if (!(checkifJsonString === -1)) {
            const arrayValues = [];
            const innerObj = JSON.parse(dataToBeChecked[element]);
            Object.keys(innerObj).forEach(elementinner => {
              const eachObj = {
                labelName: elementinner.replace(/_/g, ' '), // remove the '_' from the key name before pushing,
                labelCount: innerObj[elementinner]
              };
              arrayValues.push(eachObj);
            });

            const dataToPushForAccorDion = {
              labelName: element.replace(/_/g, ' '), // remove the '_' from the key name before pushing
              labelCount: null,
              values: arrayValues,
              isAccordion: true
            };
            this.labelData.push(dataToPushForAccorDion);
          } else {
            const dataToPush = {
              labelName: element.replace(/_/g, ' '), // remove the '_' from the key name before pushing
              labelCount: dataToBeChecked[element],
              values: null,
              isAccordion: false
            };
            this.labelData.push(dataToPush);
          }
        }
      });

    } catch (e) {
      this.logger.log('error', e);
    }
  }

  /**
   * @func closeNestedAccordion
   * @desc to navigate to different link
   */

  /**
   * @func closeAccordion
   * @param event
   * @desc need to add code to hide accordion on click of outside
   */

  closeAccordion(event) {
    // code to hide the accordion
    // this.showAccordion = false
  }

  closePopup() {
    this.showAccordion = false;
  }

  /**
   * @func navigateTo
   * @param destination (link to go)
   * @param id1 ruleid or resoueceid
   * @desc to navigate to different link
   */

  navigateTo(destination, id1?, id2?) {
    try {
      this.workflowService.addRouterSnapshotToLevel(
        this.router.routerState.snapshot.root
      );
      if (destination === 'asset details') {
        const resourceId = id1;
        const resourceType = id2;
        this.router.navigate(
          ['../../../', 'assets', 'assets-details', resourceType, resourceId],
          {
            relativeTo: this.activatedRoute,
            queryParamsHandling: 'merge'
          }
        );
      } else if (destination === 'policy knowledgebase details') {
        const ruleId = id1;
        this.router.navigate(['../../policy-knowledgebase-details', ruleId], {
          relativeTo: this.activatedRoute,
          queryParamsHandling: 'merge'
        });
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }
}
