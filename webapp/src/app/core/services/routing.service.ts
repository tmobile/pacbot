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

import { Injectable } from '@angular/core';
import { DomainMappingService } from './domain-mapping.service';
import { WorkflowService } from './workflow.service';
import { LoggerService } from '../../shared/services/logger.service';
import { Router } from '@angular/router';

@Injectable()
export class RoutingService {
    constructor(private domainMappingService: DomainMappingService,
        private workflowService: WorkflowService,
        private loggerService: LoggerService,
        private router: Router
    ) {}

    redirectToRespectiveLatestPath(moduleName, agAndDomain) {
        try {
          let url = '';
          let queryParamsToBePassed = {};

          const assetGroupAndDomain = {};
            assetGroupAndDomain['ag'] = agAndDomain['ag'];
            assetGroupAndDomain['domain'] = agAndDomain['domain'];

          const savedPath = this.workflowService.getPreviouslyOpenedPageInModule(moduleName);
          if (savedPath) {
            const queryParams = this.workflowService.getPreviouslyOpenedPageQueryParamsInModule(moduleName)
              ? JSON.parse(this.workflowService.getPreviouslyOpenedPageQueryParamsInModule(moduleName))
              : {};

            Object.assign(queryParams, assetGroupAndDomain);

            url = savedPath;
            queryParamsToBePassed = queryParams;
            const level = this.workflowService.getDetailsFromStorage();
            const newLevel = [];
            if (level['level0'] && level['level0'].length > 0) {
              for (let i = 0; i < level['level0'].length; i++) {
                if (level['level0'][i]['url'] === savedPath) {
                  break;
                } else {
                  newLevel.push(level['level0'][i]);
                }
              }
              this.workflowService.saveToStorage({level0: newLevel});
            }
          } else {

            const listOfContextualMenuItems = this.domainMappingService.getDashboardsApplicableForADomain(agAndDomain['domain'], moduleName);
            if (listOfContextualMenuItems.length > 0) {
              url = 'pl' + '/' + moduleName + '/' + listOfContextualMenuItems[0].route;
            } else {
              url = 'pl' + '/' + moduleName + '/';
            }
            queryParamsToBePassed = assetGroupAndDomain;
            this.clearPageLevel();
          }
          this.router.navigate([url], {queryParams: queryParamsToBePassed}).then(response => {
            // Clearig page levels.
          });
        } catch (error) {
            this.loggerService.log('error', 'js error - ' + error);
        }
      }

      clearPageLevel() {
        try {
          this.workflowService.clearAllLevels();
        } catch (error) {
          this.loggerService.log('error', 'js error - ' + error);
        }
      }
}
