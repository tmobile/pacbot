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

import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { AssetGroupObservableService } from '../../core/services/asset-group-observable.service';
import { DataCacheService } from '../../core/services/data-cache.service';
import { OverallComplianceService } from '../../pacman-features/services/overall-compliance.service';
import { environment } from '../../../environments/environment';
import { LoggerService } from './../../shared/services/logger.service';
import { ActivatedRoute, Router, NavigationExtras } from '@angular/router';
import { RefactorFieldsService } from '../../shared/services/refactor-fields.service';
import { AssetTilesService } from '../../core/services/asset-tiles.service';

@Component({
  selector: 'app-domain-overlay',
  templateUrl: './domain-overlay.component.html',
  styleUrls: ['./domain-overlay.component.css'],
  providers: [OverallComplianceService]
})

export class DomainOverlayComponent implements OnInit, OnDestroy {

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private assetGroupObservableService: AssetGroupObservableService,
    private dataStore: DataCacheService,
    private overallComplianceService: OverallComplianceService,
    private logger: LoggerService,
    private refactorFieldsService: RefactorFieldsService,
    private assetTileService: AssetTilesService
  ) {
    this.assetGroupSubscription = this.assetGroupObservableService
      .getAssetGroup()
      .subscribe(assetGroupName => {
        try {
          this.selectedAssetGroup = assetGroupName;
          this.updateComponent();
        } catch (error) {
          this.logger.log('error', 'js error - ' + error);
        }
      });
  }

  private assetGroupSubscription: Subscription;
  private subscriptionToDomain: Subscription;

  errorVal = 0;
  selectedAssetGroup = '';
  errorMsg = 'Unable to fetch data';
  dataArr: any = [];
  apiCount = 0;
  assetGroupName;
  assetGroupList;

  ngOnInit() {}

  updateComponent() {
    this.errorVal = 0;
    this.dataArr = [];
    this.apiCount = 0;
    this.errorMsg = 'Unable to fetch data';
    this.getData();
  }

  getData() {
    this.assetTileService
      .getAssetGroupDisplayName(this.selectedAssetGroup)
      .subscribe(
        response => {
          this.assetGroupName = response as string;
        },
        error => {
          this.logger.log(
            'error',
            'error in getting asset group display name - ' + error
          );
        }
      );

    this.assetTileService
      .getListOfDomainsAssociatedWithAssetGroup(this.selectedAssetGroup)
      .subscribe(
        result => {
          const url = environment.overallCompliance.url;
          const method = environment.overallCompliance.method;

          const listOfDomains = (result as string).split('~');

          if (!listOfDomains.length) {
            this.errorVal = -1;
            this.errorMsg = 'No domain found.';
          }

          for (let i = 0; i < listOfDomains.length; i++) {
            try {
              const queryParams = {
                ag: this.selectedAssetGroup,
                domain: listOfDomains[i]
              };

              this.subscriptionToDomain = this.overallComplianceService
                .getOverallCompliance(queryParams, url, method, true)
                .subscribe(
                  response => {
                    try {
                      let overallPercent;
                      this.apiCount++;
                      const dataObj = response[0].distribution;
                      const dataObjKeys = Object.keys(dataObj);
                      const currentArr = [];
                      for (let j = 0; j < dataObjKeys.length; j++) {
                        if (dataObjKeys[j] !== 'overall') {
                          const currentObj = {
                            key: dataObjKeys[j],
                            displayName:
                              this.refactorFieldsService.getDisplayNameForAKey(
                                dataObjKeys[j].toLowerCase()
                              ) || dataObjKeys[j],
                            value: dataObj[dataObjKeys[j]]
                          };
                          currentArr.push(currentObj);
                        } else {
                          overallPercent = dataObj[dataObjKeys[j]];
                        }
                      }

                      const finalObj = {
                        name: listOfDomains[i],
                        data: currentArr,
                        overallPercentage: overallPercent
                      };

                      this.dataArr.push(finalObj);

                      if (this.apiCount === listOfDomains.length) {
                        this.errorVal = 1;
                      }
                    } catch (e) {
                      this.logger.log('error', e);
                      this.errorVal = -1;
                      this.errorMsg = 'Oops! Something went wrong.';
                    }
                  },

                  error => {
                    this.logger.log('error', error);
                    this.errorVal = -2;
                    this.errorMsg =
                      'Oh oh! An error occured while we were fetching your data.';
                    this.apiCount++;
                    if (this.apiCount === listOfDomains.length) {
                      this.errorVal = 1;
                    }
                  }
                );
            } catch (error) {
              this.logger.log('error', error);
              this.errorVal = -1;
              this.errorMsg = 'Oops! Something went wrong.';
            }
          }
        },
        error => {
          this.logger.log(
            'error',
            'error in getting list of domains - ' + error
          );
          this.errorVal = -1;
          this.errorMsg = 'Oops! Something went wrong.';
        }
      );
  }

  changeDomain(domainName) {
    const currentAssetGroup = this.dataStore.getCurrentSelectedAssetGroup();
    const currentDomain = this.dataStore.getCurrentSelectedDomain(
      currentAssetGroup
    );

    if (domainName !== currentDomain) {
      const self = this;
      this.router.navigate(['/pl/compliance/compliance-dashboard'], {
        relativeTo: this.activatedRoute,
        queryParams: { domain: domainName },
        queryParamsHandling: 'merge'
      });
      setTimeout(() => {
        self.closeStatsModal();
      }, 150);
    } else {
      this.closeStatsModal();
    }
  }

  closeStatsModal(value?) {
    this.router.navigate(
      [
        {
          outlets: {
            modalBGMenu: null
          }
        }
      ],
      {
        relativeTo: this.activatedRoute.parent,
        queryParamsHandling: 'merge'
      }
    );
  }

  ngOnDestroy() {
    this.assetGroupSubscription.unsubscribe();
    this.subscriptionToDomain.unsubscribe();
  }
}
