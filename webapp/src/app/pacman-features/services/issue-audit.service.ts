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
 * Created by sauravdutta on 20/11/17.
 */

import { Injectable, Inject } from '@angular/core';
import { Observable } from 'rxjs/Rx';
import 'rxjs/add/operator/toPromise';
import { HttpService } from '../../shared/services/http-response.service';

@Injectable()
export class IssueAuditService {

    constructor( @Inject(HttpService) private httpService: HttpService) { }

    getHoursValue: any;
    getMinutesValue: any;
    getSecondsValue: any;
    getDateValue: any;
    getMonthValue: any;
    getMethod: any;
    getYearValue: any;

    getData(payload, IssueAuditUrl, IssueAuditMethod): Observable<any> {

        const url = IssueAuditUrl;
        const method = IssueAuditMethod;
        const queryParams = {};
        this.getMethod = IssueAuditMethod;

        try {
            return Observable.combineLatest(
                this.httpService.getHttpResponse(url, method, payload, queryParams)
                    .map(response => this.massageData(response) )
                    .catch(this.handleError)
            );
        } catch (error) {
            this.handleError(error);
        }
    }


    handleError(error: any): Observable<any> {
        return Observable.throw(error.message || error);
    }

    massageData(data): any {
        const parsedData = data;
        const response = parsedData.data.response;
        for (let i = 0; i < response.length; i++) {
            const auditdate = response[i].auditdate;
            const dateValue = new Date(auditdate);
            this.getHoursValue = dateValue.getHours();
            if (this.getHoursValue < 10) {
                this.getHoursValue = '0' + this.getHoursValue;
            }
            this.getMinutesValue = dateValue.getMinutes();
            if (this.getMinutesValue < 10) {
                this.getMinutesValue = '0' + this.getMinutesValue;
            }
            this.getSecondsValue = dateValue.getSeconds();
            if (this.getSecondsValue < 10) {
                this.getSecondsValue = '0' + this.getSecondsValue;
            }
            this.getDateValue = dateValue.getDate();
            if (this.getDateValue < 10) {
                this.getDateValue = '0' + this.getDateValue;
            }
            this.getMonthValue = dateValue.getMonth();
            this.getMonthValue = this.getMonthValue + 1;
            if (this.getMonthValue < 10) {
                this.getMonthValue = '0' + this.getMonthValue;
            }
            this.getYearValue = dateValue.getFullYear();
            const fullValue = this.getHoursValue + ':' + this.getMinutesValue + ':' + this.getSecondsValue + ' ' + this.getMonthValue + '-' + this.getDateValue + '-' + this.getYearValue;
            parsedData.data.response[i].auditdate = fullValue;
        }
        return parsedData;
    }

}
