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
import { ErrorHandlingService } from '../../shared/services/error-handling.service';

@Injectable()
export class TaggingComplianceService {
    constructor( @Inject(HttpService) private httpService: HttpService,
                private errorHandler: ErrorHandlingService) { }

    getTaggingCompliance(queryParams, taggingComplianceUrl, taggingComplianceMethod): Observable<any> {
        const url = taggingComplianceUrl;
        const method = taggingComplianceMethod;
        const payload = {};
        try {
          return Observable.combineLatest(
            this.httpService.getHttpResponse(url, method, payload, queryParams)
            .map(response => {
                try {
                    return this.massageData(response);
                } catch (error) {
                    this.errorHandler.handleJavascriptError(error);
                }
            })
          );
        } catch (error) {
            this.errorHandler.handleJavascriptError(error);
        }
    }

    getTaggingSummaryByTargetType(payload, taggingSummaryUrl, taggingSummaryMethod): Observable<any> {
        const queryParams = {};
        try {
          return this.httpService.getHttpResponse(taggingSummaryUrl, taggingSummaryMethod, payload, queryParams)
            .map(response => {
                try {
                    const data = response['data'].response;
                    data.sort(function(a, b) {
                        return b.untagged - a.untagged;     // For descending order
                    });
                    return data;
                } catch (error) {
                    this.errorHandler.handleJavascriptError(error);
                }
            });
        } catch (error) {
            this.errorHandler.handleJavascriptError(error);
        }
    }

    massageData(data): any {
        const finalObj = {};
        const tempData = data.response.untaggedList.slice();
        const finalData = [];
        const appTaggingStatus = [];
        const headerData = [];

        /* tableheaderdata is for table header */
        headerData.push('TagName');
        tempData.forEach(element => {
            const complianceInstance = [
                {title: 'topBlank', val: (100 - (element.compliancePercentage || 100) ) },
                {title: element.name, val: (element.compliancePercentage || 100)},
                {title: 'leftBlank', val: 100}
               ];
            const taggingStatus = {
                'AppName' : element.name,
                'AppDetails': [
                    {'CountType' : 'untagged', 'count' : parseInt(element.untagged, 10)},
                    {'CountType' : 'tagged', 'count' : parseInt(element.tagged, 10)}
                ]
            };
            finalData.push(complianceInstance);
            appTaggingStatus.push(taggingStatus);
        });
        appTaggingStatus[0].AppDetails.forEach((element , index) => {
            headerData.push(element.CountType);
        });
        appTaggingStatus.forEach(element => {
            element.AppDetails.forEach(details => {
                element[details.CountType] = details.count;
            });
        });
        finalObj['data'] = finalData;
        finalObj['percent'] = data.response.overallCompliance;
        finalObj['taggingStatus'] = {'header' : headerData, 'data' : appTaggingStatus};
        return finalObj;
    }
}
