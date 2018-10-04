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

/**
 * Created by adityaagarwal on 12/10/17.
 */
import { Observable } from 'rxjs/Rx';
import { Injectable, Inject } from '@angular/core';
import 'rxjs/add/operator/toPromise';
import { HttpService } from '../../shared/services/http-response.service';
import { RefactorFieldsService } from '../../shared/services/refactor-fields.service';

@Injectable()
export class OverallComplianceService {

    constructor(private refactorFieldsService: RefactorFieldsService,
                @Inject(HttpService) private httpService: HttpService) { }

    getOverallCompliance(queryParams, overallComplainceUrl, overallComplainceMethod, noMassage?): Observable<any> {

      const url = overallComplainceUrl;
      const method = overallComplainceMethod;
      const payload = {};
        try {
          return Observable.combineLatest(
            this.httpService.getHttpResponse(url, method, payload, queryParams)
            .map(response => this.massageData(response, noMassage))
            .catch(this.handleError)
          );
        } catch (error) {
            this.handleError(error);
        }
    }

    handleError(error: any): Promise<any> {
        return Promise.reject(error.message || error);
    }

    massageData(data, noMassage): any {
        if (!noMassage) {
          const finalObj = {};
          const newObj = {};
          const tempData = Object.assign(data.distribution);
          const finalData = [];
          const overallPercent = Math.round(tempData['overall']);
          delete tempData['overall'];

          const overallCompArr = Object.keys(tempData);
          let complianceInstance;
        for (let i = 0; i < overallCompArr.length; i++) {
          if (tempData[overallCompArr[i]] > 100) {
            complianceInstance = [
            {title: 'topBlank', val: 0 },
            {title: this.refactorFieldsService.getDisplayNameForAKey(overallCompArr[i].toLowerCase()) || overallCompArr[i], val: 100},
            {title: 'leftBlank', val: 100}
           ];
          } else {
            if (overallCompArr.length === 1) {
              complianceInstance = [
              {title: 'topBlank', val: Math.round((100 - parseInt(tempData[overallCompArr[i]], 10) * 1 )) },
              {title: this.refactorFieldsService.getDisplayNameForAKey(overallCompArr[i].toLowerCase()) || overallCompArr[i], val: Math.round(parseInt(tempData[overallCompArr[i]], 10) * 1)},
              {title: 'leftBlank', val: 0}
             ];
            } else {
              complianceInstance = [
              {title: 'topBlank', val: Math.round(100 - parseInt(tempData[overallCompArr[i]], 10)) },
              {title: this.refactorFieldsService.getDisplayNameForAKey(overallCompArr[i].toLowerCase()) || overallCompArr[i], val: Math.round(parseInt(tempData[overallCompArr[i]], 10))},
              {title: 'leftBlank', val: 100}
             ];
            }
          }

          finalData.push(complianceInstance);

          }
          finalObj['data'] = finalData;
          finalObj['percent'] = overallPercent;
          return finalObj;
        } else {
          return data;
        }
    }
}
